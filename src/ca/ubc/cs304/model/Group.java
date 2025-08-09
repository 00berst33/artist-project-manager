package ca.ubc.cs304.model;

public class Group {
    private final int gID;
    private String name;
    private String statement;

    public Group(int gID, String name, String statement) {
        this.gID = gID;
        this.name = name;
        this.statement = statement;
    }

    public int getgID() {
        return gID;
    }
    public String getName() {
        return name;
    }
    public String getStatement() {
        return statement;
    }

}
