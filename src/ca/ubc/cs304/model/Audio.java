package ca.ubc.cs304.model;

public class Audio extends Work {
    float duration;

    public Audio(int wID, String workName, String createdAt, float duration) {
        super(wID, workName, createdAt);
        this.duration = duration;
    }

    public float getDuration() {return duration;}
}
