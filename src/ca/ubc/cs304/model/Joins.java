package ca.ubc.cs304.model;

public class Joins {
    private String username;
    private int gID;

    public Joins(String username, int gID) {
        this.username = username;
        this.gID = gID;
    }
    public String getUsername() {
        return username;
    }
    public int getgID() {
        return gID;
    }
}
