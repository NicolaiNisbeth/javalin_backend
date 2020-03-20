package Database;

import java.io.Serializable;

public class Score implements Serializable {
    // auto mapping with mongo
    @MongoId // auto
    @MongoObjectId
    private String key;
    private String wordToGuess;
    private int score;
    private String username;

    //This constructor is userd for MongoDB mapping
    public Score(){}

    public Score(String username,String wordToGuess, int score){
        this.wordToGuess = wordToGuess;
        this.score = score;
        this.username=username;
    }


    public String getWord() {
        return wordToGuess;
    }

    public int getScore(){return score;}

    public String getUsername() {
        return username;
    }
}
