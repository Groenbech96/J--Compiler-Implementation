package jminusminus;

import java.util.ArrayList;

public class JInitializationBlockDeclaration extends JMethodDeclaration implements JMember {
    /**
     * Construct an AST node for a method declaration given the
     * line number, method name, return type, formal parameters,
     * and the method body.
     *
     * @param line       line in which the method declaration occurs
     *                   in the source file.
     * @param mods       modifiers.
     * @param name       method name.
     * @param body       method body.
     */
    public JInitializationBlockDeclaration(int line, ArrayList<String> mods, String name, JBlock body) {
            super(line, mods, name, Type.STATIC_BLOCK, new ArrayList<>(), new ArrayList<>(), body);
    }

    public JInitializationBlockDeclaration(int line, String name, JBlock body) {
        super(line, new ArrayList<>(), name, Type.INSTANCE_BLOCK, new ArrayList<>(), new ArrayList<>(), body);
    }
}
