package jminusminus;

import java.util.ArrayList;

public class JMethodInterface implements JMember {
    //TODO: Add getters for fields as needed by the ast
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
}
