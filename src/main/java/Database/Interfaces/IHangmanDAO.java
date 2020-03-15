package Database.Interfaces;

import java.rmi.Remote;
import java.sql.ResultSet;
import java.util.ArrayList;

public interface IHangmanDAO extends Remote {

/*
    int PORT = 9976;
    String PATH = "galgelegmeddatabase";
    String DOMAIN = "dist.saluton.dk";
    String URL = String.format("rmi://%s:%d/%s", DOMAIN, PORT, PATH);
*/

    void createSchema() throws Exception;

    void dropSchema() throws Exception;

    IHighscoreDTO makeHighscoreFromResultset(ResultSet resultset) throws Exception;

    ArrayList<IHighscoreDTO> getAllHighscores() throws Exception;

    void insertHighscore(String username, String word, int score) throws Exception;
}
