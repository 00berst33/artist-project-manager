package ca.ubc.cs304.database;

import ca.ubc.cs304.model.Group;

import java.sql.*;
import java.util.ArrayList;

public class GroupConnectionHandler {
    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private static final String WARNING_TAG = "[WARNING]";
    private Connection connection = null;

    public GroupConnectionHandler(Connection connection) {
        this.connection = connection;
    }

    public void groupSetup() {

    }

    public void dropGroupTableIfExists() {
        try {
            String query = "select table_name from user_tables";
            PreparedStatement ps = connection.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                if(rs.getString(1).equalsIgnoreCase("groups")) {
                    ps.execute("DROP TABLE groups");
                    break;
                }
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    public ArrayList<Group> getGroups() {
        System.out.println("getting groups");
        ArrayList<Group> result = new ArrayList<Group>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM groups");

//    		// get info on ResultSet
//    		ResultSetMetaData rsmd = rs.getMetaData();
//
//    		System.out.println(" ");
//
//    		// display column names;
//    		for (int i = 0; i < rsmd.getColumnCount(); i++) {
//    			// get column name and print it
//    			System.out.printf("%-15s", rsmd.getColumnName(i + 1));
//    		}

            while(rs.next()) {
                Group group = new Group(rs.getInt("gID"),
                        rs.getString("name"),
                        rs.getString("statement"));
                result.add(group);
            }

            System.out.println(result.size());

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return result;
    }

//    public Group[] filterGroups(String match) {
//        ArrayList<Group> result = new ArrayList<Group>();
//        String query = "SELECT * FROM groups WHERE name LIKE '%"+match+"%'";
//        try {
//            Statement stmt = connection.createStatement();
//            ResultSet rs = stmt.executeQuery(query);
//
////    		// get info on ResultSet
////    		ResultSetMetaData rsmd = rs.getMetaData();
////
////    		System.out.println(" ");
////
////    		// display column names;
////    		for (int i = 0; i < rsmd.getColumnCount(); i++) {
////    			// get column name and print it
////    			System.out.printf("%-15s", rsmd.getColumnName(i + 1));
////    		}
//
//            while(rs.next()) {
//                Group group = new Group(rs.getInt("gID"),
//                        rs.getString("name"),
//                        rs.getString("statement"));
//                result.add(group);
//            }
//
//            rs.close();
//            stmt.close();
//        } catch (SQLException e) {
//            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//        }
//
//        return result.toArray(new Group[result.size()]);
//    }

    public ArrayList<Group> filterGroups(String match, int minCreators) {
        ArrayList<Group> result = new ArrayList<Group>();
        System.out.println("filtering groups "+match+ " "+minCreators);

        //get number of people in a group: groupby gID count by username
        //left join joins to groups table
        String query = "SELECT g.gID, g.name, g.statement, COUNT(j.username) AS numCreators " +
                "FROM Groups g " +
                "LEFT JOIN Joins j ON g.gID = j.gID " +
                "WHERE g.name LIKE ? " +
                "GROUP BY g.gID, g.name, g.statement " +
                "HAVING COUNT(username) >= ?";

        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, "%"+match+"%");
            ps.setInt(2, minCreators);
            ResultSet rs = ps.executeQuery();

//    		// get info on ResultSet
//    		ResultSetMetaData rsmd = rs.getMetaData();
//
//    		System.out.println(" ");
//
//    		// display column names;
//    		for (int i = 0; i < rsmd.getColumnCount(); i++) {
//    			// get column name and print it
//    			System.out.printf("%-15s", rsmd.getColumnName(i + 1));
//    		}

            while(rs.next()) {
                Group group = new Group(rs.getInt("gID"),
                        rs.getString("name"),
                        rs.getString("statement"));
                result.add(group);
            }
            System.out.println(result.size());

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return result;
    }

    public int getNumCreators(int gID) {
        String query = "SELECT j.gID, COUNT(j.username) AS numCreators " +
                "FROM Joins j " +
                "WHERE j.gID = ? "+
                "GROUP BY j.gID";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, gID);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
//                System.out.println("SUCCCESS "+rs.getInt("numCreators"));
                return rs.getInt("numCreators");
            }
        } catch (SQLException e) {
//            System.out.println("im icaught");
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            return 0;
        }
        return 0;
    }
}
