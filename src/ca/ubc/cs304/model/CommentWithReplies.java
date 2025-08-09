package ca.ubc.cs304.model;

import java.util.List;

/**
 * Wrapper for a Comment and its Replies
 */
public class CommentWithReplies {
    private final Comment comment;
    private final List<Reply> replies;

    public CommentWithReplies(Comment comment, List<Reply> replies) {
        this.comment = comment;
        this.replies = replies;
    }

    public Comment getComment() {
        return comment;
    }

    public List<Reply> getReplies() {
        return replies;
    }

}