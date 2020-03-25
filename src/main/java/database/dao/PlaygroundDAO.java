package database.dao;

import database.DALException;
import database.DataSource;
import database.collections.Playground;
import com.mongodb.*;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import static org.jongo.Oid.withOid;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlaygroundDAO implements IPlaygroundDAO {
    private final String COLLECTION = IPlaygroundDAO.COLLECTION;
    private String MongoQueryTag = "_id";

    @Override
    public void createPlayground(Playground playground) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        System.out.println(jongo.getCollection(COLLECTION).save(playground));
    }

    @Override
    public Playground getPlayground(String playgroundName) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        Playground playground = collection.findOne("{name : #}", playgroundName).as(Playground.class);
        return playground;
    }

    @Override
    public List<Playground> getPlaygroundList() throws DALException {
        List<Playground> playgrounds = null;
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCursor<Playground> all = jongo.getCollection(COLLECTION).find("{}").as(Playground.class);
        try {
            playgrounds = new ArrayList<>();
            while (all.hasNext()) {
                playgrounds.add(all.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playgrounds;
    }

    @Override
    public boolean updatePlayground(Playground playground) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        try {
            WriteResult result = jongo.getCollection(COLLECTION).save(playground);
            System.out.println(result);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deletePlayground(String playgroundName) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        try {
            System.out.println(jongo.getCollection(COLLECTION)
                    .remove("{name : #}", playgroundName));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAllPlaygrounds() {
        Jongo jongo = new Jongo(DataSource.getDB());
        try {
            System.out.println(jongo.getCollection(COLLECTION)
                    .remove("{}"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
