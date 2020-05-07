// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.*;

/**
 * The AST node for a for-statement.
 */

class JForStatement extends JStatement {

    /**
     * The for expression
     */

    private JVariableDeclaration initVariableDecls;
    private ArrayList<JStatement> initStatements;
    private JExpression condition;
    private ArrayList<JStatement> updateStatements;

    /**
     * The body.
     */
    private JStatement body;

    /**
     * Construct an AST node for a while-statement given its line number, the
     * test expression, and the body.
     *
     * @param line              line in which the while-statement occurs in the source file.
     * @param initVariableDecls the initial variable declarations
     * @param initStatements    the initial statements
     * @param condition         the condition
     * @param updateStatements  the update statements
     * @param body              the body.
     */

    public JForStatement(int line, JVariableDeclaration initVariableDecls, ArrayList<JStatement> initStatements,
                         JExpression condition, ArrayList<JStatement> updateStatements, JStatement body) {
        super(line);
        this.initVariableDecls = initVariableDecls;
        this.initStatements = initStatements;
        this.condition = condition;
        this.updateStatements = updateStatements;
        this.body = body;
    }

    /**
     * Analysis involves analyzing the condition expression, ensuring it is a boolean
     *
     * @param context context in which names are resolved.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */
    public JForStatement analyze(Context context) {
        initVariableDecls = (JVariableDeclaration) initVariableDecls.analyze(context);
        initStatements = (ArrayList<JStatement>) initStatements.stream()
                .map(statement -> (JStatement) statement.analyze(context)).collect(toList());

        condition = condition.analyze(context);

        updateStatements = (ArrayList<JStatement>) updateStatements.stream()
                .map(statement -> (JStatement) statement.analyze(context)).collect(toList());

        body = (JStatement) body.analyze(context);

        condition.type().mustMatchExpected(line(), Type.BOOLEAN);
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
        p.printf("<JForStatement line=\"%d\">\n", line());
        p.indentRight();

        if (initVariableDecls != null) {
            p.printf("<InitialVariableDeclarations>");
            p.indentRight();
            initVariableDecls.writeToStdOut(p);
            p.indentLeft();
            p.printf("</InitialVariableDeclarations>\n");
        }

        p.printf("<InitialStatements>\n");
        p.indentRight();
        for (JStatement statement : initStatements) {
            statement.writeToStdOut(p);
        }
        p.indentLeft();
        p.printf("</InitialStatements>\n");

        p.printf("<ConditionExpression>\n");
        p.indentRight();
        if (condition == null) {
            p.printf("null\n");
        } else {
            condition.writeToStdOut(p);
        }
        p.indentLeft();
        p.printf("</ConditionExpression>\n");

        p.printf("<UpdateStatements>\n");
        p.indentRight();
        for (JStatement statement : updateStatements) {
            statement.writeToStdOut(p);
        }
        p.indentLeft();
        p.printf("</UpdateStatements>\n");

        p.printf("<Body>\n");
        p.indentRight();
        body.writeToStdOut(p);
        p.indentLeft();
        p.printf("</Body>\n");

        p.indentLeft();
        p.printf("</JForStatement>\n");
    }

}
