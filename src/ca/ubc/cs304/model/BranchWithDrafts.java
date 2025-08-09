package ca.ubc.cs304.model;

import java.util.List;

/**
 * Wrapper for a Branch and its associated Drafts
 */
public class BranchWithDrafts {
    private final Branch branch;
    private final List<Draft> drafts;

    public BranchWithDrafts(Branch branch, List<Draft> drafts) {
        this.branch = branch;
        this.drafts = drafts;
    }

    public Branch getBranch() {
        return branch;
    }

    public List<Draft> getDrafts() {
        return drafts;
    }
}