package database.dao;

import com.mongodb.WriteResult;
import database.IDataSource;
import database.exceptions.NoModificationException;
import database.collections.Event;

import java.util.List;
import java.util.NoSuchElementException;

public interface IEventDAO {
    String COLLECTION = "events";

    void setDataSource(IDataSource dataSource);

    WriteResult createEvent(Event event) throws IllegalArgumentException, NoModificationException;

    Event getEvent(String id) throws IllegalArgumentException, NoSuchElementException;

    List<Event> getEventList() throws NoSuchElementException;

    WriteResult updateEvent(Event event) throws IllegalArgumentException, NoModificationException;

    WriteResult deleteEvent(String id) throws IllegalArgumentException, NoModificationException;

    WriteResult deleteAllEvents();
}
