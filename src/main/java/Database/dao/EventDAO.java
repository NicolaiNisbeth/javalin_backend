package Database.dao;

import Database.DALException;
import Database.collections.Event;

import java.util.List;

public class EventDAO implements IEventDAO {
    @Override
    public void createEvent() throws DALException {

    }

    @Override
    public Event getEvent() throws DALException {
        return null;
    }

    @Override
    public List<Event> getEventList() throws DALException {
        return null;
    }

    @Override
    public boolean updateEvent(Event event) throws DALException {
        return false;
    }

    @Override
    public boolean deleteEvent(int id) throws DALException {
        return false;
    }
}
