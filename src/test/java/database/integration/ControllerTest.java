package database.integration;

import com.mongodb.WriteResult;
import database.Controller;
import database.IController;
import database.exceptions.NoModificationException;
import database.TestDB;
import database.dto.*;
import org.junit.jupiter.api.*;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    static IController controller = Controller.getInstance();

    @BeforeAll
    static void killAll(){
        controller.setDataSource(TestDB.getInstance());
        controller.killAll();
    }

    @Test
    @DisplayName("Create and delete playground")
    void createdPlaygroundShouldBeFetchedPlayground() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();


        // check that playground is NOT present in db
        Assertions.assertThrows(NoSuchElementException.class, () -> controller.getPlayground(playground.getName()));

        controller.createPlayground(playground);
        PlaygroundDTO fetchedPlayground = controller.getPlayground(playground.getName());

        // check that playground is present in db
        Assertions.assertAll(
                () -> Assertions.assertNotNull(fetchedPlayground),
                () -> assertEquals(playground, fetchedPlayground)
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    @DisplayName("Create and delete user")
    void createdUserShouldBeFetchedUser() throws NoModificationException {
        UserDTO user = new UserDTO.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        // check that user is NOT present in db
        Assertions.assertThrows(NoSuchElementException.class, () -> controller.getUser(user.getUsername()));

        controller.createUser(user);
        UserDTO fetchedUser = controller.getUser(user.getUsername());

        // check that playground is present in db
        Assertions.assertAll(
                () -> Assertions.assertNotNull(user),
                () -> assertEquals(user, fetchedUser)
        );

        controller.deleteUser(user.getUsername());
    }

    @Test
    void addedAssociationsShouldBeFetchedAssociations() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        UserDTO user = new UserDTO.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        EventDTO playgroundEvent = new EventDTO.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        MessageDTO playgroundMessage = new MessageDTO.Builder()
                .setCategory("Networking")
                .setIcon("asdasdads.jpg")
                .setOutDated(false)
                .setMessageString("I would like the parents to show up....")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        controller.createPlayground(playground);
        controller.createUser(user);

        // add associations
        controller.addPedagogueToPlayground(playground.getName(), user.getUsername());
        controller.createPlaygroundEvent(playground.getName(), playgroundEvent);
        controller.createPlaygroundMessage(playground.getName(), playgroundMessage);

        // get playground
        PlaygroundDTO fetchedPlayground = controller.getPlayground(playground.getName());

        // check references
        Assertions.assertAll(
                () -> assertEquals(user, fetchedPlayground.getAssignedPedagogue().iterator().next()),
                () -> assertEquals(playgroundEvent, fetchedPlayground.getEvents().iterator().next()),
                () -> assertEquals(playgroundMessage, fetchedPlayground.getMessages().iterator().next())
        );

        List<PlaygroundDTO> list = controller.getPlaygrounds();

        controller.deleteUser(user.getUsername());
        controller.deletePlayground(playground.getName());
    }

    @Test
    void addedEventToUserShouldBeInUserEventList() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        UserDTO user = new UserDTO.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        EventDTO event1 = new EventDTO.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        EventDTO event2 = new EventDTO.Builder()
                .name("Fangeleg")
                .description("Fangeleg rundt om Snorrstrup sø")
                .participants(10)
                .imagePath("asdasdsa9d8dsa.jpg")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        WriteResult e1 = controller.createPlaygroundEvent(playground.getName(), event1);
        WriteResult e2 = controller.createPlaygroundEvent(playground.getName(), event2);

        WriteResult u1 = controller.createUser(user);
        controller.addUserToEvent(e1.getUpsertedId().toString(), user.getUsername());
        controller.addUserToEvent(e2.getUpsertedId().toString(), user.getUsername());

        // get user
        UserDTO fetchedUser = controller.getUser(user.getUsername());
        EventDTO fetchedEvent1 = controller.getEvent(e1.getUpsertedId().toString());
        EventDTO fetchedEvent2 = controller.getEvent(e2.getUpsertedId().toString());

        // check associations
        Assertions.assertAll(
                () -> assertEquals(2, fetchedUser.getEvents().size()),
                () -> assertEquals(fetchedUser ,fetchedEvent1.getAssignedUsers().iterator().next()),
                () -> assertEquals(fetchedUser ,fetchedEvent2.getAssignedUsers().iterator().next())
        );

        controller.deleteUser(user.getUsername());
        controller.deletePlayground(playground.getName());
    }

    @Test
    void getEvent() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        UserDTO user = new UserDTO.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        EventDTO event = new EventDTO.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        WriteResult ws = controller.createPlaygroundEvent(playground.getName(), event);

        controller.createUser(user);
        controller.addUserToEvent(ws.getUpsertedId().toString(), user.getUsername());

        // get event
        EventDTO fetchedEvent = controller.getEvent(ws.getUpsertedId().toString());

        // check references
        Assertions.assertAll(
                () -> assertEquals(playground.getName(), fetchedEvent.getPlaygroundName()),
                () -> assertEquals(user , fetchedEvent.getAssignedUsers().iterator().next())
        );

        controller.deleteUser(user.getUsername());
        controller.deletePlayground(playground.getName());
    }

    @Test
    void createdPlaygroundMessageShouldHavePlaygroundID() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        MessageDTO playgroundMessage = new MessageDTO.Builder()
                .setCategory("Networking")
                .setIcon("asdasdads.jpg")
                .setOutDated(false)
                .setMessageString("I would like the parents to show up....")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        controller.createPlayground(playground);
        controller.createPlaygroundMessage(playground.getName(), playgroundMessage);

        // get message
        PlaygroundDTO fetchedPlayground = controller.getPlayground(playground.getName());
        MessageDTO message = fetchedPlayground.getMessages().iterator().next();

        // check references
        Assertions.assertAll(
                () -> assertEquals(playground.getName(), message.getPlaygroundName())
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void addedEventShouldBeInPlayground() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        EventDTO playgroundEvent1 = new EventDTO.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        EventDTO playgroundEvent2 = new EventDTO.Builder()
                .name("Fangeleg")
                .description("Fangeleg rundt om Snorrstrup sø")
                .participants(10)
                .imagePath("asdasdsa9d8dsa.jpg")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        controller.createPlaygroundEvent(playground.getName(), playgroundEvent1);
        controller.createPlaygroundEvent(playground.getName(), playgroundEvent2);

        List<EventDTO> eventList = controller.getEventsInPlayground(playground.getName());

        Assertions.assertAll(
                () -> assertEquals(playgroundEvent1, eventList.get(0)),
                () -> assertEquals(playgroundEvent2, eventList.get(1))
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void addedMessagesShouldBeInPlayground() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        MessageDTO playgroundMessage1 = new MessageDTO.Builder()
                .setCategory("Networking")
                .setIcon("asdasdads.jpg")
                .setOutDated(false)
                .setMessageString("I would like the parents to show up....")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        MessageDTO playgroundMessage2 = new MessageDTO.Builder()
                .setCategory("Corona")
                .setIcon("asdasd2323ads.jpg")
                .setOutDated(false)
                .setMessageString("Corono is le good shit")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        controller.createPlayground(playground);
        controller.createPlaygroundMessage(playground.getName(), playgroundMessage1);
        controller.createPlaygroundMessage(playground.getName(), playgroundMessage2);

        List<MessageDTO> messageList = controller.getMessagesInPlayground(playground.getName());
        Assertions.assertAll(
                () -> assertEquals(playgroundMessage1, messageList.get(0)),
                () -> assertEquals(playgroundMessage2, messageList.get(1))
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void updatePlaygroundEvent() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        EventDTO playgroundEvent = new EventDTO.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .imagePath("asd98asd89asd.jpg")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        controller.createPlaygroundEvent(playground.getName(), playgroundEvent);

        // check that playground and event is present in db, and the associations between them
        PlaygroundDTO fetchedPlayground = controller.getPlayground(playground.getName());
        EventDTO fetchedEvent = fetchedPlayground.getEvents().iterator().next();
        Assertions.assertAll(
                () -> assertEquals(fetchedPlayground.getName(), fetchedEvent.getPlaygroundName()),
                () -> assertEquals(fetchedEvent, playgroundEvent)
        );

        // update values
        fetchedEvent.setDescription("Fodbold og snobrød i Søndermarken");
        fetchedEvent.setImagepath("as9d89a8sd98sa98dsa.jpg");
        controller.updatePlaygroundEvent(fetchedEvent);

        // check that event has updated values
        EventDTO updatedEvent = controller.getEvent(fetchedEvent.getID());
        Assertions.assertAll(
                () -> assertEquals(fetchedEvent.getDescription(), updatedEvent.getDescription()),
                () -> assertEquals(fetchedEvent.getParticipants(), updatedEvent.getParticipants()),
                () -> assertEquals(fetchedEvent.getImagepath(), updatedEvent.getImagepath())
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void updatePlaygroundMessage() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        MessageDTO playgroundMessage = new MessageDTO.Builder()
                .setCategory("Networking")
                .setIcon("asdasdads.jpg")
                .setOutDated(false)
                .setMessageString("I would like the parents to show up....")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        controller.createPlayground(playground);
        controller.createPlaygroundMessage(playground.getName(), playgroundMessage);

        // check that playground and event is present in db, and the associations between them
        PlaygroundDTO fetchedPlayground = controller.getPlayground(playground.getName());
        MessageDTO fetchedMessage = fetchedPlayground.getMessages().iterator().next();
        Assertions.assertAll(
                () -> assertEquals(fetchedPlayground.getName(), fetchedMessage.getPlaygroundName()),
                () -> assertEquals(fetchedMessage, playgroundMessage)
        );

        // update values
        fetchedMessage.setCategory("Alternativ leg");
        fetchedMessage.setIcon("as9dsa98dsa98d98sa.jpg");
        fetchedMessage.setMessageString("Ny forskning viser at børn under ....");
        controller.updatePlaygroundMessage(fetchedMessage);

        // check that event has updated values
        MessageDTO updatedMessage = controller.getMessage(fetchedMessage.getID());
        Assertions.assertAll(
                () -> assertEquals(fetchedMessage.getCategory(), updatedMessage.getCategory()),
                () -> assertEquals(fetchedMessage.getIcon(), updatedMessage.getIcon()),
                () -> assertEquals(fetchedMessage.getMessageString(), updatedMessage.getMessageString())
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void deletePlaygroundShouldRemoveReferenceInUser() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        UserDTO user = new UserDTO.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        EventDTO playgroundEvent = new EventDTO.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        MessageDTO playgroundMessage = new MessageDTO.Builder()
                .setCategory("Networking")
                .setIcon("asdasdads.jpg")
                .setOutDated(false)
                .setMessageString("I would like the parents to show up....")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        // setup
        controller.createPlayground(playground);
        controller.createUser(user);
        controller.addPedagogueToPlayground(playground.getName(), user.getUsername());
        controller.createPlaygroundEvent(playground.getName(), playgroundEvent);
        controller.createPlaygroundMessage(playground.getName(), playgroundMessage);

        // check all the references
        PlaygroundDTO fetchedPlayground = controller.getPlayground(playground.getName());
        UserDTO playPovPedagogue = fetchedPlayground.getAssignedPedagogue().iterator().next();
        EventDTO playPovEvent = fetchedPlayground.getEvents().iterator().next();
        MessageDTO playPovMessage = fetchedPlayground.getMessages().iterator().next();

        UserDTO fetchedPedagogue = controller.getUser(playPovPedagogue.getUsername());
        EventDTO fetchedEvent = controller.getEvent(playPovEvent.getID());
        MessageDTO fetchedMessage = controller.getMessage(playPovMessage.getID());

        Assertions.assertAll(
                () -> assertEquals(playPovPedagogue.getUsername(), fetchedPedagogue.getUsername()),
                () -> assertEquals(playPovEvent.getID(), fetchedEvent.getID()),
                () -> assertEquals(playPovMessage.getID(), fetchedMessage.getID())
        );

        // delete playground
        controller.deletePlayground(playground.getName());

        // check that the references are gone
        UserDTO updatedUser = controller.getUser(playPovPedagogue.getUsername());
        Assertions.assertAll(() -> assertFalse(updatedUser.getPlaygroundsIDs().iterator().hasNext()));

        controller.deleteUser(updatedUser.getUsername());
    }

    @Test
    void deleteUserShouldRemoveReferenceInPlayground() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        UserDTO user = new UserDTO.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        EventDTO playgroundEvent = new EventDTO.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        controller.createUser(user);

        // add references
        controller.addPedagogueToPlayground(playground.getName(), user.getUsername());

        WriteResult eventResult = controller.createPlaygroundEvent(playground.getName(), playgroundEvent);
        String eventID = eventResult.getUpsertedId().toString();
        controller.addUserToEvent(eventID, user.getUsername());


        // confirm the different references
        UserDTO fetchedUser = controller.getUser(user.getUsername());
        PlaygroundDTO fetchedPlayground = controller.getPlayground(playground.getName());
        EventDTO fetchedEvent = controller.getEvent(eventID);

        Assertions.assertAll(
                () -> assertEquals(eventID, fetchedUser.getEvents().iterator().next().getID()),
                () -> assertEquals(playground.getName(), fetchedUser.getPlaygroundsIDs().iterator().next()),
                () -> assertEquals(fetchedPlayground.getAssignedPedagogue().iterator().next().getUsername(), user.getUsername()),
                () -> assertEquals(fetchedEvent.getAssignedUsers().iterator().next().getUsername(), user.getUsername())
        );

        // delete user and check that references are removed
        controller.deleteUser(user.getUsername());
        PlaygroundDTO updatedPlayground = controller.getPlayground(playground.getName());
        EventDTO updatedEvent = controller.getEvent(eventID);

        Assertions.assertAll(
                () -> assertFalse(updatedPlayground.getAssignedPedagogue().iterator().hasNext()),
                () -> assertFalse(updatedEvent.getAssignedUsers().iterator().hasNext())
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void addPedagogueToPlayground() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        UserDTO user = new UserDTO.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        controller.createPlayground(playground);
        controller.createUser(user);

        PlaygroundDTO fetchedPlayground = controller.getPlayground(playground.getName());
        assertFalse(fetchedPlayground.getAssignedPedagogue().iterator().hasNext());

        // add references
        controller.addPedagogueToPlayground(playground.getName(), user.getUsername());

        // check reference is correct
        PlaygroundDTO updatedPlayground = controller.getPlayground(playground.getName());
        Assertions.assertAll(
                () -> assertTrue(updatedPlayground.getAssignedPedagogue().iterator().hasNext()),
                () -> assertEquals(user, updatedPlayground.getAssignedPedagogue().iterator().next())
        );

        controller.deleteUser(user.getUsername());
        controller.deletePlayground(playground.getName());
    }

    @Test
    void addUserToPlaygroundEvent() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        UserDTO user = new UserDTO.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        EventDTO playgroundEvent = new EventDTO.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        WriteResult ws = controller.createPlaygroundEvent(playground.getName(), playgroundEvent);
        controller.createUser(user);

        // check that user is not in event
        EventDTO fetchedEvent = controller.getEvent(ws.getUpsertedId().toString());
        assertFalse(fetchedEvent.getAssignedUsers().iterator().hasNext());

        controller.addUserToEvent(ws.getUpsertedId().toString(), user.getUsername());

        // check that user is in event
        EventDTO updatedEvent = controller.getEvent(ws.getUpsertedId().toString());
        Assertions.assertAll(
                () -> assertTrue(updatedEvent.getAssignedUsers().iterator().hasNext()),
                () -> assertEquals(user, updatedEvent.getAssignedUsers().iterator().next())
        );

        controller.deleteUser(user.getUsername());
        controller.deletePlayground(playground.getName());
    }

    @Test
    void addPlaygroundEvent() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        EventDTO playgroundEvent = new EventDTO.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        controller.createPlaygroundEvent(playground.getName(), playgroundEvent);


        // check that playground and event is present in db, and the associations between them
        PlaygroundDTO fetchedPlayground = controller.getPlayground(playground.getName());
        EventDTO fetchedEvent = fetchedPlayground.getEvents().iterator().next();
        Assertions.assertAll(
                () -> assertNotNull(fetchedPlayground),
                () -> assertNotNull(fetchedEvent),
                () -> assertEquals(fetchedPlayground.getName(), fetchedEvent.getPlaygroundName()),
                () -> assertEquals(fetchedEvent, playgroundEvent)
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void addPlaygroundMessage() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        MessageDTO playgroundMessage = new MessageDTO.Builder()
                .setCategory("Networking")
                .setIcon("asdasdads.jpg")
                .setOutDated(false)
                .setMessageString("I would like the parents to show up....")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        controller.createPlayground(playground);
        controller.createPlaygroundMessage(playground.getName(), playgroundMessage);

        // check that playground and message is present in db, and the associations between them
        PlaygroundDTO fetchedPlayground = controller.getPlayground(playground.getName());
        MessageDTO fetchedMessage = fetchedPlayground.getMessages().iterator().next();
        Assertions.assertAll(
                () -> assertNotNull(fetchedPlayground),
                () -> assertNotNull(fetchedMessage),
                () -> assertEquals(fetchedPlayground.getName(), fetchedMessage.getPlaygroundName()),
                () -> assertEquals(fetchedMessage, playgroundMessage)
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void removePedagogueFromPlayground() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        UserDTO user = new UserDTO.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        controller.createPlayground(playground);
        controller.createUser(user);

        // add references
        controller.addPedagogueToPlayground(playground.getName(), user.getUsername());

        // check reference is correct
        PlaygroundDTO fetchedPlayground = controller.getPlayground(playground.getName());
        Assertions.assertAll(
                () -> assertTrue(fetchedPlayground.getAssignedPedagogue().iterator().hasNext()),
                () -> assertEquals(user, fetchedPlayground.getAssignedPedagogue().iterator().next())
        );

        // delete pedagogue from playground
        controller.removePedagogueFromPlayground(playground.getName(), user.getUsername());

        // check references are removed
        PlaygroundDTO updatedPlayground = controller.getPlayground(playground.getName());
        assertFalse(updatedPlayground.getAssignedPedagogue().iterator().hasNext());

        controller.deleteUser(user.getUsername());
        controller.deletePlayground(playground.getName());
    }

    @Test
    void removeUserFromPlaygroundEvent() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        UserDTO user = new UserDTO.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        EventDTO playgroundEvent = new EventDTO.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        WriteResult ws = controller.createPlaygroundEvent(playground.getName(), playgroundEvent);
        controller.createUser(user);

        // add user to event
        controller.addUserToEvent(ws.getUpsertedId().toString(), user.getUsername());

        // check that user is in event
        EventDTO fetchedEvent = controller.getEvent(ws.getUpsertedId().toString());
        Assertions.assertAll(
                () -> assertTrue(fetchedEvent.getAssignedUsers().iterator().hasNext()),
                () -> assertEquals(user, fetchedEvent.getAssignedUsers().iterator().next())
        );

        // remove user from event
        controller.removeUserFromEvent(ws.getUpsertedId().toString(), user.getUsername());

        // check that user is removed from event
        EventDTO updatedEvent = controller.getEvent(ws.getUpsertedId().toString());
        assertFalse(updatedEvent.getAssignedUsers().iterator().hasNext());

        controller.deleteUser(user.getUsername());
        controller.deletePlayground(playground.getName());
    }

    @Test
    void removePlaygroundEvent() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        EventDTO playgroundEvent = new EventDTO.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new DetailsDTO(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        controller.createPlaygroundEvent(playground.getName(), playgroundEvent);

        // check that playground and event is present in db, and the associations between them
        PlaygroundDTO fetchedPlayground = controller.getPlayground(playground.getName());
        EventDTO fetchedEvent = fetchedPlayground.getEvents().iterator().next();
        Assertions.assertAll(
                () -> assertNotNull(fetchedPlayground),
                () -> assertNotNull(fetchedEvent),
                () -> assertEquals(fetchedPlayground.getName(), fetchedEvent.getPlaygroundName()),
                () -> assertEquals(fetchedEvent, playgroundEvent)
        );

        // remove event from playground
        controller.deletePlaygroundEvent(fetchedEvent.getID());

        // check that all event references are removed
        PlaygroundDTO updatedPlayground = controller.getPlayground(playground.getName());
        assertFalse(updatedPlayground.getEvents().iterator().hasNext());

        controller.deletePlayground(playground.getName());
    }

    @Test
    void removePlaygroundMessage() throws NoModificationException {
        PlaygroundDTO playground = new PlaygroundDTO.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        MessageDTO playgroundMessage = new MessageDTO.Builder()
                .setCategory("Networking")
                .setIcon("asdasdads.jpg")
                .setOutDated(false)
                .setMessageString("I would like the parents to show up....")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        controller.createPlayground(playground);
        controller.createPlaygroundMessage(playground.getName(), playgroundMessage);

        // check that playground and message is present in db, and the associations between them
        PlaygroundDTO fetchedPlayground = controller.getPlayground(playground.getName());
        MessageDTO fetchedMessage = fetchedPlayground.getMessages().iterator().next();
        Assertions.assertAll(
                () -> assertNotNull(fetchedPlayground),
                () -> assertNotNull(fetchedMessage),
                () -> assertEquals(fetchedPlayground.getName(), fetchedMessage.getPlaygroundName()),
                () -> assertEquals(fetchedMessage, playgroundMessage)
        );

        // remove playground message
        controller.deletePlaygroundMessage(fetchedMessage.getID());

        // check message references are removed
        PlaygroundDTO updatedPlayground = controller.getPlayground(playground.getName());
        assertFalse(updatedPlayground.getMessages().iterator().hasNext());

        controller.deletePlayground(playground.getName());
    }

}