package Database.dao;

import Database.DALException;
import Database.collections.Playground;
import com.google.gson.Gson;
import com.mongodb.Cursor;
import com.mongodb.DBCollection;

import java.util.ArrayList;
import java.util.List;

public class PlaygroundDAO implements IPlaygroundDAO {
    @Override
    public void createPlayground() throws DALException {

    }

    @Override
    public Playground getPlayground() throws DALException {
        return null;
    }

    @Override
    public List<Playground> getPlaygroundList() throws DALException {
        List<Playground> playgroundsFromDB = null;
       /* DBCollection collection = getDB().getCollection("playgrounds");
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
        }*/

        return playgroundsFromDB;
    }


    @Override
    public boolean updatePlayground(Playground playground) throws DALException {
        return false;
    }

    @Override
    public boolean deletePlayground(int id) throws DALException {
        return false;
    }
}
