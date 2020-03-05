package jminusminus;

import java.util.ArrayList;

public class JInterfaceDeclaration extends JAST implements JTypeDecl {
    private final ArrayList<JMember> methods;
    private final ArrayList<Type> interfaces;
    private final String name;
    private final ArrayList<String> mods;

    /**
     * Construct an AST node the given its line number in the source file.
     *
     * @param line line in which the source for the AST was found.
     */
    protected JInterfaceDeclaration(int line, ArrayList<String> mods, String name,  ArrayList<Type> interfaces, ArrayList<JMember> methods) {
        super(line);
        this.mods = mods;
        this.name = name;
        this.interfaces = interfaces;
        this.methods = methods;
    }

    //TODO: Fill out codegen and analysis

    @Override
    public JAST analyze(Context context) {
        return null;
    }

    @Override
    public void codegen(CLEmitter output) {

    }

    @Override
    public void writeToStdOut(PrettyPrinter p) {

    }

    @Override
    public void declareThisType(Context context) {

    }

    @Override
    public void preAnalyze(Context context) {

    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Type superType() {
        return null;
    }

    @Override
    public Type thisType() {
        return null;
    }
}
