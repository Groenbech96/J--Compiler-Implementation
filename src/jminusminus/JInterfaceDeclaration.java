package jminusminus;

import java.util.ArrayList;

public class JInterfaceDeclaration extends JAST implements JTypeDecl {
    private final ArrayList<JMember> members;
    private final ArrayList<Type> superInterfaces;
    private final String name;
    private final ArrayList<String> mods;
    private Type thisType;
    private ArrayList<JFieldDeclaration> staticFieldInitializations;
    private ClassContext context;
    private ArrayList<String> superInterfaceNames;

    /**
     * Construct an AST node the given its line number in the source file.
     *
     * @param line line in which the source for the AST was found.
     */
    protected JInterfaceDeclaration(int line, ArrayList<String> mods, String name,  ArrayList<Type> superInterfaces, ArrayList<JMember> members) {
        super(line);
        this.mods = mods;
        this.mods.add("interface");
        this.name = name;
        this.superInterfaces = superInterfaces;
        this.members = members;
        this.staticFieldInitializations = new ArrayList<>();
    }

    @Override
    public void preAnalyze(Context context) {
        this.context = new ClassContext(this, context);

        // Create the (partial) interface, this is required
        // in the pre-analysis pass so the interface can be
        // referenced before it is declared
        CLEmitter partial = new CLEmitter(false);

        String packageName = JAST.compilationUnit.packageName();
        String qualifiedName = packageName == "" ? name : packageName + "/" + name;

        // TODO: CHECK
        superInterfaceNames = new ArrayList<String>();
        if(superInterfaces != null) {
            for(Type t : superInterfaces) {
                Type superType = t.resolve(this.context);
                superInterfaceNames.add(superType.jvmName());
            }
        }
        // All interfaces have OBJECT as their supertype
        partial.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), null, false);

        // Ensure that all members are methods or field declarations
        for(JMember member : members) {
            if(!(member instanceof JMethodDeclaration || member instanceof JFieldDeclaration)) {
                JAST.compilationUnit.reportSemanticError(line(), 
                    "Member %s is not a valid interface member", member.toString());
            }

            member.preAnalyze(this.context, partial);
        }

        // Get the Class rep for the (partial) class and make it
        // the representation for this type
        Type id = this.context.lookupType(name);
        if (id != null && !JAST.compilationUnit.errorHasOccurred()) {
            id.setClassRep(partial.toClass());
        }
    }

    @Override
    public JAST analyze(Context context) {
        // Analyze all members
        for (JMember member : members) {
            ((JAST) member).analyze(this.context);
        }

        // Copy declared fields for purposes of initialization.
        for (JMember member : members) {
            if (member instanceof JFieldDeclaration) {
                JFieldDeclaration fieldDecl = (JFieldDeclaration) member;
                if (fieldDecl.mods().contains("static")) {
                    staticFieldInitializations.add(fieldDecl);
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
        output.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), superInterfaceNames, false); 

        // Generate code for the interface members
        for (JMember member : members) {
            ((JAST) member).codegen(output);
        }

        // Generate code for the static fields
        for (JFieldDeclaration staticField : staticFieldInitializations) {
            staticField.codegenInitializations(output);
        }
    }

    @Override
    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JInterfaceDeclaration line=\"%d\" name=\"%s\"", line(), name);
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
        p.println("<InterfaceBlock>");
        if (members.size() > 0) {
            for (JMember member : members) {
                ((JAST) member).writeToStdOut(p);
            }
        }
        p.println("</InterfaceBlock>");

        p.indentLeft();
        p.println("</JInterfaceDeclaration>");
    }

    @Override
    public void declareThisType(Context context) {
        String packageName = JAST.compilationUnit.packageName();
        String qualifiedName = packageName == "" ? name : packageName + "/" + name;
        CLEmitter partial = new CLEmitter(false);
        partial.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), superInterfaceNames, false);
        thisType = Type.typeFor(partial.toClass());
        context.addType(line, thisType);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Type superType() {
        return Type.OBJECT;
    }

    @Override
    public Type thisType() {
        return thisType;
    }
}
