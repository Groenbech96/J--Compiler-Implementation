package jminusminus;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class JInterfaceDeclaration extends JAST implements JTypeDecl {
    private final ArrayList<JMember> methods;
    private final ArrayList<Type> interfaces;
    private final String name;
    private final ArrayList<String> mods;
    private ClassContext context;
    private Type thisType;

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
        this.thisType = this.thisType();
    }

    //TODO: Fill out codegen

    @Override
    public void preAnalyze(Context context) {
        this.context = new ClassContext(this, context);
        ArrayList<Type> resolvedInterfaces = new ArrayList<>();
        for (Type _interface : interfaces) {
            resolvedInterfaces.add(_interface.resolve(this.context));
        }
        interfaces.clear();
        interfaces.addAll(resolvedInterfaces);

        Type.checkInterfaceAccess(line,thisType().classRep(),interfaces);

        CLEmitter clEmitter = new CLEmitter(false);
        String qualifiedName = JAST.compilationUnit.packageName().equals("") ? name
                : JAST.compilationUnit.packageName() + "/" + name;

        ArrayList<String> interfaceNames = interfaces.stream().map(Type::jvmName).collect(Collectors.toCollection(ArrayList::new));
        clEmitter.addClass(mods,qualifiedName,null, interfaceNames,false);

        for(JMember method: methods){
            method.preAnalyze(context, clEmitter);
        }

        // Get the Class rep for the (partial) class and make it
        // the
        // representation for this type
        Type id = this.context.lookupType(name);
        if (id != null && !JAST.compilationUnit.errorHasOccurred()) {
            id.setClassRep(clEmitter.toClass());
        }
    }

    @Override
    public JAST analyze(Context context) {
        // s164429: I don't see any members of the declaration that needs analyze called on it.
        return this;
    }

    @Override
    public void codegen(CLEmitter output) {

    }

    @Override
    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JInterfaceDeclaration line=\"%d\" name=\"%s\""
                + " super interfaces=\"%s\">\n", line(), name, interfaces.stream().map(Type::simpleName).collect(Collectors.joining(", ")));
        p.indentRight();
        if (context != null) {
            context.writeToStdOut(p);
        }
        if (mods != null) {
            p.println("<Modifiers>");
            p.indentRight();
            for (String mod : mods) {
                p.printf("<Modifier name=\"%s\"/>\n", mod);
            }
            p.indentLeft();
            p.println("</Modifiers>");
        }
        if(methods != null) {
            p.println("<Method declarations>");
            for (JMember member: methods) {
                ((JMethodInterface) member).writeToStdOut(p);
            }
        }
        p.indentLeft();
        p.println("</JClassDeclaration>");
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
        return thisType;
    }

    public ArrayList<JMember> getMethods() {
        return methods;
    }

}
