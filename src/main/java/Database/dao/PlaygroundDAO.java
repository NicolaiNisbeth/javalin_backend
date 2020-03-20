package Database.dao;

import Database.DALException;
import Database.DataSource;
import Database.collections.Playground;
import com.google.gson.Gson;
import com.mongodb.Cursor;
import com.mongodb.DBCollection;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.util.ArrayList;
import java.util.List;

public class PlaygroundDAO implements IPlaygroundDAO {

    @Override
    public void createPlayground(Playground playground) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection test =  jongo.getCollection(COLLECTION);
        test.save(playground);
    }

    @Override
    public Playground getPlayground(String id) throws DALException {


        return null;
    }

    @Override
    public List<Playground> getPlaygroundList() throws DALException {
        List<Playground> playgroundsFromDB = null;
        DBCollection collection = DataSource.getDB().getCollection("playgrounds");
        Gson gson = new Gson();
        try {
            Cursor cursor = collection.find();
            playgroundsFromDB = new ArrayList<>();
            Playground playgroundFromDB = null;

            while (cursor.hasNext()) {
                playgroundFromDB = gson.fromJson(cursor.next().toString(), Playground.class);
                playgroundsFromDB.add(playgroundFromDB);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return playgroundsFromDB;
    }


    @Override
    public boolean updatePlayground(Playground playground) throws DALException {
        return false;
    }

    @Override
    public boolean deletePlayground(String id) throws DALException {
        return false;
    }

}
