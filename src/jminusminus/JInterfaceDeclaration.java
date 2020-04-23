package jminusminus;

import java.util.ArrayList;

public class JInterfaceDeclaration extends JAST implements JTypeDecl {
    private final ArrayList<JMember> members;
    private final ArrayList<Type> interfaces;
    private final String name;
    private final ArrayList<String> mods;
    private ArrayList<JFieldDeclaration> fieldInitializations;
    private ClassContext context;

    /**
     * Construct an AST node the given its line number in the source file.
     *
     * @param line line in which the source for the AST was found.
     */
    protected JInterfaceDeclaration(int line, ArrayList<String> mods, String name,  ArrayList<Type> interfaces, ArrayList<JMember> members) {
        super(line);
        this.mods = mods;
        this.name = name;
        this.interfaces = interfaces;
        this.members = members;
        this.fieldInitializations = new ArrayList<>();
    }

    public void preAnalyze(Context context) {
        this.context = new ClassContext(this, context);

        // Create the (partial) interface, this is required
        // in the pre-analysis pass so the interface can be
        // referenced before it is declared
        CLEmitter partial = new CLEmitter(false);

        String packageName = JAST.compilationUnit.packageName();
        String qualifiedName = packageName == "" ? name : packageName + "/" + name;

        // All interfaces have OBJECT as their supertype
        partial.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), null, false);

        // Ensure that all members are abstract methods
        for(JMember member : members) {
            if(!(member instanceof JMethodDeclaration || member instanceof JFieldDeclaration)) {
                JAST.compilationUnit.reportSemanticError(line(), 
                    "Member %s is not a valid interface member", member.toString());
            }

            member.preAnalyze(context, partial);
        }
    }

    @Override
    public JAST analyze(Context context) {
        // Analyze all members
        for (JMember member : members) {
            ((JAST) member).analyze(context);
        }

        // Copy declared fields for purposes of initialization.
        for (JMember member : members) {
            if (member instanceof JFieldDeclaration) {
                JFieldDeclaration fieldDecl = (JFieldDeclaration) member;
                if (fieldDecl.mods().contains("static")) {
                    this.fieldInitializations.add(fieldDecl);
                } else {
                    JAST.compilationUnit.reportSemanticError(line(), 
                    "Field declaration is not a static member, interfaces may only have static field declarations", member.toString());
                }
            }
        }

        return this;
    }

    @Override
    public void codegen(CLEmitter output) {
        String packageName = JAST.compilationUnit.packageName();
        String qualifiedName = packageName == "" ? name : packageName + "/" + name;
        ArrayList<String> interfaceNames = new ArrayList<String>();
        if(interfaces != null) {
            for(Type t : interfaces) {
                interfaceNames.add(t.jvmName());
            }
        }
        output.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), interfaceNames, false); 

        // Generate code for the interface members
        for (JMember member : members) {
            ((JAST) member).codegen(output);
        }
    }

    @Override
    public void writeToStdOut(PrettyPrinter p) {

    }

    @Override
    public void declareThisType(Context context) {

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
