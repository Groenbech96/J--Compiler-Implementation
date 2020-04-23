// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import static jminusminus.CLConstants.*;

/**
 * The AST node for an assignment statement. This is an abtract class into which
 * we factor behavior common to all assignment operations.
 */

abstract class JAssignment extends JBinaryExpression {

    /**
     * Construct an AST node for an assignment operation.
     *
     * @param line     line in which the assignment operation occurs in the source
     *                 file.
     * @param operator the actual assignment operator.
     * @param lhs      the lhs operand.
     * @param rhs      the rhs operand.
     */

    public JAssignment(int line, String operator, JExpression lhs,
                       JExpression rhs) {
        super(line, operator, lhs, rhs);
    }

    int numericAsm(int intInstruction, int doubleInstruction) {
        if (type == Type.INT) return intInstruction;
        if (type == Type.DOUBLE) return doubleInstruction;
        JAST.compilationUnit.reportSemanticError(line(), "Illegal type " + type.toString());
        return -1;
    }

    int numericAdd() {
        if (type == Type.INT) return IADD;
        if (type == Type.DOUBLE) return DADD;
        JAST.compilationUnit.reportSemanticError(line(), "Illegal type for adding " + type.toString());
        return -1;
    }

    int numericSub() {
        if (type == Type.INT) return ISUB;
        if (type == Type.DOUBLE) return DSUB;
        JAST.compilationUnit.reportSemanticError(line(), "Illegal type for subtracting " + type.toString());
        return -1;
    }

    int numericMul() {
        if (type == Type.INT) return IMUL;
        if (type == Type.DOUBLE) return DMUL;
        JAST.compilationUnit.reportSemanticError(line(), "Illegal type for multiplying " + type.toString());
        return -1;
    }

    int numericDiv() {
        if (type == Type.INT) return IDIV;
        if (type == Type.DOUBLE) return DDIV;
        JAST.compilationUnit.reportSemanticError(line(), "Illegal type for dividing " + type.toString());
        return -1;
    }

    int numericRem() {
        if (type == Type.INT) return IDIV;
        if (type == Type.DOUBLE) return DDIV;
        JAST.compilationUnit.reportSemanticError(line(), "Illegal type for dividing " + type.toString());
        return -1;
    }

    Type checkNumericTypes(JExpression lhs, JExpression rhs, Type ... types) {

        for (Type t : types) {
            if (lhs.type().equals(t)) {
                rhs.type().mustMatchExpected(line(), t);
                return t;
            }
        }
        JAST.compilationUnit.reportSemanticError(line(),
                "Invalid lhs type for +=: " + lhs.type());
        return null;

    }


}

/**
 * The AST node for an assignment (=) expression. The = operator has two
 * operands: a lhs and a rhs.
 */

class JAssignOp extends JAssignment {

    /**
     * Construct the AST node for an assignment (=) expression given the lhs and
     * rhs operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  lhs operand.
     * @param rhs  rhs operand.
     */

    public JAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "=", lhs, rhs);
    }

    /**
     * Analyze the lhs and rhs, checking that types match, and set the result
     * type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {

        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Illegal lhs for assignment");
        } else {
            lhs = (JExpression) ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = (JExpression) rhs.analyze(context);
        rhs.type().mustMatchExpected(line(), lhs.type());
        type = rhs.type();
        if (lhs instanceof JVariable) {
            IDefn defn = ((JVariable) lhs).iDefn();
            if (defn != null) {
                // Local variable; consider it to be initialized now.
                ((LocalVariableDefn) defn).initialize();
            }
        }
        return this;
    }

    /**
     * Code generation for an assignment involves, generating code for loading
     * any necessary Lvalue onto the stack, for loading the Rvalue, for (unless
     * a statement) copying the Rvalue to its proper place on the stack, and for
     * doing the store.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        rhs.codegen(output);
        if (!isStatementExpression) {
            // Generate code to leave the Rvalue atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

/**
 * The AST node for a += expression. A += expression has two operands: a lhs and
 * a rhs.
 */

class JPlusAssignOp extends JAssignment {

    /**
     * Construct the AST node for a += expression given its lhs and rhs
     * operands.
     *
     * @param line line in which the assignment expression occurs in the source
     *             file.
     * @param lhs  the lhs operand.
     * @param rhs  the rhs operand.
     */

    public JPlusAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "+=", lhs, rhs);
    }

    /**
     * Analyze the lhs and rhs, rewrite rhs as lhs + rhs (string concatenation)
     * if lhs is a String, and set the result type.
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Illegal lhs for assignment");
            return this;
        } else {
            lhs = (JExpression) ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = (JExpression) rhs.analyze(context);

        checkNumericTypes(lhs, rhs, Type.INT, Type.DOUBLE, Type.STRING);

//        if (lhs.type().equals(Type.INT)) {
//            rhs.type().mustMatchExpected(line(), Type.INT);
//            type = Type.INT;
//        } else if (lhs.type().equals(Type.DOUBLE)) {
//            rhs.type().mustMatchExpected(line(), Type.DOUBLE);
//            type = Type.DOUBLE;
//        } else if (lhs.type().equals(Type.STRING)) {
//            rhs = (new JStringConcatenationOp(line, lhs, rhs)).analyze(context);
//            type = Type.STRING;
//        } else {
//            JAST.compilationUnit.reportSemanticError(line(),
//                    "Invalid lhs type for +=: " + lhs.type());
//        }
        return this;
    }

    /**
     * Code generation for += involves, generating code for loading any
     * necessary l-value onto the stack, for (unless a string concatenation)
     * loading the r-value, for (unless a statement) copying the r-value to its
     * proper place on the stack, and for doing the store.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        if (lhs.type().equals(Type.STRING)) {
            rhs.codegen(output);
        } else {
            ((JLhs) lhs).codegenLoadLhsRvalue(output);
            rhs.codegen(output);
            output.addNoArgInstruction(numericAsm(IADD, DADD));
        }
        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }

}

class JMinusAssignOp extends JAssignment {
    public JMinusAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "-=", lhs, rhs);
    }

    public JExpression analyze(Context context) {

        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Illegal lhs for assignment");
            return this;
        } else {
            lhs = (JExpression) ((JLhs) lhs).analyzeLhs(context);
        }

        rhs = (JExpression) rhs.analyze(context);
        type = checkNumericTypes(lhs, rhs, Type.INT, Type.DOUBLE);

        return this;

    }

    public void codegen(CLEmitter output) {

        // Load L-value onto stack
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        // Load R value for assignment
        ((JLhs) lhs).codegenLoadLhsRvalue(output);

        rhs.codegen(output);
        output.addNoArgInstruction(numericAsm(ISUB, DSUB));

        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack (x = y--)
            // (y-- should be treated as R-value, hence atop stack)
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);

    }
}

class JStarAssignOp extends JAssignment {
    public JStarAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "*=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        // Make sure that lhs is an actual lhs
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Illegal lhs for assignment");
            return this;
        } else {
            lhs = (JExpression) ((JLhs) lhs).analyzeLhs(context);
        }

        rhs = (JExpression) rhs.analyze(context);
        type = checkNumericTypes(lhs, rhs, Type.INT, Type.DOUBLE);

        return this;

    }

    public void codegen(CLEmitter output) {

        // Load L-value onto stack
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        // Load R value for assignment
        ((JLhs) lhs).codegenLoadLhsRvalue(output);

        rhs.codegen(output);
        output.addNoArgInstruction(numericAsm(IMUL, DMUL));

        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack (x = y--)
            // (y-- should be treated as R-value, hence atop stack)
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);

    }
}

class JDivideAssignOp extends JAssignment {
    public JDivideAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "/=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Illegal lhs for assignment");
            return this;
        } else {
            lhs = (JExpression) ((JLhs) lhs).analyzeLhs(context);
        }

        rhs = (JExpression) rhs.analyze(context);
        type = checkNumericTypes(lhs, rhs, Type.INT, Type.DOUBLE);

        return this;
    }

    public void codegen(CLEmitter output) {

        // Load L-value onto stack
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        // Load R value for assignment
        ((JLhs) lhs).codegenLoadLhsRvalue(output);

        rhs.codegen(output);
        output.addNoArgInstruction(numericAsm(IDIV, DDIV));

        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack (x = y--)
            // (y-- should be treated as R-value, hence atop stack)
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }
}

class JRemainderAssignOp extends JAssignment {
    public JRemainderAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "%=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Illegal lhs for assignment");
            return this;
        } else {
            lhs = (JExpression) ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = (JExpression) rhs.analyze(context);
        type = checkNumericTypes(lhs, rhs, Type.INT, Type.DOUBLE);

        return this;
    }

    public void codegen(CLEmitter output) {

        // Load L-value onto stack
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        // Load R value for assignment
        ((JLhs) lhs).codegenLoadLhsRvalue(output);

        rhs.codegen(output);
        output.addNoArgInstruction(numericAsm(IREM, DREM));

        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack (x = y--)
            // (y-- should be treated as R-value, hence atop stack)
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }
}

class JRShiftAssignOp extends JAssignment {
    public JRShiftAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, ">>=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        if (!(lhs instanceof JLhs)) {
            JAST.compilationUnit.reportSemanticError(line(),
                    "Illegal lhs for assignment");
            return this;
        } else {
            lhs = (JExpression) ((JLhs) lhs).analyzeLhs(context);
        }
        rhs = (JExpression) rhs.analyze(context);
        type = checkNumericTypes(lhs, rhs, Type.INT);

        return this;
    }

    public void codegen(CLEmitter output) {
        // Load L-value onto stack
        ((JLhs) lhs).codegenLoadLhsLvalue(output);
        // Load R value for assignment
        ((JLhs) lhs).codegenLoadLhsRvalue(output);

        rhs.codegen(output);
        output.addNoArgInstruction(numericAsm(ISHR, NOP));

        if (!isStatementExpression) {
            // Generate code to leave the r-value atop stack (x = y--)
            // (y-- should be treated as R-value, hence atop stack)
            ((JLhs) lhs).codegenDuplicateRvalue(output);
        }
        ((JLhs) lhs).codegenStore(output);
    }
}

class JRShiftZeroAssignOp extends JAssignment {
    public JRShiftZeroAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, ">>>=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        return null;
    }

    public void codegen(CLEmitter output) {
    }
}

class JLShiftAssignOp extends JAssignment {
    public JLShiftAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "<<=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        return null;
    }

    public void codegen(CLEmitter output) {
    }
}

class JAndAssignOp extends JAssignment {
    public JAndAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "&=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        return null;
    }

    public void codegen(CLEmitter output) {
    }
}

class JOrAssignOp extends JAssignment {
    public JOrAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "|=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        return null;
    }

    public void codegen(CLEmitter output) {
    }
}

class JXorAssignOp extends JAssignment {
    public JXorAssignOp(int line, JExpression lhs, JExpression rhs) {
        super(line, "^=", lhs, rhs);
    }

    public JExpression analyze(Context context) {
        return null;
    }

    public void codegen(CLEmitter output) {
    }
}