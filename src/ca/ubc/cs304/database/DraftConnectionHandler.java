package ca.ubc.cs304.database;

import ca.ubc.cs304.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DraftConnectionHandler {
    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private final Connection connection;

    public DraftConnectionHandler(Connection connection) {
        this.connection = connection;
    }

    // Get draft + comments + replies
    public DraftWithComments getDraftWithCommentsAndReplies(int dID) {
        try {
            Draft draft = getDraftById(dID);
            if (draft == null) {
                return null;
            }

            List<Comment> comments = getCommentsByDraft(dID);
            List<CommentWithReplies> commentWithRepliesList = new ArrayList<>();

            for (Comment comment : comments) {
                List<Reply> replies = getRepliesByComment(comment.getCommentID());
                commentWithRepliesList.add(new CommentWithReplies(comment, replies));
            }

            return new DraftWithComments(draft, commentWithRepliesList);

        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            return null;
        }
    }

    public int getCommentCountForDraft(int draftId) throws SQLException {
        String query = "SELECT COUNT(*) AS count FROM comments WHERE dID = ?";
        int count = 0;

        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, draftId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("count");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return count;
    }

    // DIVISION
    public List<Integer> getDraftsCommentedByAllUsers(List<String> usernames) {
        List<Integer> result = new ArrayList<>();

        if (usernames == null || usernames.isEmpty()) {
            return result; // No users selected
        }

        String placeholders = String.join(", ", usernames.stream().map(u -> "?").toArray(String[]::new));

        String query = "SELECT c.dID " +
                "FROM comments c " +
                "WHERE c.username IN (" + placeholders + ") " +
                "GROUP BY c.dID " +
                "HAVING COUNT(DISTINCT c.username) = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            int i = 1;
            for (String username : usernames) {
                stmt.setString(i++, username);
            }
            stmt.setInt(i, usernames.size());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getInt("dID"));
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }

        return result;
    }

    // NESTED QUERY
    public List<String> getUsernamesForWork(int workId) {
        List<String> usernames = new ArrayList<>();
        String query = """
                        SELECT DISTINCT c.username
                        FROM comments c
                        WHERE c.dID IN (
                            SELECT d.dID
                            FROM drafts d
                            JOIN branches b ON d.bID = b.bID AND d.pID = b.pID
                            WHERE b.wID = ?
                        )
                    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, workId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String user = rs.getString("username");
                usernames.add(user);
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }

        return usernames;
    }

    private List<Reply> getRepliesByComment(int commentID) throws SQLException {
        List<Reply> replies = new ArrayList<>();

        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM Reply_to WHERE commentID = ?"
        );
        stmt.setInt(1, commentID);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Reply reply = new Reply(
                    rs.getInt("commentID"),
                    rs.getInt("replyID"),
                    rs.getString("username"),
                    rs.getDate("createdAt"),
                    rs.getString("text")
            );
            replies.add(reply);
        }

        rs.close();
        stmt.close();

        return replies;
    }

    private Draft getDraftById(int dID) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM drafts WHERE dID = ?"
        );
        stmt.setInt(1, dID);
        ResultSet rs = stmt.executeQuery();

        Draft draft = null;
        if (rs.next()) {
            draft = new Draft(
                    rs.getInt("dID"),
                    rs.getDate("createdAt"),
                    rs.getString("username"),
                    rs.getInt("bID"),
                    rs.getInt("pID")
            );
        }

        rs.close();
        stmt.close();

        return draft;
    }

    private List<Comment> getCommentsByDraft(int dID) throws SQLException {
        List<Comment> comments = new ArrayList<>();

        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM comments WHERE dID = ?"
        );
        stmt.setInt(1, dID);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Comment comment = new Comment(
                    rs.getInt("commentID"),
                    rs.getString("text"),
                    rs.getString("username"),
                    rs.getDate("createdAt"),
                    rs.getInt("dID")
            );
            comments.add(comment);
        }

        rs.close();
        stmt.close();

        return comments;
    }

    private void rollbackConnection() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }
}
