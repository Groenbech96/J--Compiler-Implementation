// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for a binary expression. A binary expression has an operator and
 * two operands: a lhs and a rhs.
 */

abstract class JBinaryExpression extends JExpression {

    /**
     * The binary operator.
     */
    protected String operator;

    /**
     * The lhs operand.
     */
    protected JExpression lhs;

    /**
     * The rhs operand.
     */
    protected JExpression rhs;

    /**
     * Construct an AST node for a binary expression given its line number, the
     * binary operator, and lhs and rhs operands.
     *
     * @param line     line in which the binary expression occurs in the source file.
     * @param operator the binary operator.
     * @param lhs      the lhs operand.
     * @param rhs      the rhs operand.
     */

    protected JBinaryExpression(int line, String operator, JExpression lhs,
                                JExpression rhs) {
        super(line);
        this.operator = operator;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    void sidesMustMatchNumerical() {
        if (!isNumericalType(lhs.type()))
            JAST.compilationUnit.reportSemanticError(line, "LHS %s is not numerical", lhs.type());
        lhs.type().mustMatchExpected(line(), lhs.type());
        rhs.type().mustMatchExpected(line(), lhs.type());
    }

    boolean isNumericalType(Type type) {
        return type == Type.INT || type == Type.DOUBLE;
    }

    boolean sidesAreSameNumerical() {
        return isNumericalType(lhs.type()) && lhs.type() == rhs.type();
    }

    int div() {
        if (type == Type.INT) return IDIV;
        if (type == Type.DOUBLE) return DDIV;
        JAST.compilationUnit.reportSemanticError(line, "LHS %s is unknown for div", type.toString());
        return -1;
    }

    int mul() {
        if (type == Type.INT) return IMUL;
        if (type == Type.DOUBLE) return DMUL;
        JAST.compilationUnit.reportSemanticError(line, "LHS %s is unknown for mul", type.toString());
        return -1;
    }

    int add() {
        if (type == Type.INT) return IADD;
        if (type == Type.DOUBLE) return DADD;
        JAST.compilationUnit.reportSemanticError(line, "LHS %s is unknown for add", type.toString());
        return -1;
    }

    int rem() {
        if (type == Type.INT) return IREM;
        if (type == Type.DOUBLE) return DREM;
        JAST.compilationUnit.reportSemanticError(line, "LHS %s is unknown for rem", type.toString());
        return -1;
    }

    int sub() {
        if (type == Type.INT) return ISUB;
        if (type == Type.DOUBLE) return DSUB;
        JAST.compilationUnit.reportSemanticError(line, "LHS %s is unknown for sub", type.toString());
        return -1;
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JBinaryExpression line=\"%d\" type=\"%s\" "
                + "operator=\"%s\">\n", line(), ((type == null) ? "" : type
                .toString()), Util.escapeSpecialXMLChars(operator));
        p.indentRight();
        p.printf("<Lhs>\n");
        p.indentRight();
        lhs.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Lhs>\n");
        p.printf("<Rhs>\n");
        p.indentRight();
        rhs.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Rhs>\n");
        p.indentLeft();
        p.printf("</JBinaryExpression>\n");
    }

}

/**
 * The AST node for a plus (+) expression. In j--, as in Java, + is overloaded
 * to denote addition for numbers and concatenation for Strings.
 */

class JPlusOp extends JBinaryExpression {

    /**
     * Construct an AST node for an addition expression given its line number,
     * and the lhs and rhs operands.
     *
     * @param line line in which the addition expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JPlusOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "+", lhs, rhs);
    }

    /**
     * Analysis involves first analyzing the operands. If this is a string
     * concatenation, we rewrite the subtree to make that explicit (and analyze
     * that). Otherwise we check the types of the addition operands and compute
     * the result type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        lhs = lhs.analyze(context);
        rhs = rhs.analyze(context);
        if (lhs.type() == Type.STRING || rhs.type() == Type.STRING) {
            return (new JStringConcatenationOp(line, lhs, rhs))
                    .analyze(context);
        } else if (sidesAreSameNumerical()) {
            type = lhs.type();
        } else {
            type = Type.ANY;
            JAST.compilationUnit.reportSemanticError(line(),
                    "Invalid operand types for +");
        }
        return this;
    }

    /**
     * Any string concatenation has been rewritten as a JStringConcatenationOp
     * (in analyze()), so code generation here involves simply generating code
     * for loading the operands onto the stack and then generating the
     * appropriate add instruction.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        if (isNumericalType(type)) {
            lhs.codegen(output);
            rhs.codegen(output);
            output.addNoArgInstruction(add());
        }
    }

}

/**
 * The AST node for a subtraction (-) expression.
 */

class JSubtractOp extends JBinaryExpression {

    /**
     * Construct an AST node for a subtraction expression given its line number,
     * and lhs and rhs operands.
     *
     * @param line line in which the subtraction expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JSubtractOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "-", lhs, rhs);
    }

    /**
     * Analyzing the - operation involves analyzing its operands, checking
     * types, and determining the result type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        lhs = lhs.analyze(context);
        rhs = rhs.analyze(context);
        sidesMustMatchNumerical();
        type = lhs.type();
        return this;
    }

    /**
     * Generating code for the - operation involves generating code for the two
     * operands, and then the subtraction instruction.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        lhs.codegen(output);
        rhs.codegen(output);
        output.addNoArgInstruction(sub());
    }

}

/**
 * The AST node for a multiplication (*) expression.
 */

class JMultiplyOp extends JBinaryExpression {

    /**
     * Construct an AST for a multiplication expression given its line number,
     * and the lhs and rhs operands.
     *
     * @param line line in which the multiplication expression occurs in the
     *             source file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JMultiplyOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "*", lhs, rhs);
    }

    /**
     * Analyzing the * operation involves analyzing its operands, checking
     * types, and determining the result type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        lhs = lhs.analyze(context);
        rhs = rhs.analyze(context);
        sidesMustMatchNumerical();
        type = lhs.type();
        return this;
    }

    /**
     * Generating code for the * operation involves generating code for the two
     * operands, and then the multiplication instruction.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        lhs.codegen(output);
        rhs.codegen(output);
        output.addNoArgInstruction(mul());
    }

}

class JDivideOp extends JBinaryExpression {
    public JDivideOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "/", lhs, rhs);
    }

    @Override
    public JExpression analyze(Context context) {
        lhs = lhs.analyze(context);
        rhs = rhs.analyze(context);
        sidesMustMatchNumerical();
        type = lhs.type();
        return this;
    }

    @Override
    public void codegen(CLEmitter output) {
        lhs.codegen(output);
        rhs.codegen(output);
        output.addNoArgInstruction(div());
    }
}

class JRemainderOp extends JBinaryExpression {
    public JRemainderOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "%", lhs, rhs);
    }

    @Override
    public JExpression analyze(Context context) {
        lhs = lhs.analyze(context);
        rhs = rhs.analyze(context);
        sidesMustMatchNumerical();
        type = lhs.type();
        return this;
    }

    @Override
    public void codegen(CLEmitter output) {
        lhs.codegen(output);
        rhs.codegen(output);
        output.addNoArgInstruction(rem());
    }
}
