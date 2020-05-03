package database.unit;

import com.mongodb.WriteResult;
import database.exceptions.NoModificationException;
import database.TestDB;
import database.dto.DetailsDTO;
import database.dto.EventDTO;
import database.dao.EventDAO;
import database.dao.IEventDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

class EventDAOTest {
    private static IEventDAO eventDAO = new EventDAO(TestDB.getInstance());

    @BeforeAll
    static void killAll(){
        eventDAO.deleteAllEvents();
    }

    @Test
    void createdEventShouldBeFetchedEvent() throws NoModificationException {
        EventDTO event = new EventDTO.Builder()
                .name("Football")
                .imagePath("asdasd9asdsad.jpg")
                .participants(20)
                .description("Football near the bay...")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        WriteResult ws = eventDAO.createEvent(event);
        EventDTO fetchedEvent = eventDAO.getEvent(ws.getUpsertedId().toString());
        Assertions.assertEquals(event, fetchedEvent);
        eventDAO.deleteEvent(ws.getUpsertedId().toString());
    }

    @Test
    void createTwoEventsShouldFetchListSizeTwo() throws NoModificationException {
        EventDTO event1 = new EventDTO.Builder()
                .name("Football")
                .imagePath("asdasd9asdsad.jpg")
                .participants(20)
                .description("Football near the bay...")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        EventDTO event2 = new EventDTO.Builder()
                .name("Boardgames")
                .imagePath("asd23asd9asds23ad.jpg")
                .participants(3)
                .description("Boardgames in library...")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        WriteResult ws1 = eventDAO.createEvent(event1);
        WriteResult ws2 = eventDAO.createEvent(event2);

        List<EventDTO> eventList = eventDAO.getEventList();
        Assertions.assertEquals(eventList.size(), 2);

        Assertions.assertEquals(eventList.get(0), event1);
        Assertions.assertEquals(eventList.get(1), event2);

        eventDAO.deleteEvent(ws1.getUpsertedId().toString());
        eventDAO.deleteEvent(ws2.getUpsertedId().toString());
    }

    @Test
    void updateEventShouldFetchUpdatedEvent() throws NoModificationException {
        EventDTO event = new EventDTO.Builder()
                .name("Football")
                .imagePath("asdasd9asdsad.jpg")
                .participants(20)
                .description("Football near the bay...")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        WriteResult ws = eventDAO.createEvent(event);
        event.setName("new name");
        eventDAO.updateEvent(event);

        EventDTO updatedEvent = eventDAO.getEvent(ws.getUpsertedId().toString());
        Assertions.assertEquals("new name", updatedEvent.getName());

        eventDAO.deleteEvent(ws.getUpsertedId().toString());
    }

    @Test
    void deleteAllEventsInCollection() throws NoModificationException {
        EventDTO event1 = new EventDTO.Builder()
                .name("Football")
                .imagePath("asdasd9asdsad.jpg")
                .participants(20)
                .description("Football near the bay...")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        EventDTO event2 = new EventDTO.Builder()
                .name("Boardgames")
                .imagePath("asd23asd9asds23ad.jpg")
                .participants(3)
                .description("Boardgames in library...")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        WriteResult ws = eventDAO.createEvent(event1);
        WriteResult ws2 = eventDAO.createEvent(event2);

        Assertions.assertNotNull(eventDAO.getEvent(ws.getUpsertedId().toString()));
        Assertions.assertNotNull(eventDAO.getEvent(ws2.getUpsertedId().toString()));

        for (EventDTO i: eventDAO.getEventList()) {
            eventDAO.deleteEvent(i.getID());
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