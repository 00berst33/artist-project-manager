package ca.ubc.cs304.model;

import java.time.LocalDate;

public class Work {
    int wID;
    String workName;
    String createdAt = LocalDate.now().toString();

    public Work(int wID, String workName, String createdAt) {
        this.wID = wID;
        this.workName = workName;
        this.createdAt = createdAt;
    }

    public int getwID() {
        return wID;
    }

    public String getWorkName() {
        return workName;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
