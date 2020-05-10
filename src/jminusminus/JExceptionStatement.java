package jminusminus;

import java.util.ArrayList;

public class JExceptionStatement extends JStatement {

    private JBlock tryBlock;
    private ArrayList<JBlock> catchBlocks;
    private ArrayList<ArrayList<JFormalParameter>> parametersList;

    private JBlock finalBlock;

    /**
     * Construct an AST node for a statement given its line number.
     *
     * @param line line in which the statement occurs in the source file.
     */
    protected JExceptionStatement(int line, JBlock tryblock, ArrayList<JBlock> catchBlocks, ArrayList<ArrayList<JFormalParameter>> parameters, JBlock finalBlock) {
        super(line);
        this.tryBlock = tryblock;
        this.catchBlocks = catchBlocks;
        this.parametersList = parameters;
        this.finalBlock = finalBlock;

        if(this.catchBlocks == null){
            this.catchBlocks = new ArrayList<>();
        }
    }

    @Override
    public JAST analyze(Context context) {
        tryBlock = tryBlock.analyze(context);

        for (JBlock block : catchBlocks) {
            block = block.analyze(context);
        }

        for (ArrayList<JFormalParameter> parameters : parametersList) {
            for (JFormalParameter parameter : parameters) {
                parameter = (JFormalParameter) parameter.analyze(context);
                parameter.type().resolve(context).mustInheritFrom(line(), Type.THROWABLE);
            }
        }
        if(parametersList.size() != catchBlocks.size()){
            JAST.compilationUnit.reportSemanticError(line, "Mismatch in size of catch parameters and catch blocks");
        }
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
        output.addLabel(startLabel);
        tryBlock.codegen(output);
        output.addLabel(endLabel);


        for (int i = 0; i< catchBlocks.size(); i++){
            String catchLabel = "catchNr"+i+"Line"+line;
            JBlock catchBlock = catchBlocks.get(i);
            output.addLabel(catchLabel);
            ArrayList<JFormalParameter> catchParameters = parametersList.get(i);
            for (JFormalParameter catchParameter : catchParameters) {
                output.addExceptionHandler(startLabel,endLabel,catchLabel,catchParameter.name());
                catchParameter.codegen(output);
            }
            catchBlock.codegen(output);
        }

        if(finalBlock != null){
            output.addExceptionHandler(startLabel,endLabel,"finally"+line, null);
            finalBlock.codegen(output);
        }

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
        if(!parametersList.isEmpty()) {

            for (int counter = 0; counter < parametersList.size(); counter++) {
                p.printf("<CatchBlock>\n");
                p.indentRight();
                for (JFormalParameter param : parametersList.get(counter)) {
                    p.printf("<CatchParameter>\n");
                    p.indentRight();
                    param.writeToStdOut(p);
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
