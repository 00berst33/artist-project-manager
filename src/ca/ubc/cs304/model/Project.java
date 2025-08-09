package ca.ubc.cs304.model;

public class Project {
    private final int pID; // final?
    private final String title;
    private final String description;
    private final int gID;

    public Project(int pID, String title, String description, int gID) {
        this.pID = pID;
        this.title = title;
        this.description = description;
        this.gID = gID;
    }

    public int getpID() {return pID;}

    public String getTitle() {return title;}

    public String getDescription() {return description;}

    public int getgID() {return gID;}
}
