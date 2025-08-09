package ca.ubc.cs304.database;

import ca.ubc.cs304.model.CreatorModel;

import java.sql.*;
import java.util.ArrayList;

public class CreatorConnectionHandler {
    private static final String EXCEPTION_TAG = "[EXCEPTION]";
    private static final String WARNING_TAG = "[WARNING]";

    private Connection connection = null;

    public CreatorConnectionHandler(Connection connection) {
        this.connection = connection;

    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

//    public void deleteCreator(String email) {
//        try {
//
//        } catch (SQLException e) {
//            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
//            rollbackConnection();
//        }
//    }
private void rollbackConnection() {
    try  {
        connection.rollback();
    } catch (SQLException e) {
        System.out.println(EXCEPTION_TAG + " " + e.getMessage());
    }
}

    public void insertCreator(CreatorModel model) {
  		try {
  			PreparedStatement ps = connection.prepareStatement("INSERT INTO creators VALUES (?,?,?,?)");
  			ps.setString(1, model.getEmail());
  			ps.setString(2, model.getUsername());
  			ps.setString(3, model.getPassword());
  			ps.setInt(4, model.getNumDrafts());

  			ps.executeUpdate();
  			connection.commit();

  			ps.close();
  		        } catch (SQLException e) {
  			System.out.println(EXCEPTION_TAG + " " + e.getMessage());
  			rollbackConnection();
         }

    }

    public CreatorModel getCreator(String username) {
        String query = "SELECT * FROM creators WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String foundUsername = rs.getString("username");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    int numDrafts = rs.getInt("numDrafts");

                    return new CreatorModel(foundUsername, email, password, numDrafts);
                } else {
                    System.out.println("creator not found " + username);
                    return null;
                }
            }

        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            return null;
        }

    }

    public CreatorModel getCreator(String email, String password) {
        String query = "SELECT * FROM creators WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String foundPassword = rs.getString("password");
                    String username = rs.getString("username");
                    int numDrafts = rs.getInt("numDrafts");
                    return new CreatorModel(username, email, foundPassword, numDrafts);
                }
            }
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            return null;
        }
        return null;
    }



    public CreatorModel[] getCreatorInfo() {
        ArrayList<CreatorModel> result = new ArrayList<CreatorModel>();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM creators");

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
                CreatorModel model = new CreatorModel(rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getInt("numDrafts"));
                result.add(model);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        return result.toArray(new CreatorModel[result.size()]);
    }

    public void databaseSetup() {
        dropCreatorTableIfExists();

        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE Creators( username  VARCHAR(40) PRIMARY KEY email     VARCHAR(40) password  VARCHAR(40) numDrafts INTEGER  UNIQUE (email);");
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }

        CreatorModel creator1 = new CreatorModel("ersta027", "berstad@ymail.com", "password", 50);
        insertCreator(creator1);

        CreatorModel creator2 = new CreatorModel("caoyut", "rcao@ymaill.com", "password2", 20);
        insertCreator(creator2);

        CreatorModel creator3 = new CreatorModel("i0z9o", "usharif@ymail.com", "password3", 100);
        insertCreator(creator3);

        CreatorModel creator4 = new CreatorModel("misspiggy", "mpiggy@ymail.com", "password4", 3);
        insertCreator(creator4);

        CreatorModel creator5 = new CreatorModel("animal", "animal@ymail.com", "password5", 0);
        insertCreator(creator5);

        CreatorModel creator6 = new CreatorModel("jthrnhill", "jcharlton@ymail.com", "password6", 44);
        insertCreator(creator6);

        CreatorModel creator7 = new CreatorModel("nthrnhill", "nthrn@ymail.com", "password7", 34);
        insertCreator(creator7);

        CreatorModel creator8 = new CreatorModel("benthrn", "bmaida@ymail.com", "password8", 78);
        insertCreator(creator8);

        CreatorModel creator9 = new CreatorModel("ethrn", "emccann@ymail.com", "password9", 55);
        insertCreator(creator9);

        CreatorModel creator10 = new CreatorModel("davinki", "dvinki@ymail.com", "password10", 2950);
        insertCreator(creator10);

        CreatorModel creator11 = new CreatorModel("mangelo", "mangelo@ymail.com", "password11", 86);
        insertCreator(creator11);

        CreatorModel creator12 = new CreatorModel("sirtpratch", "tpratchett@ymail.com", "password12", 47);
        insertCreator(creator12);

    }

    public boolean lookupCreatorEmail(String email) {
        String query = "SELECT 1 FROM creators WHERE email = ?";
        try  {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // returns true if any result is found
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            rollbackConnection();
            return false;
        }

    }

//    public boolean isValidSignup(String email, String username) {
//
//    }

    public boolean lookupCreatorUsername(String username) {
        String query = "SELECT 1 FROM creators WHERE username = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next(); // returns true if any result is found
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
            return false;
        }
    }

    public boolean verifyLogin(String email, String password) {
        String query = "SELECT password FROM creators WHERE email = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    System.out.println("Found user: " + storedPassword);
                    return storedPassword.equals(password);
                }

        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
        return false;
    }

    public void changePassword(String username, String password) {
        String query = "UPDATE creators SET password = ? WHERE username = ?";
        System.out.println(username + " " + password);
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, password);
            ps.setString(2, username);
            int rowsAffected = ps.executeUpdate();
            System.out.println(rowsAffected);
            connection.commit();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    public void changeEmail(String username, String email) {
        String query = "UPDATE creators SET email = ? WHERE username = ?";
        System.out.println(username + " " + email);
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, email);
            ps.setString(2, username);

            int rowsAffected = ps.executeUpdate();
            System.out.println(rowsAffected);
            connection.commit();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }

    private void dropCreatorTableIfExists() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("select table_name from user_tables");

            while(rs.next()) {
                if(rs.getString(1).toLowerCase().equals("creators")) {
                    stmt.execute("DROP TABLE creators");
                    break;
                }
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(EXCEPTION_TAG + " " + e.getMessage());
        }
    }
}