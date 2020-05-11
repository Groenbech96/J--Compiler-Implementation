// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import java.util.PrimitiveIterator;

import static jminusminus.CLConstants.GOTO;

/**
 * The AST node for a for-statement.
 */

class JForEachStatement extends JStatement {

    /**
     * The for expression
     */
    private JVariable freeVariable;
    private JStatementExpression counterGetNext;
    private JLessThanOp counterHasNext;
    private JVariableDeclaration counterDecl;
    private JVariableDeclarator parameter;
    private JVariableDeclaration parameterDecl;
    private JVariable array;
    private boolean usingIterator;
    private JStatementExpression iteratorGetNextAndIncrement;
    private JMessageExpression iteratorHasNext;
    private JVariableDeclaration iteratorDecl;

    /**
     * The body.
     */
    private JStatement body;


    /**
     * The new context (built in analyze()) represented by this for statement
     */
    private LocalContext context;

    /**
     * Construct an AST node for a while-statement given its line number, the
     * test expression, and the body.
     *
     * @param line      line in which the while-statement occurs in the source file.
     * @param parameter the parameter on the left side of the for-each expression
     * @param array     the array parameter on the right side of the for-each expression
     * @param body      the body
     */

    public JForEachStatement(int line, JVariableDeclarator parameter, JVariable array, JStatement body) {
        super(line);
        this.parameter = parameter;
        this.array = array;
        this.body = body;
        this.usingIterator = false;

        // Variable declaration for parameter on left side of for-each loop
        this.parameter.useDefaultInitializer();
        JVariable localParameter = new JVariable(line, this.parameter.name());
        this.parameterDecl = JVariableDeclaration.single(line, parameter);

        // Artificial variable either holding Iterator or integer counter
        this.freeVariable = new JVariable(line, "0" + this.parameter.name());

        JMessageExpression getIterator = new JMessageExpression(line, array, "iterator", new ArrayList<>());
        JMessageExpression iteratorNext = new JMessageExpression(line, freeVariable, "next", new ArrayList<>());
        JAssignOp iteratorAssign = new JAssignOp(line, localParameter, iteratorNext);
        iteratorAssign.isStatementExpression = true;

        this.iteratorHasNext = new JMessageExpression(line, freeVariable, "hasNext", new ArrayList<>());
        this.iteratorGetNextAndIncrement = new JStatementExpression(line, iteratorAssign);
        this.iteratorDecl = JVariableDeclaration.single(line,
                new JVariableDeclarator(line, freeVariable.name(), Type.ITERATOR, getIterator));

        JFieldSelection length = new JFieldSelection(line, array, "length");
        JExpression indexExpression = new JArrayExpression(line, array, freeVariable);
        JAssignOp assign = new JAssignOp(line, localParameter, indexExpression);
        assign.isStatementExpression = true;

        this.counterHasNext = new JLessThanOp(line, freeVariable, length);
        this.counterGetNext = new JStatementExpression(line, assign);
        this.counterDecl = JVariableDeclaration.single(line,
                new JVariableDeclarator(line, freeVariable.name(), Type.INT, new JLiteralInt(line, "0")));
    }

    /**
     * Analysis involves analyzing the condition expression, ensuring it is a boolean
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */
    public JForEachStatement analyze(Context context) {
        this.context = new LocalContext(context);

        this.parameterDecl = (JVariableDeclaration) this.parameterDecl.analyze(this.context);
        // TODO: figure out how to handle when this doesnt return JVariable
        JExpression analyzedArray = this.array.analyze(this.context);

        if (analyzedArray.type().isArray()) {
            analyzedArray.type().componentType().mustMatchOrInheritFrom(line(), this.parameter.type().resolve(this.context));
            this.counterDecl = (JVariableDeclaration) this.counterDecl.analyze(this.context);
            this.counterHasNext = (JLessThanOp) this.counterHasNext.analyze(this.context);
            this.counterGetNext = (JStatementExpression) this.counterGetNext.analyze(this.context);
        } else {
            analyzedArray.type().mustMatchOrInheritFrom(line(), Type.ITERABLE);
            this.usingIterator = true;
            this.iteratorDecl = (JVariableDeclaration) this.iteratorDecl.analyze(this.context);
            this.iteratorHasNext = (JMessageExpression) this.iteratorHasNext.analyze(this.context);
            System.out.println(analyzedArray.type());
            this.iteratorGetNextAndIncrement = (JStatementExpression) this.iteratorGetNextAndIncrement.analyze(this.context);
        }

        this.body = (JStatement) this.body.analyze(this.context);
        return this;
    }

    /**
     * Generate code for the for loop.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        String startLabel = output.createLabel();
        String endLabel = output.createLabel();
        parameterDecl.codegen(output);

        if (usingIterator) {
            // Declare variable
            iteratorDecl.codegen(output);

            // Label start of loop
            output.addLabel(startLabel);

            iteratorHasNext.codegen(output, endLabel, false);
            iteratorGetNextAndIncrement.codegen(output);

        } else {
            // Declare variable
            counterDecl.codegen(output);

            // Label start of loop
            output.addLabel(startLabel);

            counterHasNext.codegen(output, endLabel, false);
            counterGetNext.codegen(output);
            int offset = ((LocalVariableDefn) freeVariable.iDefn()).offset();
            output.addIINCInstruction(offset, 1);
        }

        body.codegen(output);
        output.addBranchInstruction(GOTO, startLabel);
        output.addLabel(endLabel);
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JForEachStatement line=\"%d\">\n", line());
        p.indentRight();

        p.printf("<Parameter>\n");
        p.indentRight();
        parameter.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Parameter>\n");

        p.printf("<Array>\n");
        p.indentRight();
        p.printf("%s\n", array.name());
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
