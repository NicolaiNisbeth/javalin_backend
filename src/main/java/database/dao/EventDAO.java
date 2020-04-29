package database.dao;

import com.mongodb.WriteResult;
import database.IDataSource;
import database.exceptions.NoModificationException;
import database.collections.Event;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class EventDAO implements IEventDAO {
    private IDataSource dataSource;

    public EventDAO(IDataSource dataSource){
        this.dataSource = dataSource;
    }

    /**
     * Create event in db
     * @param event to be created
     * @return writeResult where upsertedId can be derived
     * @throws IllegalArgumentException when event is null
     * @throws NoModificationException when event is not created
     */
    @Override
    public WriteResult createEvent(Event event) throws IllegalArgumentException, NoModificationException {
        if (event == null)
            throw new IllegalArgumentException(
                    String.format("Can't create event in %s collection when event is null", COLLECTION));

        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        WriteResult wr = collection.save(event);

        if (wr.getN() == 0)
            throw new NoModificationException(
                    String.format("Playground can't be created in %s collection", COLLECTION));

        return wr;
    }

    /**
     * Get event in db
     * @param id uniquely identifies a user in db
     * @return event with given id
     * @throws IllegalArgumentException when id is invalid
     * @throws NoSuchElementException when event is not found in db
     */
    @Override
    public Event getEvent(String id) throws IllegalArgumentException, NoSuchElementException {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException(
                    String.format("%s as ID is not valid in identifying an event", id));

        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        Event event = collection.findOne(new ObjectId(id)).as(Event.class);

        if (event == null)
            throw new NoSuchElementException(
                    String.format("No event in %s collection with id %s", COLLECTION, id));

        return event;
    }

    /**
     * Get list of all events in db
     * @return list of events
     * @throws NoSuchElementException when no events are found in db
     */
    @Override
    public List<Event> getEventList() throws NoSuchElementException {
        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        List<Event> eventList = new ArrayList<>();
        for (Event event : collection.find().as(Event.class)) {
            eventList.add(event);
        }

        if (eventList.isEmpty())
            throw new NoSuchElementException(
                    String.format("No events in %s collection", COLLECTION));

        return eventList;
    }

    /**
     * Update event in db
     * @param event with updated values
     * @return writeResult where id of updated event can be derived
     * @throws IllegalArgumentException when event is null
     * @throws NoModificationException when no event is updated
     */
    @Override
    public WriteResult updateEvent(Event event) throws IllegalArgumentException, NoModificationException {
        if (event == null || event.getId() == null)
            throw new IllegalArgumentException(
                    String.format("Can't update event in %s collection when param is null", COLLECTION));

        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        WriteResult wr = collection
                .update(new ObjectId(event.getId()))
                .with(event);

        if (!wr.wasAcknowledged())
            throw new NoModificationException(
                    String.format("Event in %s collection with id %s was not updated", COLLECTION, event.getId()));

        return wr;
    }

    /**
     * Delete event in db
     * @param id uniquely identifies an event in db
     * @return writeResult where id of deleted user can be derived
     * @throws NoModificationException when no event is deleted
     * @throws IllegalArgumentException when id is invalid
     */
    @Override
    public WriteResult deleteEvent(String id) throws IllegalArgumentException, NoModificationException {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException(
                    String.format("%s as ID is not valid in identifying an event", id));

        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        WriteResult wr = collection.remove(new ObjectId(id));

        if (!wr.wasAcknowledged())
            throw new NoModificationException(
                    String.format("No event in %s collection with id %s was deleted", COLLECTION, id));

        return wr;
    }

    /**
     * Delete all events in db
     * @return writeResult where ids of deleted events can be derived
     */
    @Override
    public WriteResult deleteAllEvents(){
        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        return collection.remove("{}");
    }

    @Override
    public void setDataSource(IDataSource dataSource) {
        this.dataSource = dataSource;
    }
}