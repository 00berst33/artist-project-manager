package ca.ubc.cs304.model;

public class IsParentOf {
    private final int parentBID;
    private final int parentPID;
    private final int childBID;
    private final int childPID;

    public IsParentOf(int parentBID, int parentPID, int childBID, int childPID) {
        this.parentBID = parentBID;
        this.parentPID = parentPID;
        this.childBID = childBID;
        this.childPID = childPID;
    }

    public int getParentBID() {
        return parentBID;
    }

    public int getParentPID() {
        return parentPID;
    }

    public int getChildBID() {
        return childBID;
    }

    public int getChildPID() {
        return childPID;
    }
}
