package database.dao;

import com.mongodb.WriteResult;
import database.DALException;
import database.DataSource;
import database.collections.Event;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.util.ArrayList;
import java.util.List;

public class EventDAO implements IEventDAO{

    /**
     * Create event in events collection
     * @param event
     * @return true if created else false
     * @throws DALException
     */
    @Override
    public WriteResult createEvent(Event event) throws DALException {
        if (event == null)
            throw new DALException(String.format("Can't create event in %s collection when event is null", COLLECTION));

        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);

        WriteResult ws = collection.save(event);

        if (ws.getN() == 0)
            throw new DALException(String.format("Event can't be created in %s collection", COLLECTION));

        return ws;
    }

    /**
     * Get event with given id
     * @param id
     * @return event
     * @throws DALException
     */
    @Override
    public Event getEvent(String id) throws DALException {
        if (id == null || id.isBlank())
            throw new DALException(String.format("%s as ID is not valid in identifying an event", id));

        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);

        Event event = collection.findOne(new ObjectId(id)).as(Event.class);

        if (event == null)
            throw new DALException(String.format("No event in %s collection with id %s", COLLECTION, id));

        return event;
    }

    /**
     * Get all events in collection
     * @return list of events
     * @throws DALException
     */
    @Override
    public List<Event> getEventList() throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);

        List<Event> eventList = new ArrayList<>();
        for (Event event : collection.find().as(Event.class)) {
            eventList.add(event);
        }

        if (eventList.isEmpty())
            throw new DALException(String.format("No events in %s collection", COLLECTION));

        return eventList;
    }

    /**
     * Replace event with same id
     * @param event
     * @return true if updated else false
     * @throws DALException
     */
    @Override
    public boolean updateEvent(Event event) throws DALException {
        if (event == null)
            throw new DALException(String.format("Can't update event in %s collection when input event is null", COLLECTION));

        if (event.getId() == null)
            throw new DALException(String.format("Can't find event to be updated in %s collection when id is null", COLLECTION));

        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);

        boolean isEventUpdated = collection
                .update(new ObjectId(event.getId()))
                .with(event)
                .wasAcknowledged();

        if (!isEventUpdated)
            throw new DALException(String.format("No event in %s collection with id %s", COLLECTION, event.getId()));

        return true;
    }

    /**
     * Delete event with given id
     * @param id
     * @return true if deleted else false
     * @throws DALException
     */
    @Override
    public boolean deleteEvent(String id) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);

        boolean isEventDeleted = collection
                .remove(new ObjectId(id))
                .wasAcknowledged();

        if (!isEventDeleted)
            throw new DALException(String.format("No event in %s collection with id %s", COLLECTION, id));

        return true;
    }

    @Override
    public boolean deleteAllEvents() throws DALException {
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