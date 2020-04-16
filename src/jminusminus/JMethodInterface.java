package jminusminus;

import java.util.ArrayList;

public class JMethodInterface implements JMember {
    //TODO: Add getters for fields as needed by the ast
    //TODO: Add exceptions to JMethodInterface
    private final int line;
    private final String name;
    private Type returnType;
    private final ArrayList<JFormalParameter> params;

    public JMethodInterface(int line, String name, Type returnType,
                            ArrayList<JFormalParameter> params) {
        this.line = line;
        this.name = name;
        this.returnType = returnType;
        this.params = params;
    }

    @Override
    public void preAnalyze(Context context, CLEmitter partial) {
        // Resolve types of the formal parameters
        for (JFormalParameter param : params) {
            param.setType(param.type().resolve(context));
        }

        // Resolve return type
        returnType = returnType.resolve(context);
    }

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JMethodInterface line=\"%d\" name=\"%s\" "
                + "returnType=\"%s\">\n", line, name, returnType
                .toString());
        p.indentRight();
        if (params != null) {
            p.println("<FormalParameters>");
            for (JFormalParameter param : params) {
                p.indentRight();
                param.writeToStdOut(p);
                p.indentLeft();
            }
            p.println("</FormalParameters>");
        }
        //TODO: Uncomment onces exceptions are implemented
        /*
        if (exceptions != null) {
            p.println("<Exceptions>");
            p.indentRight();
            for (Type t : exceptions) {
                p.printf("<Exception type=\"%s\"/>\n", t.toString());
            }
            p.indentLeft();
            p.println("</Exceptions>");
        }
        */
        p.indentLeft();
        p.println("</JMethodDeclaration>");
    }
}
