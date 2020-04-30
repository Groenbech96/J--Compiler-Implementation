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
                parameter.type().mustMatchExpected(line(), Type.THROWABLE);
            }
        }

        finalBlock = finalBlock.analyze(context);

        return this;
    }

    @Override
    public void codegen(CLEmitter output) {

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
