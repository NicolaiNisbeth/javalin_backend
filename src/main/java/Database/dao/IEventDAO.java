package Database.dao;

import Database.DALException;
import Database.collections.Event;
import Database.collections.User;

import java.util.List;

public interface IEventDAO {
    String COLLECTION = "events";

    boolean createEvent(Event event)       throws DALException;
    Event getEvent(String id)           throws DALException;
    List<Event> getEventList()          throws DALException;
    boolean updateEvent(Event event)    throws DALException;
    boolean deleteEvent(String id)      throws DALException;
}
