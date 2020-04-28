package database.dao;

import com.mongodb.WriteResult;
import database.DALException;
import database.NoModificationException;
import database.collections.Event;

import java.util.List;
import java.util.NoSuchElementException;

public interface IEventDAO {
    String COLLECTION = "events";

    WriteResult createEvent(Event event) throws IllegalArgumentException, NoModificationException;

    Event getEvent(String id) throws IllegalArgumentException, NoSuchElementException;

    List<Event> getEventList() throws NoSuchElementException;

    WriteResult updateEvent(Event event) throws IllegalArgumentException, NoModificationException;

    WriteResult deleteEvent(String id) throws IllegalArgumentException, NoModificationException;

    WriteResult deleteAllEvents();
}
