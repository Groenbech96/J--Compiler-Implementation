package jminusminus;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class JInterfaceDeclaration extends JAST implements JTypeDecl {
    private final ArrayList<JMember> members;
    private final ArrayList<Type> superInterfaces;
    private final String name;
    private final ArrayList<String> mods;
    private Type thisType;
    private ArrayList<JFieldDeclaration> staticFieldInitializations;
    private ClassContext context;

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
        ArrayList<Type> resolvedInterfaces = new ArrayList<>();
        for (Type _interface : superInterfaces) {
            resolvedInterfaces.add(_interface.resolve(this.context));
        }
        superInterfaces.clear();
        superInterfaces.addAll(resolvedInterfaces);

        Type.checkInterfaceAccess(line,thisType().classRep(),superInterfaces);

        CLEmitter clEmitter = new CLEmitter(false);
        String qualifiedName = JAST.compilationUnit.packageName().equals("") ? name
                : JAST.compilationUnit.packageName().replace(".", "/") + "/" + name;


        ArrayList<String> interfaceNames = superInterfaces.stream().map(Type::jvmName).collect(Collectors.toCollection(ArrayList::new));
        clEmitter.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), interfaceNames, false);

        for(JMember member : members) {
            if(!(member instanceof JMethodDeclaration || member instanceof JFieldDeclaration)) {
                JAST.compilationUnit.reportSemanticError(line(),
                        "Member %s is not a valid interface member", member.toString());
            }

            member.preAnalyze(this.context, clEmitter);
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
        String qualifiedName = packageName.equals("") ? name : packageName.replace(".", "/") + "/" + name;
        ArrayList<String> superInterfaceNames = superInterfaces.stream().map(Type::jvmName).collect(Collectors.toCollection(ArrayList::new));

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
        p.printf("<JInterfaceDeclaration line=\"%d\" name=\"%s\""
                + " super interfaces=\"%s\">\n", line(), name, superInterfaces.stream().map(Type::simpleName).collect(Collectors.joining(", ")));
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
        if(members != null) {
            p.println("<Method declarations>");
            for (JMember member: members) {
                if(member instanceof JMethodDeclaration){
                    ((JMethodDeclaration) member).writeToStdOut(p);
                } else if (member instanceof  JFieldDeclaration){
                    ((JFieldDeclaration) member).writeToStdOut(p);
                }
            }
        }
        p.indentLeft();
        p.println("</JClassDeclaration>");
    }

    @Override
    public void declareThisType(Context context) {
        String packageName = JAST.compilationUnit.packageName();

        String qualifiedName = packageName.equals("") ? name : packageName.replace(".", "/") + "/" + name;
        CLEmitter partial = new CLEmitter(false);

        ArrayList<String> superInterfaceNames = superInterfaces.stream().map(t -> packageName.replace(".", "/") + "/"+ t.jvmName()).collect(Collectors.toCollection(ArrayList::new));
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

    public ArrayList<JMember> getMembers() {
        return members;
    }

}
