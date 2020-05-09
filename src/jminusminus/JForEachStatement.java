// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.GOTO;

/**
 * The AST node for a for-statement.
 */

class JForEachStatement extends JStatement {

    /**
     * The for expression
     */

    private JVariable counterName;
    private JStatementExpression setForCurrentIndex;
    private JLessThanOp condition;
    private JVariableDeclaration counter;
    private JVariableDeclarator parameter;
    private JVariableDeclaration parameterDecl;
    private JVariable array;

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
     * @param parameter the formal parameter on the left side of the for-each expression
     * @param array     the array parameter on the right side of the for-each expression
     * @param body      the body
     */

    public JForEachStatement(int line, JVariableDeclarator parameter, JVariable array, JStatement body) {
        super(line);
        this.array = array;
        this.body = body;

        //Artificial nodes
        String stringVar = "0" + parameter.name();
        this.counterName = new JVariable(line, stringVar);

        this.parameter = parameter;
        this.parameterDecl = new JVariableDeclaration(line, new ArrayList<>(),
                new ArrayList<JVariableDeclarator>() {{ add(parameter); }});

        this.counter = new JVariableDeclaration(line, new ArrayList<>(),
                new ArrayList<JVariableDeclarator>() {{
                    new JVariableDeclarator(line, stringVar, Type.INT, new JLiteralInt(line, "0"));
                }});

        JFieldSelection length = new JFieldSelection(line, array, "length");
        this.condition = new JLessThanOp(line, counterName, length);

        JExpression indexExpression = new JArrayExpression(line, array, counterName);
        JExpression assign = new JAssignOp(line, new JVariable(line, parameter.name()), indexExpression);
        assign.isStatementExpression = true;
        this.setForCurrentIndex = new JStatementExpression(line, assign);
    }

    /**
     * Analysis involves analyzing the condition expression, ensuring it is a boolean
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */
    public JForEachStatement analyze(Context context) {
        this.context = new LocalContext(context);
        this.counter = (JVariableDeclaration) this.counter.analyze(this.context);
        this.parameterDecl = (JVariableDeclaration) this.parameterDecl.analyze(this.context);
        this.condition = (JLessThanOp) this.condition.analyze(this.context);
        this.setForCurrentIndex = (JStatementExpression) this.setForCurrentIndex.analyze(this.context);

        body = (JStatement) body.analyze(this.context);
        array = (JVariable) array.analyze(this.context);

        LocalVariableDefn localVariable = (LocalVariableDefn) array.iDefn();
        if (!localVariable.type().isArray())
            localVariable.type().mustMatchExpected(line(), Type.ENUMERABLE);

        localVariable.type().componentType().mustMatchExpected(line(), parameter.type().resolve(this.context));

        return this;
    }

    /**
     * Generate code for the for loop.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        String test = output.createLabel();
        String out = output.createLabel();

        //init_iterator.codegen(output);
        counter.codegen(output);

        // Branch out of the loop on the test condition
        // being false
        output.addLabel(test);
        condition.codegen(output, out, false);
        setForCurrentIndex.codegen(output);

        // Codegen body
        body.codegen(output);

        int offset = ((LocalVariableDefn) counterName.iDefn()).offset();
        output.addIINCInstruction(offset, 1);

        // Unconditional jump back up to test
        output.addBranchInstruction(GOTO, test);

        // The label below and outside the loop
        output.addLabel(out);
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
