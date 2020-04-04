package database.dao;

import com.mongodb.WriteResult;
import database.DALException;
import database.collections.Event;

import java.util.List;

public interface IEventDAO {
    String COLLECTION = "events";

    WriteResult createEvent(Event event) throws DALException;

    Event getEvent(String id) throws DALException;

    List<Event> getEventList() throws DALException;

    boolean updateEvent(Event event) throws DALException;

    boolean deleteEvent(String id) throws DALException;

    boolean deleteAllEvents() throws DALException;
}
