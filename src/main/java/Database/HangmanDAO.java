package Database;

import java.sql.*;
import java.util.ArrayList;

public class HangmanDAO implements IHangmanDAO {

    String tablename = "Highscores";
    String id = "id";
    String username = "username";
    String word = "word";
    String score = "score";

    @Override
    public ArrayList<IHighscoreDTO> getAllHighscores() throws Exception {
        Connection connection = null;
        try {
            connection = DataSource.getHikari().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + tablename);
            ArrayList<IHighscoreDTO> highscorelist = new ArrayList<>();
            while (resultSet.next()) {
                IHighscoreDTO hangmanPOJO = makeHighscoreFromResultset(resultSet);
                highscorelist.add(hangmanPOJO);
            }
            return highscorelist;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void insertHighscore(String username, String word, int score) throws Exception {
        String inserString = "INSERT INTO " + tablename +
                "(username, word, score) VALUES (?,?,?);";

        Connection connection = null;
        try {
            connection = DataSource.getHikari().getConnection();
            PreparedStatement statement = connection.prepareStatement(inserString);
            statement.setString(1, username);
            statement.setString(2, word);
            statement.setInt(3, score);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            e.getMessage();
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dropSchema() throws DALException {
        Connection connection = null;
        String drop = "DROP TABLE " + tablename;
        try {
            connection = DataSource.getHikari().getConnection();
            PreparedStatement statement = connection.prepareStatement(drop);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException(e.getMessage());
        }
    }

    @Override
    public void createSchema() throws Exception {
        Connection connection = null;
        String highscores = "CREATE TABLE if not exists " + tablename
                + " (" + id + " int PRIMARY KEY AUTO_INCREMENT, "
                + username + " varchar(45), " + word + " varchar(45), "
                + score + " varchar(45) )";
        try {
            connection = DataSource.getHikari().getConnection();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(highscores);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DALException(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public IHighscoreDTO makeHighscoreFromResultset(ResultSet resultset) throws Exception {
        IHighscoreDTO highscoreDAL = new HighscoreDTO();
        highscoreDAL.setUsername(resultset.getString("username"));
        highscoreDAL.setWord(resultset.getString("word"));
        highscoreDAL.setScore(resultset.getInt("score"));
        return highscoreDAL;
    }
}