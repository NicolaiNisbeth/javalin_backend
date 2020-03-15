package Database;

import org.json.JSONObject;

public class HighscoreDTO implements IHighscoreDTO {

    private String word;
    private String username;
    private int score;

    //Don't know if i need a constructer, might get deleted later.
    public HighscoreDTO() {
        word = "N/A";
        username = "N/A";
        score = 0;
    }

    @Override
    public String getWord() {
        return word;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.append("username", username);
        json.append("word", word);
        json.append("score", score);
        return json.toString();
    }
}
