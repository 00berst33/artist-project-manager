package ca.ubc.cs304.model;

import java.sql.Date;

/**
 * The intent for this class is to update/store information about a single draft
 */
public class Draft {
    private final int dID;
    private final Date createdAt;
    private final String username;
    private final int bID;
    private final int pID;

    private int commentCount;

    public Draft(int dID, Date createdAt, String username, int bID, int pID) {
        this.dID = dID;
        this.createdAt = createdAt;
        this.username = username;
        this.bID = bID;
        this.pID = pID;
    }

    public int getdID() {
        return dID;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getUsername() {
        return username;
    }

    public int getbID() {
        return bID;
    }

    public int getpID() {
        return pID;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int count) {
        this.commentCount = count;
    }

}
