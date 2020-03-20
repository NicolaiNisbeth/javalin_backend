/*
package Database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HighscoreDAO {

    private final String COLLECTION = "Test1";
    private final String MongoQueryTag = "wordToGuess";

    public void addHighScoreName(Score score){
        Jongo jongo = new Jongo(DataSource2.getInstance());

        MongoCollection test =  jongo.getCollection(COLLECTION);
        test.save(score);

    }

*
     * Retreve the highscore from a specified word.
     * @param wordToGuess
     * @return


    public Score getHighScoreWord(String wordToGuess){

        Jongo jongo = new Jongo(DataSource2.getInstance());

        MongoCollection test =  jongo.getCollection(COLLECTION);

        Score username = test.findOne("{'"+MongoQueryTag+"': '"+wordToGuess+"'}").as(Score.class);

        return username;

    }

*
     * Delete a score from the database
     * @param wordToGuess



    public void deleteHighScoreWord(String wordToGuess){

        Jongo jongo = new Jongo(DataSource2.getInstance());

        MongoCollection test =  jongo.getCollection(COLLECTION);
        test.remove("{'"+MongoQueryTag+"': '" + wordToGuess + "'}");
    }


*
     * Returns all highscores from the database
     * @return List<Data.Score></Data.Score>



    public List<Score> getAllHighScoreWords(){
        Jongo jongo = new Jongo(DataSource2.getInstance());

        MongoCollection test =  jongo.getCollection(COLLECTION);

        List<Score> scores = new ArrayList<Score>();

        Iterator<Score> items = test.find().as(Score.class).iterator();

        while(items.hasNext()){
            scores.add(items.next());
        }

        return scores;
    }

    public void clearDatabase(){
        Jongo jongo = new Jongo(DataSource2.getInstance());
        MongoCollection test =  jongo.getCollection(COLLECTION);

        for (Score i: getAllHighScoreWords()) {
            deleteHighScoreWord(i.getWord());
        }

    }


}
*/
