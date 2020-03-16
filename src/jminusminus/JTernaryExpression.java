package jminusminus;

/**
 * The AST node for a ternary expression 'bool ? a : b'
 */

public class JTernaryExpression extends JExpression {

    private JExpression condition;
    private JExpression ifTrue;
    private JExpression ifFalse;

    /**
     *
     * @param line the line this code is found at
     * @param condition the boolean condition for this ternary
     * @param ifTrue, the result if the condition is true
     * @param ifFalse, the condition if the condition is false
     */
    public JTernaryExpression(int line, JExpression condition, JExpression ifTrue, JExpression ifFalse) {
        super(line);
        this.condition = condition;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    /**
     * Analyze the condition and ensures it returns bool
     * Analyzes the possible return cases and ensures they are the same types
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */
    public JExpression analyze(Context context) {
        condition = condition.analyze(context);
        ifTrue = ifTrue.analyze(context);
        ifFalse = ifFalse.analyze(context);

        if (condition.type() != Type.BOOLEAN)
            JAST.compilationUnit.reportSemanticError(line(), "Ternary condition must evaluate to bool, evaluated to " + condition.type());

        if (ifTrue.type() != ifFalse.type())
            JAST.compilationUnit.reportSemanticError(line(), "Ternary cases must evaluate to same type, evaluated to " + ifTrue.type() + " and " + ifFalse.type());

        type = ifTrue.type();

        return this;
    }

    public void codegen(CLEmitter output) {
        // TODO: this
        System.err.println("Error in code generation for ternary");
    }

    @Override
    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JTernaryExpression line=\"%d\" type=\"%s\">\n", line(),  ((type == null) ? "" : type.toString()));
            p.indentRight();
                p.printf("<Condition>\n");
                    p.indentRight();
                    condition.writeToStdOut(p);
                    p.indentLeft();
                p.printf("</Condition>\n");
                p.printf("<IfTrue>\n");
                    p.indentRight();
                    ifTrue.writeToStdOut(p);
                    p.indentLeft();
                p.printf("</IfTrue>\n");
                p.printf("<IfFalse>\n");
                    p.indentRight();
                    ifFalse.writeToStdOut(p);
                    p.indentLeft();
                p.printf("</IfFalse>\n");
            p.indentLeft();
        p.printf("</JTernaryExpression>\n");
    }
}
