package Database.dao;

import Database.DALException;
import Database.collections.Event;
import Database.collections.User;

import java.util.List;

public interface IEventDAO {
    String COLLECTION = "events";


    void createEvent()                   throws DALException;
    Event getEvent()                     throws DALException;
    List<Event> getEventList()           throws DALException;
    boolean updateEvent(Event event)     throws DALException;
    boolean deleteEvent(int id)          throws DALException;
}
