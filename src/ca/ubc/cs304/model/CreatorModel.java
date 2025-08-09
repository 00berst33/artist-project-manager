package ca.ubc.cs304.model;

/**
 * The intent for this class is to update/store information about a single creator in the table
 */

public class CreatorModel {
    private final String username;
    private final String email;
    private final String password;
//    private final String userRank;
    private int numDrafts;

    public CreatorModel(String username, String email, String password, int numDrafts) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.numDrafts = numDrafts;
    }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public int getNumDrafts() { return numDrafts; }
    public int setNumDrafts(int numDrafts) { this.numDrafts = numDrafts; return numDrafts;}
    public int incrementNumDrafts() { this.numDrafts++; return this.numDrafts; }

}
