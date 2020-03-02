package jminusminus;

import static jminusminus.CLConstants.DCONST_0;
import static jminusminus.CLConstants.DCONST_1;

public class JLIteralDouble extends JExpression {
    /**
     * String representation of the int.
     */
    private String text;

    /**
     * Construct an AST node for an int literal given its line number and string
     * representation.
     *
     * @param line line in which the literal occurs in the source file.
     * @param text string representation of the literal.
     */

    public JLIteralDouble(int line, String text) {
        super(line);
        this.text = text;
    }

    /**
     * Analyzing an int literal is trivial.
     *
     * @param context context in which names are resolved (ignored here).
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        type = Type.DOUBLE;
        return this;
    }

    /**
     * Generating code for an int literal means generating code to push it onto
     * the stack.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        double i = Double.parseDouble(text);
        if (i == 0) {
            output.addNoArgInstruction(DCONST_0);
        } else if (i == 1){
            output.addNoArgInstruction(DCONST_1);
        } else {
            // TODO: code-gen
        }
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JLiteralDouble line=\"%d\" type=\"%s\" " + "value=\"%s\"/>\n",
                line(), ((type == null) ? "" : type.toString()), text);
    }

}
