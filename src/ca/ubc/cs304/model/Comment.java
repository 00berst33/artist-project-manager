package ca.ubc.cs304.model;

import java.sql.Date;

/**
 * The intent for this class is to store a single comment
 */
public class Comment {
    private final int commentID;
    private final String text;
    private final String username;
    private final Date createdAt;
    private final int dID;

    public Comment(int commentID, String text, String username, Date createdAt, int dID) {
        this.commentID = commentID;
        this.text = text;
        this.username = username;
        this.createdAt = createdAt;
        this.dID = dID;
    }

    public int getCommentID() {
        return commentID;
    }

    public String getText() {
        return text;
    }

    public String getUsername() {
        return username;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getdID() {
        return dID;
    }
}
