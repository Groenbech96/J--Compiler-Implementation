package jminusminus;

import java.util.ArrayList;

import static jminusminus.CLConstants.*;
import static jminusminus.CLConstants.RETURN;

public class JClassBody extends JAST  {


    private ArrayList<JBlock> staticBlocks = new ArrayList<JBlock>();
    private ArrayList<JBlock> instanceBlocks = new ArrayList<JBlock>();
    private ArrayList<JMember> members = new ArrayList<JMember>();

    /**
     * Whether this class has an explicit constructor.
     */
    private boolean hasExplicitConstructor = false;

    /**
     * Instance fields of this class.
     */
    private ArrayList<JFieldDeclaration> instanceFieldInitializations;

    /**
     * Static (class) fields of this class.
     */
    private ArrayList<JFieldDeclaration> staticFieldInitializations;



    /**
     * Class declaration super class type.
     */
    private Type classSuperType;

    public void setClassSuperType(Type classSuperType) {
        this.classSuperType = classSuperType;
    }

    /**
     * The initializations for instance fields (now expressed as assignment
     * statments).
     *
     * @return the field declarations having initializations.
     */

    public ArrayList<JFieldDeclaration> instanceFieldInitializations() {
        return instanceFieldInitializations;
    }

    /**
     * Construct an AST node the given its line number in the source file.
     *
     * @param line line in which the source for the AST was found.
     */
    protected JClassBody(int line, ArrayList<JBlock> staticBlocks, ArrayList<JBlock> instanceBlocks, ArrayList<JMember> members) {
        super(line);
        this.staticBlocks = staticBlocks;
        this.instanceBlocks = instanceBlocks;
        this.members = members;

        instanceFieldInitializations = new ArrayList<JFieldDeclaration>();
        staticFieldInitializations = new ArrayList<JFieldDeclaration>();
    }

    public void preAnalyzeMembers(Context context, CLEmitter partial) {

        // Pre-analyze the members and add them to the partial
        // class

        for (JMember member : members) {
            member.preAnalyze(context, partial);
            if (member instanceof JConstructorDeclaration
                    && ((JConstructorDeclaration) member).params.size() == 0) {
                hasExplicitConstructor = true;
            }
        }

        // Add the implicit empty constructor?
        if (!this.hasExplicitConstructor) {
            codegenPartialImplicitConstructor(partial);
        }

    }

    @Override
    public JAST analyze(Context context) {
        // Analyze all members
        for (JMember member : members) {
            //Todo: Should we update the members?
            ((JAST) member).analyze(context);
        }

        // Copy declared fields for purposes of initialization.
        for (JMember member : members) {
            if (member instanceof JFieldDeclaration) {
                JFieldDeclaration fieldDecl = (JFieldDeclaration) member;
                if (fieldDecl.mods().contains("static")) {
                    staticFieldInitializations.add(fieldDecl);
                } else {
                    instanceFieldInitializations.add(fieldDecl);
                }
            }
        }

        return this;
    }

    @Override
    public void codegen(CLEmitter output) {

        // The members
        for (JMember member : members) {
            ((JAST) member).codegen(output);
        }

        // The implicit empty constructor?
        if (!hasExplicitConstructor) {
            codegenImplicitConstructor(output);
        }

        // Generate a class initialization method?
        if (staticFieldInitializations.size() > 0) {
            codegenClassInit(output);
        }




    }

    @Override
    public void writeToStdOut(PrettyPrinter p) {

        if(staticBlocks.size() > 0) {
            p.println("<ClassBlock>");
            for (JBlock member : staticBlocks) {
                ((JAST) member).writeToStdOut(p);
            }
            p.println("</ClassBlock>");
        }

        if(instanceBlocks.size() > 0) {
            p.println("<ClassBlock>");
            for (JBlock member : staticBlocks) {
                ((JAST) member).writeToStdOut(p);
            }
            p.println("</ClassBlock>");
        }

        if (members.size() > 0) {
            p.println("<ClassBlock>");
            for (JMember member : members) {
                ((JAST) member).writeToStdOut(p);
            }
            p.println("</ClassBlock>");
        }

    }

    /**
     * Generate code for an implicit empty constructor. (Necessary only if there
     * is not already an explicit one.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    private void codegenImplicitConstructor(CLEmitter output) {
        // Invoke super constructor
        ArrayList<String> mods = new ArrayList<String>();
        mods.add("public");
        output.addMethod(mods, "<init>", "()V", null, false);
        output.addNoArgInstruction(ALOAD_0);
        output.addMemberAccessInstruction(INVOKESPECIAL, classSuperType.jvmName(),
                "<init>", "()V");

        // If there are instance field initializations, generate
        // code for them
        for (JFieldDeclaration instanceField : instanceFieldInitializations) {
            instanceField.codegenInitializations(output);
        }

        // Return
        output.addNoArgInstruction(RETURN);
    }

    /**
     * Generate code for class initialization, in j-- this means static field
     * initializations.
     *
     * @param output the code emitter (basically an abstraction for producing the
     *               .class file).
     */

    private void codegenClassInit(CLEmitter output) {
        ArrayList<String> mods = new ArrayList<String>();
        mods.add("public");
        mods.add("static");
        output.addMethod(mods, "<clinit>", "()V", null, false);

        // If there are instance initializations, generate code
        // for them
        for (JFieldDeclaration staticField : staticFieldInitializations) {
            staticField.codegenInitializations(output);
        }

        // Return
        output.addNoArgInstruction(RETURN);
    }

    /**
     * Generate code for an implicit empty constructor. (Necessary only if there
     * is not already an explicit one.)
     *
     * @param partial the code emitter (basically an abstraction for producing a
     *                Java class).
     */

    private void codegenPartialImplicitConstructor(CLEmitter partial) {
        // Invoke super constructor
        ArrayList<String> mods = new ArrayList<String>();
        mods.add("public");
        partial.addMethod(mods, "<init>", "()V", null, false);
        partial.addNoArgInstruction(ALOAD_0);
        partial.addMemberAccessInstruction(INVOKESPECIAL, classSuperType.jvmName(),
                "<init>", "()V");

        // Return
        partial.addNoArgInstruction(RETURN);
    }

}
