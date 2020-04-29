package database.unit;

import com.mongodb.WriteResult;
import database.DALException;
import database.DataSource;
import database.NoModificationException;
import database.collections.Details;
import database.collections.Event;
import database.dao.EventDAO;
import database.dao.IEventDAO;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

class EventDAOTest {
    private static IEventDAO eventDAO = new EventDAO(DataSource.getTestDB());

    @BeforeAll
    static void killAll(){
        eventDAO.deleteAllEvents();
    }

    @Test
    void createdEventShouldBeFetchedEvent() throws NoModificationException {
        Event event = new Event.Builder()
                .name("Football")
                .imagePath("asdasd9asdsad.jpg")
                .participants(20)
                .description("Football near the bay...")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        WriteResult ws = eventDAO.createEvent(event);
        Event fetchedEvent = eventDAO.getEvent(ws.getUpsertedId().toString());
        Assertions.assertEquals(event, fetchedEvent);
        eventDAO.deleteEvent(ws.getUpsertedId().toString());
    }

    @Test
    void createTwoEventsShouldFetchListSizeTwo() throws NoModificationException {
        Event event1 = new Event.Builder()
                .name("Football")
                .imagePath("asdasd9asdsad.jpg")
                .participants(20)
                .description("Football near the bay...")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        Event event2 = new Event.Builder()
                .name("Boardgames")
                .imagePath("asd23asd9asds23ad.jpg")
                .participants(3)
                .description("Boardgames in library...")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        WriteResult ws1 = eventDAO.createEvent(event1);
        WriteResult ws2 = eventDAO.createEvent(event2);

        List<Event> eventList = eventDAO.getEventList();
        Assertions.assertEquals(eventList.size(), 2);

        Assertions.assertEquals(eventList.get(0), event1);
        Assertions.assertEquals(eventList.get(1), event2);

        eventDAO.deleteEvent(ws1.getUpsertedId().toString());
        eventDAO.deleteEvent(ws2.getUpsertedId().toString());
    }

    @Test
    void updateEventShouldFetchUpdatedEvent() throws NoModificationException {
        Event event = new Event.Builder()
                .name("Football")
                .imagePath("asdasd9asdsad.jpg")
                .participants(20)
                .description("Football near the bay...")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        WriteResult ws = eventDAO.createEvent(event);
        event.setName("new name");
        eventDAO.updateEvent(event);

        Event updatedEvent = eventDAO.getEvent(ws.getUpsertedId().toString());
        Assertions.assertEquals("new name", updatedEvent.getName());

        eventDAO.deleteEvent(ws.getUpsertedId().toString());
    }

    @Test
    void deleteAllEventsInCollection() throws NoModificationException {
        Event event1 = new Event.Builder()
                .name("Football")
                .imagePath("asdasd9asdsad.jpg")
                .participants(20)
                .description("Football near the bay...")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        Event event2 = new Event.Builder()
                .name("Boardgames")
                .imagePath("asd23asd9asds23ad.jpg")
                .participants(3)
                .description("Boardgames in library...")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        WriteResult ws = eventDAO.createEvent(event1);
        WriteResult ws2 = eventDAO.createEvent(event2);

        Assertions.assertNotNull(eventDAO.getEvent(ws.getUpsertedId().toString()));
        Assertions.assertNotNull(eventDAO.getEvent(ws2.getUpsertedId().toString()));

        for (Event i: eventDAO.getEventList()) {
            eventDAO.deleteEvent(i.getId());
        }

        Assertions.assertThrows(NoSuchElementException.class, () -> eventDAO.getEventList());
    }

    @Test
    void nullInCreateShouldThrowIllegalArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventDAO.createEvent(null));
    }

    @Test
    void nullInGetShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventDAO.getEvent(null));
    }
    @Test
    void emptyIdInGetShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventDAO.getEvent(""));
    }

    @Test
    void noEventsInGetEventsShouldThrowNoSuchElements(){
        Assertions.assertThrows(NoSuchElementException.class, () -> eventDAO.getEventList());
    }

    @Test
    void nullInUpdateShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventDAO.updateEvent(null));
    }

    @Test
    void nullInDeleteShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventDAO.deleteEvent(null));
    }

    @Test
    void emptyIdInDeleteShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> eventDAO.deleteEvent(""));
    }
}