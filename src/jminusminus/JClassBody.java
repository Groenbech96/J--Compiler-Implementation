package jminusminus;

import java.util.ArrayList;

public class JClassBody extends JAST {


    private ArrayList<JBlock> staticBlocks = new ArrayList<JBlock>();
    private ArrayList<JBlock> instanceBlocks = new ArrayList<JBlock>();



    private ArrayList<JMember> members = new ArrayList<JMember>();

    /**
     * Construct an AST node the given its line number in the source file.
     *
     * @param line line in which the source for the AST was found.
     */
    protected JClassBody(ArrayList<JBlock> staticBlocks, ArrayList<JBlock> instanceBlocks, ArrayList<JMember> members) {
        super(0); // hmm?
        this.staticBlocks = staticBlocks;
        this.instanceBlocks = instanceBlocks;
        this.members = members;
    }

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


    public ArrayList<JBlock> getStaticBlocks() {
        return staticBlocks;
    }

    public ArrayList<JBlock> getInstanceBlocks() {
        return instanceBlocks;
    }

    public ArrayList<JMember> getMembers() {
        return members;
    }

}
