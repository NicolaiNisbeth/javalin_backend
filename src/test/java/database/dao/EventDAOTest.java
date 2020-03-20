package database.dao;

import database.DALException;
import database.collections.Details;
import database.collections.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

class EventDAOTest {
    private EventDAO eventDAO;

    @BeforeEach
    void setUp() {
        eventDAO = new EventDAO();
    }

    @AfterEach
    void tearDown() {
        eventDAO = null;
    }

    @Test
    void createEvent_DeleteEvent() throws DALException {
        Event event = new Event.Builder("Football")
                .imagePath("asdasd9asdsad.jpg")
                .participants(20)
                .description("Football near the bay...")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        eventDAO.createEvent(event);

        Event fetchedEvent = eventDAO.getEvent(event.getId());
        Assertions.assertEquals(event, fetchedEvent);

        eventDAO.deleteEvent(event.getId());

        // try to fetch deleted event and confirm that exception is thrown with msg: no event
        DALException thrown = Assertions.assertThrows(
                DALException.class, () -> eventDAO.getEvent(event.getId())
        );
        Assertions.assertTrue(thrown.getMessage().contains("No event"));
    }

    @Test
    void createEvents_getEventList_deleteEvents() throws DALException {
        Event event1 = new Event.Builder("Football")
                .imagePath("asdasd9asdsad.jpg")
                .participants(20)
                .description("Football near the bay...")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        Event event2 = new Event.Builder("Boardgames")
                .imagePath("asd23asd9asds23ad.jpg")
                .participants(3)
                .description("Boardgames in library...")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        eventDAO.createEvent(event1);
        eventDAO.createEvent(event2);

        List<Event> eventList = eventDAO.getEventList();
        Assertions.assertEquals(eventList.size(), 2);

        Assertions.assertEquals(eventList.get(0), event1);
        Assertions.assertEquals(eventList.get(1), event2);

        eventDAO.deleteEvent(event1.getId());
        eventDAO.deleteEvent(event2.getId());
    }

    @Test
    void createEvent_UpdateEvent_deleteEvent() throws DALException {
        Event event = new Event.Builder("Football")
                .imagePath("asdasd9asdsad.jpg")
                .participants(20)
                .description("Football near the bay...")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        eventDAO.createEvent(event);

        event.setParticipants(40);
        eventDAO.updateEvent(event);

        Event updatedEvent = eventDAO.getEvent(event.getId());
        Assertions.assertEquals(40, updatedEvent.getParticipants());

        eventDAO.deleteEvent(event.getId());
    }

    @Disabled("This test is disabled because it deletes all events in collection")
    @Test
    void deleteAllUsersInCollection() throws DALException {
        for (Event i: eventDAO.getEventList()) {
            eventDAO.deleteEvent(i.getId());
        }
    }
}