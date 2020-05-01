package database.dao;

import database.IDataSource;
import database.exceptions.NoModificationException;
import database.dto.PlaygroundDTO;
import com.mongodb.*;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class PlaygroundDAO implements IPlaygroundDAO {

    private IDataSource dataSource;

    public PlaygroundDAO(IDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Create playground in db
     * @param playground to be created
     * @return writeResult where upsertedId can be derived
     * @throws IllegalArgumentException when playground is null
     * @throws NoModificationException when playground is not created
     */
    @Override
    public WriteResult createPlayground(PlaygroundDTO playground) throws IllegalArgumentException, NoModificationException {
        if (playground == null)
            throw new IllegalArgumentException(
                    String.format("Can't create playground in %s collection when playground is null", COLLECTION));

        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        WriteResult wr = collection.save(playground);

        if (wr.getN() == 0)
            throw new NoModificationException(
                    String.format("Playground can't be created in %s collection", COLLECTION));

        return wr;
    }

    /**
     * Get playground in db
     * @param playgroundName uniquely identifies a playground in db
     * @return playground with given username
     * @throws IllegalArgumentException when username is invalid
     * @throws NoSuchElementException when playground is not found in db
     */
    @Override
    public PlaygroundDTO getPlayground(String playgroundName) throws IllegalArgumentException, NoSuchElementException {
        if (playgroundName == null || playgroundName.isEmpty())
            throw new IllegalArgumentException(
                    String.format("%s as ID is not valid in identifying an playground", playgroundName));

        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        PlaygroundDTO playground = collection.findOne("{name : #}", playgroundName).as(PlaygroundDTO.class);

        if (playground == null)
            throw new NoSuchElementException(
                    String.format("No playground in %s collection with name %s", COLLECTION, playground));

        return playground;
    }

    /**
     * Get list of all playgrounds in db
     * @return list of playgrounds
     * @throws NoSuchElementException when no playgrounds are found in db
     */
    @Override
    public List<PlaygroundDTO> getPlaygroundList() throws NoSuchElementException {
        List<PlaygroundDTO> playgrounds = new ArrayList<>();
        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCursor<PlaygroundDTO> all = jongo.getCollection(COLLECTION).find("{}").as(PlaygroundDTO.class);
        while (all.hasNext()) {
            playgrounds.add(all.next());
        }

        if (playgrounds.isEmpty())
            throw new NoSuchElementException(
                    String.format("No playgrounds in %s collection", COLLECTION));

        return playgrounds;
    }

    /**
     * Update playground in db
     * @param playground with updated values
     * @return writeResult where id of updated playground can be derived
     * @throws IllegalArgumentException when playground is null
     * @throws NoModificationException when no playground is updated
     */
    @Override
    public WriteResult updatePlayground(PlaygroundDTO playground) throws IllegalArgumentException, NoModificationException {
        if (playground == null || playground.getId() == null)
            throw new IllegalArgumentException(
                    String.format("Can't update playground in %s collection when param is null", COLLECTION));

        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        WriteResult wr = collection
                .update(new ObjectId(playground.getId()))
                .with(playground);

        if (!wr.wasAcknowledged())
            throw new NoModificationException(
                    String.format("Playground in %s collection with id %s was not updated", COLLECTION, playground.getId()));

        return wr;
    }

    /**
     * Delete playground in db
     * @param playgroundName uniquely identifies a playground in db
     * @return writeResult where id of deleted playground can be derived
     * @throws IllegalArgumentException when playgroundName is invalid
     * @throws NoModificationException when no playground is deleted
     */
    @Override
    public WriteResult deletePlayground(String playgroundName) throws IllegalArgumentException, NoModificationException {
        if (playgroundName == null || playgroundName.isEmpty())
            throw new IllegalArgumentException(
                    String.format("%s as ID is not valid in identifying an playground", playgroundName));

        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        WriteResult wr = collection.remove("{name : #}", playgroundName);

        if (!wr.wasAcknowledged())
            throw new NoModificationException(
                    String.format("No playground in %s collection with id %s was deleted", COLLECTION, playgroundName));

        return wr;
    }

    /**
     * Delete all playgrounds in db
     * @return writeResult where ids of deleted events can be derived
     */
    public WriteResult deleteAllPlaygrounds() {
        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        return collection.remove("{}");
    }

    @Override
    public void setDataSource(IDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
