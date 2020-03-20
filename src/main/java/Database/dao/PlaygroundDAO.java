package Database.dao;

import Database.DALException;
import Database.DataSource;
import Database.collections.Playground;
import com.mongodb.*;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCursor;

import static org.jongo.Oid.withOid;


import java.util.ArrayList;
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
    public Playground getPlayground(String id) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        return jongo.getCollection(COLLECTION).findOne(withOid(id)).as(Playground.class);
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
    public boolean deletePlayground(String id) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        try {
            System.out.println(jongo.getCollection(COLLECTION)
                    .remove(new ObjectId(id)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAllPlaygrounds() {
        Jongo jongo = new Jongo(DataSource.getDB());
        try {
            jongo.getCollection(COLLECTION)
                    .remove("{}");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
