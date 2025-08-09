package ca.ubc.cs304.database;

import ca.ubc.cs304.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BranchConnectionHandler {
    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private static final String WARNING_TAG = "[WARNING]";
    private Connection connection = null;

    public BranchConnectionHandler(Connection connection) {
        this.connection = connection;
    }

    public List<BranchWithDrafts> getBranchesAndDraftsByWorkId(int workId) {
        List<BranchWithDrafts> result = new ArrayList<>();

        try {
            List<Branch> branches = getBranchesByWorkId(workId);
            for (Branch branch : branches) {
                List<Draft> drafts = getDraftsByBranchId(branch.getbID());
                result.add(new BranchWithDrafts(branch, drafts));
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return result;
    }

    public void deleteBranch(int bID, int pID) {
        String query = "DELETE FROM branches WHERE bID = ? AND pID = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, bID);
            ps.setInt(2, pID);

            int rowCount = ps.executeUpdate();
            if (rowCount == 0) {
                System.out.println(WARNING_TAG + " Branch ( " + bID + " , " + pID + " ) does not exist!");
            }

            connection.commit();

            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
        }
    }

    public String getBranchNameById(int bID, int pID) {
        String branchName = null;

        String query = "SELECT BRANCHNAME FROM branches WHERE bID = ? AND pID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bID);
            stmt.setInt(2, pID);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                branchName = rs.getString("branchName");
            }

            rs.close();
        } catch (SQLException e) {
            System.out.println("[EXCEPTION] " + e.getMessage());
        }

        return branchName;
    }

    private List<Branch> getBranchesByWorkId(int workId) throws SQLException {
        List<Branch> branches = new ArrayList<>();

        PreparedStatement stmt = connection.prepareStatement(
                "SELECT * FROM branches WHERE wID = ?"
        );
        stmt.setInt(1, workId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Branch branch = new Branch(
                    rs.getInt("bID"),
                    rs.getInt("pID"),
                    rs.getInt("wID"),
                    rs.getString("branchName"),
                    rs.getDate("branchCreatedAt"),
                    rs.getString("username")
            );
            branches.add(branch);
        }

        rs.close();
        stmt.close();

        return branches;
    }

    // GROUP BY on comments
    private List<Draft> getDraftsByBranchId(int branchId) throws SQLException {
        List<Draft> drafts = new ArrayList<>();

        PreparedStatement stmt = connection.prepareStatement(
                "SELECT d.dID, d.createdAt, d.username, d.bID, d.pID, COUNT(c.commentID) AS commentCount " +
                        "FROM drafts d LEFT JOIN comments c ON d.dID = c.dID " +
                        "WHERE d.bID = ? " +
                        "GROUP BY d.dID, d.createdAt, d.username, d.bID, d.pID " +
                        "ORDER BY d.createdAt ASC"
        );
        stmt.setInt(1, branchId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Draft draft = new Draft(
                    rs.getInt("dID"),
                    rs.getDate("createdAt"),
                    rs.getString("username"),
                    rs.getInt("bID"),
                    rs.getInt("pID")
            );
            draft.setCommentCount(rs.getInt("commentCount"));
            drafts.add(draft);
        }

        rs.close();
        stmt.close();

        return drafts;
    }

    private void rollbackConnection() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }
}
