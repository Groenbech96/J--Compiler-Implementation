// Copyright 2013 Bill Campbell, Swami Iyer and Bahar Akbal-Delibas

package jminusminus;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * A class declaration has a list of modifiers, a name, a super class and a
 * class block; it distinguishes between instance fields and static (class)
 * fields for initialization, and it defines a type. It also introduces its own
 * (class) context.
 */

class JClassDeclaration extends JAST implements JTypeDecl {


    /**
     * Class modifiers.
     */
    private ArrayList<String> mods;

    /**
     * Class name.
     */
    private String name;

    /**
     * Class block.
     */
    // private ArrayList<JMember> classBlock;
    private JClassBody classBody;

    /**
     * Super class type.
     */
    private Type superType;

    /**
     * Interface types.
     */
    private final ArrayList<Type> interfaces;

    /**
     * This class type.
     */
    private Type thisType;

    /**
     * Context for this class.
     */
    private ClassContext context;

    /**
     * Interface names
     */
    private ArrayList<String> interfaceNames;



    /**
     * Construct an AST node for a class declaration given the line number, list
     * of class modifiers, name of the class, its super class type, and the
     * class block.
     *
     * @param line       line in which the class declaration occurs in the source file.
     * @param mods       class modifiers.
     * @param name       class name.
     * @param superType  super class type.
     * @param interfaces interfaces of the class
     * @param classBody class body.
     */

    public JClassDeclaration(int line, ArrayList<String> mods, String name,
                             Type superType, ArrayList<Type> interfaces, JClassBody classBody) {
        super(line);
        this.mods = mods;
        this.name = name;
        this.superType = superType;
        this.interfaces = interfaces;

        // Set class body
        this.classBody = classBody;
        if(classBody != null) {
            this.classBody.setClassSuperType(superType);
        }
    }

    /**
     * Return the class name.
     *
     * @return the class name.
     */

    public String name() {
        return name;
    }

    /**
     * Return the class' super class type.
     *
     * @return the super class type.
     */

    public Type superType() {
        return superType;
    }

    /**
     * Return the type that this class declaration defines.
     *
     * @return the defined type.
     */

    public Type thisType() {
        return thisType;
    }

    /**
     * The initializations for instance fields (now expressed as assignment
     * statments).
     *
     * @return the field declarations having initializations.
     */

    public ArrayList<JFieldDeclaration> instanceFieldInitializations() {
        return classBody.instanceFieldInitializations();
    }

    /**
     * Declare this class in the parent (compilation unit) context.
     *
     * @param context the parent (compilation unit) context.
     */

    public void declareThisType(Context context) {
        String qualifiedName = JAST.compilationUnit.packageName().equals("") ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        CLEmitter partial = new CLEmitter(false);
        partial.addClass(mods, qualifiedName, Type.OBJECT.jvmName(), null,
                false); // Object for superClass, just for now
        thisType = Type.typeFor(partial.toClass());
        context.addType(line, thisType);
    }

    /**
     * Pre-analyze the members of this declaration in the parent context.
     * Pre-analysis extends to the member headers (including method headers) but
     * not into the bodies.
     *
     * @param context the parent (compilation unit) context.
     */

    public void preAnalyze(Context context) {
        // Construct a class context
        this.context = new ClassContext(this, context);

        // Resolve superclass and pass to class body
        superType = superType.resolve(this.context);
        classBody.setClassSuperType(superType);

        //Resolve possible interfaces
        ArrayList<Type> resolvedInterfaces = new ArrayList<>();
        for (Type _interface : interfaces) {
            Type resolvedInterface = _interface.resolve(this.context);
            resolvedInterfaces.add(resolvedInterface);

        }
        interfaces.clear();
        interfaces.addAll(resolvedInterfaces);

        // Creating a partial class in memory can result in a
        // java.lang.VerifyError if the semantics below are
        // violated, so we can't defer these checks to analyze()
        thisType.checkAccess(line, superType, resolvedInterfaces);
        if (superType.isFinal()) {
            JAST.compilationUnit.reportSemanticError(line,
                    "Cannot extend a final type: %s", superType.toString());
        }

        // Create the (partial) class
        CLEmitter partial = new CLEmitter(false);

        interfaceNames = new ArrayList<>();
        if(interfaces != null) {
            for(Type type : interfaces) {
                type = type.resolve(this.context);
                interfaceNames.add(type.jvmName());
            }
        }
        // Add the class header to the partial class
        String qualifiedName = JAST.compilationUnit.packageName().equals("") ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        partial.addClass(mods, qualifiedName, superType.jvmName(), interfaceNames, false);

        // Pre analyze all members of this class
        // Finds out if we have an explicit constructor
        this.classBody.preAnalyzeMembers(this.context, partial);

        // Get the Class rep for the (partial) class and make it
        // the representation for this type
        Type id = this.context.lookupType(name);
        if (id != null && !JAST.compilationUnit.errorHasOccurred()) {
            id.setClassRep(partial.toClass());
        }
    }

    /**
     * Perform semantic analysis on the class and all of its members within the
     * given context. Analysis includes field initializations and the method
     * bodies.
     *
     * @param context the parent (compilation unit) context. Ignored here.
     * @return the analyzed (and possibly rewritten) AST subtree.
     */

    public JAST analyze(Context context) {
        // Analyze body
        this.classBody.analyze(this.context);

        // Finally, ensure that a non-abstract class has
        // no abstract methods.
        if (!thisType.isAbstract() && thisType.abstractMethods().size() > 0) {
            StringBuilder methods = new StringBuilder();
            for (Method method : thisType.abstractMethods()) {
                methods.append("\n").append(method);
            }
            JAST.compilationUnit.reportSemanticError(line,
                    "Class %s must be declared abstract since it defines "
                            + "the following abstract methods: %s", name, methods);

        }

        // Make sure that all interface methods are implemented
        // StringBuilder missingInterfaceMethods = new StringBuilder();
        // for (Type _interface: this.interfaces) {
        //     Class<? extends Type> classRep = _interface.getClass();
        //     java.lang.reflect.Method[] methods = classRep.getDeclaredMethods();
        //     for (java.lang.reflect.Method member: methods) {
        //         Method method = thisType.methodFor(member.getName(),
        //                     new Type[]{Type.typeFor(member.getReturnType())});
        //         if(method == null){
        //             missingInterfaceMethods.append("\n").append(_interface.simpleName()).append(".")
        //                 .append(member.getName());
        //         }
        //     }
        // }


        // if(!missingInterfaceMethods.toString().equals("")){
        //     JAST.compilationUnit.reportSemanticError(line,
        //             "Not all interfaces are implemented. Missing functions are: %s",
        //             missingInterfaceMethods.toString());
        // }

        return this;
    }

    /**
     * Generate code for the class declaration.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    public void codegen(CLEmitter output) {
        // The class header
        String qualifiedName = JAST.compilationUnit.packageName().equals("") ? name
                : JAST.compilationUnit.packageName() + "/" + name;
        output.addClass(mods, qualifiedName, superType.jvmName(), new ArrayList<>(interfaces.stream().map(Type::jvmName).collect(Collectors.toList())), false);

        this.classBody.codegen(output);
    }

    /**
     * @inheritDoc
     */

    public void writeToStdOut(PrettyPrinter p) {
        p.printf("<JClassDeclaration line=\"%d\" name=\"%s\""
                + " super=\"%s\">\n", line(), name, superType.toString());
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
        if(classBody != null) {
            classBody.writeToStdOut(p);
        }
        p.indentLeft();
        p.println("</JClassDeclaration>");
    }





}
