package ca.ubc.cs304.model;

public class Text extends Work {
    int wordCount;

    public Text(int wID, String workName, String createdAt, int wordCount) {
        super(wID, workName, createdAt);
        this.wordCount = wordCount;
    }

    public int getWordCount() {
        return wordCount;
    }
}
