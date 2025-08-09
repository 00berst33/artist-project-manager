package ca.ubc.cs304.model;

public class Image extends Work {
    int canvasHeight;
    int canvasWidth;

    public Image(int wID, String workName, String createdAt, int canvasHeight, int canvasWidth) {
        super(wID, workName, createdAt);
        this.canvasHeight = canvasHeight;
        this.canvasWidth = canvasWidth;
    }

    public int getCanvasHeight() {return canvasHeight;}

    public int getCanvasWidth() {return canvasWidth;}
}
