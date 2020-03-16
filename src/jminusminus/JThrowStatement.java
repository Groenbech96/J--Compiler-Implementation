package jminusminus;

public class JThrowStatement extends JStatement {

    private JExpression expression;

    /**
     * Construct an AST node for a statement given its line number.
     *
     * @param line line in which the statement occurs in the source file.
     */
    protected JThrowStatement(int line, JExpression expression) {
        super(line);
        this.expression = expression;
    }

    @Override
    public JAST analyze(Context context) {
        return null;
    }

    @Override
    public void codegen(CLEmitter output) {

    }

    @Override
    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JThrowStatement line=\"%d\">\n", line());
        p.indentRight();
        expression.writeToStdOut(p);
        p.indentLeft();
        p.printf("</JThrowStatement>\n");

    }
}
