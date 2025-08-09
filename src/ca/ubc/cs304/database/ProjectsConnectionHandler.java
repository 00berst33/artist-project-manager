package ca.ubc.cs304.database;

import ca.ubc.cs304.model.Project;
import ca.ubc.cs304.model.Work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ProjectsConnectionHandler {
    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private static final String WARNING_TAG = "[WARNING]";
    private Connection connection = null;

    public ProjectsConnectionHandler(Connection connection) {
        this.connection = connection;
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM Projects"
            ); // get all projects that are managed by a group that the user is in

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Project project = new Project(
                        rs.getInt("pID"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("gID")
                );
                projects.add(project);
            }

            rs.close();
            ps.close();
        }

        catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return projects;
    }

    public List<Project> getProjectsByLastDraftDate(String lastDraftDate) {
        List<Project> projects = new ArrayList<>();


        // Use format matching the user's input, or the DB column if you format consistently
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date utilDate;

        try {
            utilDate = sdf.parse(lastDraftDate);
        } catch (ParseException e) {
            System.err.println("Invalid date format. Expected yyyy-MM-dd");
            return projects; // Return empty list if invalid
        }

        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT *\n" +
                            "FROM PROJECTS p\n" +
                            "WHERE p.pID IN (SELECT pID\n" +
                            "                FROM Drafts\n" +
                            "                WHERE createdAt >= ?)"
            ); // get all projects that were contributed to after certain date

            ps.setDate(1, sqlDate);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Project project = new Project(
                        rs.getInt("pID"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("gID")
                );
                projects.add(project);
            }

            rs.close();
            ps.close();
        }

        catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return projects;
    }

    public List<Work> getWorksInProject(int pID) {
        List<Work> works = new ArrayList<>();

        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT wID, workName, createdAt " +
                            "FROM Works w NATURAL JOIN WorksInProjects wp " +
                            "WHERE pID = ?"
            ); // get all works in the chosen project

            ps.setInt(1, pID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Work work = new Work(
                        rs.getInt("wID"),
                        rs.getString("workName"),
                        rs.getDate("createdAt").toString()
                );
                works.add(work);
            }

            rs.close();
            ps.close();
        }

        catch (SQLException e) {
            System.out.println("bad");
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return works;
    }

//    public void updateProject(int pID, String title) {
//        try {
//            String query = "UPDATE Projects SET title = ? WHERE pID = ?";
//            PreparedStatement ps = connection.prepareStatement(query);
//            ps.setString(1, title);
//            ps.setInt(2, pID);
//
//            int rowCount = ps.executeUpdate();
//            if (rowCount == 0) {
//                System.out.println(WARNING_TAG + " Project " + pID + " does not exist!");
//            }
//
//            connection.commit();
//
//            ps.close();
//        } catch (SQLException e) {
//            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//            rollbackConnection();
//        }
//    }

    public Project[] getProjectInfo() {
        ArrayList<Project> result = new ArrayList<>();

        try {
            String query = "SELECT * FROM projects";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Project project = new Project(
                        rs.getInt("pID"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("gID"));
                result.add(project);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return result.toArray(new Project[result.size()]);
    }

    private void dropProjectsTableIfExists() {
        try {
            String query = "select table_name from user_tables";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                if(rs.getString(1).equalsIgnoreCase("Projects")) {
                    ps.execute("DROP TABLE Projects");
                    break;
                }
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }
}
