package ca.ubc.cs304.model;

import java.util.List;

/**
 * Wrapper for a Draft and its associated Comments
 */
public class DraftWithComments {
    private final Draft draft;
    private final List<CommentWithReplies> comments;

    public DraftWithComments(Draft draft, List<CommentWithReplies> comments) {
        this.draft = draft;
        this.comments = comments;
    }

    public Draft getDraft() {
        return draft;
    }

    public List<CommentWithReplies> getComments() {
        return comments;
    }

    public List<CommentWithReplies> getCommentsWithReplies() {
        return comments;
    }
}