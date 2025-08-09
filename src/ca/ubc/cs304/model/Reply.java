package ca.ubc.cs304.model;

import java.sql.Date;

/**
 * Model for a single Reply to a Comment
 */
public class Reply {
    private final int commentID;
    private final int replyID;
    private final String username;
    private final Date createdAt;
    private final String text;

    public Reply(int commentID, int replyID, String username, Date createdAt, String text) {
        this.commentID = commentID;
        this.replyID = replyID;
        this.username = username;
        this.createdAt = createdAt;
        this.text = text;
    }

    public int getCommentID() {
        return commentID;
    }

    public int getReplyID() {
        return replyID;
    }

    public String getUsername() {
        return username;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getText() {
        return text;
    }
}
