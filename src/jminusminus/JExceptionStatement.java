package jminusminus;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JExceptionStatement extends JStatement {

    private JBlock tryBlock;
    private ArrayList<JBlock> catchBlocks;
    ArrayList<ArrayList<Type>> catchParameters;
    ArrayList<String> catchNames;
    ArrayList<JVariable> catchVariables;

    private JBlock finalBlock;

    /**
     * Construct an AST node for a statement given its line number.
     *
     * @param line line in which the statement occurs in the source file.
     */
    protected JExceptionStatement(int line, JBlock tryblock, ArrayList<JBlock> catchBlocks, ArrayList<ArrayList<Type>> parameters, ArrayList<String> catchNames, JBlock finalBlock) {
        super(line);
        this.tryBlock = tryblock;
        this.catchBlocks = catchBlocks;
        this.catchParameters = parameters;
        this.catchNames = catchNames;
        this.finalBlock = finalBlock;

        if(this.catchBlocks == null){
            this.catchBlocks = new ArrayList<>();
        }
        this.catchVariables = new ArrayList<>();
    }

    @Override
    public JAST analyze(Context context) {
        tryBlock = tryBlock.analyze(context);


        ArrayList<JBlock> resolvedBlocks = new ArrayList<>();
        for (JBlock block : catchBlocks) {
            resolvedBlocks.add(block.analyze(context));
        }
        catchBlocks.clear();
        catchBlocks.addAll(resolvedBlocks);

        if(catchParameters.size() != catchBlocks.size()){
            JAST.compilationUnit.reportSemanticError(line, "Mismatch in size of catch parameters and catch blocks");
        }

        AtomicInteger i = new AtomicInteger();
        catchParameters = catchParameters.stream().map(cp -> {
            cp = cp.stream().map(p -> p.resolve(context)).collect(Collectors.toCollection(ArrayList::new));
            for (Type parameter : cp) {
                parameter.mustMatchOrInheritFrom(line(), Type.THROWABLE);
            }
            Type commonType;
            if(cp.size() > 1){
                commonType = Type.typeFor(Exception.class);
            } else{
                commonType = cp.get(0);
            }
            commonType = commonType.resolve(context);
            JBlock currentBlock = catchBlocks.get(i.get());
            String currentCatchParameter = catchNames.get(i.get());

            LocalVariableDefn localVariableDefn = new LocalVariableDefn(commonType, currentBlock.getNextOffset());
            localVariableDefn.initialize();
            currentBlock.addEntry(currentCatchParameter,localVariableDefn);
            JVariable variable = new JVariable(line, currentCatchParameter);
            variable = (JVariable) variable.analyzeLhs( currentBlock.getContext());
            this.catchVariables.add(variable);
            i.getAndIncrement();
            return cp;
        }).collect(Collectors.toCollection(ArrayList::new));


        if(catchBlocks.size() == 0 && finalBlock == null){
            JAST.compilationUnit.reportSemanticError(line, "A try statement must have minimum 1 catch or final block");
        }
        if(finalBlock != null){
            finalBlock = finalBlock.analyze(context);
        }

        return this;
    }

    @Override
    public void codegen(CLEmitter output) {
        String startLabel = "tryStart"+line;
        String endLabel = "tryEnd"+line;
        String endCatch = output.createLabel();
        output.addLabel(startLabel);
        tryBlock.codegen(output);
        String finallyLabel = "finally"+line;
        output.addBranchInstruction(CLConstants.GOTO, endCatch);
        output.addLabel(endLabel);
        JAST.compilationUnit.reportSemanticError(line, "Exception codeGen, amount of catchBLocks: %d", catchBlocks.size());

        for (int i = 0; i< catchBlocks.size(); i++){
            String catchLabel = "catchNr"+i+"Line"+line;
            JBlock catchBlock = catchBlocks.get(i);
            output.addLabel(catchLabel);
            ArrayList<Type> parameters = this.catchParameters.get(i);
            for (Type catchParameter : parameters) {
                output.addExceptionHandler(startLabel,endLabel,catchLabel,catchParameter.jvmName());
            }
            this.catchVariables.get(i).codegenStore(output);
            catchBlock.codegen(output);
            output.addBranchInstruction(CLConstants.GOTO, endCatch);
        }

        output.addLabel(endCatch);
        String endMethodLabel = output.createLabel();
        if(finalBlock != null){
            output.addLabel(finallyLabel);
            output.addExceptionHandler(startLabel,endLabel,finallyLabel, null);
            finalBlock.codegen(output);
            output.addBranchInstruction(CLConstants.GOTO, endMethodLabel);
        }

        output.addLabel(endMethodLabel);

    }

    @Override
    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JExceptionStatement line=\"%d\">\n", line());
        p.indentRight();
        p.printf("<TryBlock>\n");
        p.indentRight();
        tryBlock.writeToStdOut(p);
        p.indentLeft();
        p.printf("</TryBlock>\n");
        if(!catchParameters.isEmpty()) {

            for (int counter = 0; counter < catchParameters.size(); counter++) {
                p.printf("<CatchBlock>\n");
                p.indentRight();
                for (Type param : catchParameters.get(counter)) {
                    p.printf("<CatchParameter>\n");
                    p.indentRight();
                    p.print(param.jvmName()+ "\n");
                    p.indentLeft();
                    p.printf("</CatchParameter>\n");
                }
                catchBlocks.get(counter).writeToStdOut(p);
                p.indentLeft();
                p.printf("</CatchBlock>\n");
            }
        }
        if (finalBlock != null) {
            p.printf("<FinallyBlock>\n");
            p.indentRight();
            finalBlock.writeToStdOut(p);
            p.indentLeft();
            p.printf("</FinallyBlock>\n");
        }
        p.indentLeft();
        p.printf("</JExceptionStatement>\n");
    }
}
