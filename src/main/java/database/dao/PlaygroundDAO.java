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

        if (playground == null)
            throw new DALException(String.format("No playground in %s collection with playgroundname %s", COLLECTION, playgroundName));

        return playground;
    }

    @Override
    public List<Playground> getPlaygroundList() throws DALException {
        List<Playground> playgrounds = new ArrayList<>();
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCursor<Playground> all = jongo.getCollection(COLLECTION).find("{}").as(Playground.class);

        while (all.hasNext()) {
            playgrounds.add(all.next());
        }

        if (playgrounds.isEmpty())
            throw new DALException(String.format("No playgrounds in %s collection", COLLECTION));

        return playgrounds;
    }

    @Override
    public boolean updatePlayground(Playground playground) throws DALException {
        if (playground == null)
            throw new DALException(String.format("Can't update playground in %s collection when param is null", COLLECTION));

        if (playground.getName() == null)
            throw new DALException(String.format("Can't find playground to be updated in %s collection when playgroundname is null", COLLECTION));

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
