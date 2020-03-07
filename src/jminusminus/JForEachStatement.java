// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

/**
 * The AST node for a for-statement.
 */

class JForEachStatement extends JStatement {

    /**
     * The for expression
     */

    private JFormalParameter parameter;
    private JVariable array;

    /**
     * The body.
     */
    private JStatement body;

    /**
     * Construct an AST node for a while-statement given its line number, the
     * test expression, and the body.
     *
     * @param line      line in which the while-statement occurs in the source file.
     * @param parameter the formal parameter on the left side of the for-each expression
     * @param array     the array parameter on teh right side of the for-each expression
     * @param body      the body
     */

    public JForEachStatement(int line, JFormalParameter parameter, JVariable array, JStatement body) {
        super(line);
        this.parameter = parameter;
        this.array = array;
        this.body = body;
    }

    /**
     * Analysis involves analyzing the condition expression, ensuring it is a boolean
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */
    public JForEachStatement analyze(Context context) {
        body = (JStatement)body.analyze(context);
        return this;
    }

    /**
     * Generate code for the for loop.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JForEachStatement line=\"%d\">\n", line());
        p.indentRight();

        p.printf("<FormalParameter>\n");
        p.indentRight();
        parameter.writeToStdOut(p);
        p.indentLeft();
        p.printf("</FormalParameter>\n");

        p.printf("<Array>\n");
        p.indentRight();
        array.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Array>\n");

        p.printf("<Body>\n");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");

        p.indentLeft();
        p.printf("</JForEachStatement>\n");
    }

}
