package Database.Interfaces;

public interface IHighscoreDTO {

    String getUsername();

    String getWord();

    int getScore();

    void setUsername(String username);

    void setScore(int score);

    void setWord(String word);
}
