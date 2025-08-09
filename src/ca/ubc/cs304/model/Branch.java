package ca.ubc.cs304.model;

import java.sql.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * The intent for this class is to update/store information about a single branch
 */
public class Branch {
	private final int bID;
	private final int pID;
	private final int wID;
	private final String branchName;
	private final Date branchCreatedAt;
	private final String username;

	private Branch parentBranch = null;
	private List<Branch> childBranches;

	public Branch(int bID, int pID, int wID, String branchName, Date branchCreatedAt, String username) {
		this.bID = bID;
		this.pID = pID;
		this.wID = wID;
		this.branchName = branchName;
		this.branchCreatedAt = branchCreatedAt;
		this.username = username;

		this.childBranches = new ArrayList<>();
	}

	public int getbID() {
		return bID;
	}

	public int getpID() {
		return pID;
	}

	public int getwID() {
		return wID;
	}

	public String getBranchName() {return branchName;}

	public Date getBranchCreatedAt() {
		return branchCreatedAt;
	}

	public String getUsername() {return username; }

	public void setParentBranch(Branch parentBranch) { this.parentBranch = parentBranch; }

	public Branch getParentBranch() { return parentBranch; }

	public void setChildBranches(List<Branch> childBranches) { this.childBranches = childBranches; }

	public void addChildBranch(Branch child) {this.childBranches.add(child);}

	public List<Branch> getChildBranches() { return childBranches; }
}
