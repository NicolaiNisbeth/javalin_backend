package database.integration;

import com.mongodb.WriteResult;
import database.DALException;
import database.DataSource;
import database.NoModificationException;
import database.collections.*;
import database.dao.*;
import org.junit.jupiter.api.*;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    static IController controller = Controller.getInstance(DataSource.getTestDB());
    static IEventDAO eventDAO = new EventDAO(DataSource.getTestDB());
    static IMessageDAO messageDAO = new MessageDAO(DataSource.getTestDB());
    static IPlaygroundDAO playgroundDAO = new PlaygroundDAO(DataSource.getTestDB());
    static IUserDAO userDAO = new UserDAO(DataSource.getTestDB());

    @BeforeAll
    static void killAll(){
        playgroundDAO.deleteAllPlaygrounds();
        userDAO.deleteAllUsers();
        messageDAO.deleteAllMessages();
        eventDAO.deleteAllEvents();
    }

    @Test
    @DisplayName("Create and delete playground")
    void createdPlaygroundShouldBeFetchedPlayground() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
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
        Playground fetchedPlayground = controller.getPlayground(playground.getName());

        // check that playground is present in db
        Assertions.assertAll(
                () -> Assertions.assertNotNull(fetchedPlayground),
                () -> assertEquals(playground, fetchedPlayground)
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    @DisplayName("Create and delete user")
    void createdUserShouldBeFetchedUser() throws DALException, NoModificationException {
        User user = new User.Builder("s175565")
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
        User fetchedUser = controller.getUser(user.getUsername());

        // check that playground is present in db
        Assertions.assertAll(
                () -> Assertions.assertNotNull(user),
                () -> assertEquals(user, fetchedUser)
        );

        controller.deleteUser(user.getUsername());
    }

    @Test
    void addedAssociationsShouldBeFetchedAssociations() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        User user = new User.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        Event playgroundEvent = new Event.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        Message playgroundMessage = new Message.Builder()
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
        controller.addPlaygroundMessage(playground.getName(), playgroundMessage);

        // get playground
        Playground fetchedPlayground = controller.getPlayground(playground.getName());

        // check references
        Assertions.assertAll(
                () -> assertEquals(user, fetchedPlayground.getAssignedPedagogue().iterator().next()),
                () -> assertEquals(playgroundEvent, fetchedPlayground.getEvents().iterator().next()),
                () -> assertEquals(playgroundMessage, fetchedPlayground.getMessages().iterator().next())
        );

        List<Playground> list = controller.getPlaygrounds();

        controller.deleteUser(user.getUsername());
        controller.deletePlayground(playground.getName());
    }

    @Test
    void addedEventToUserShouldBeInUserEventList() throws DALException, NoModificationException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        User user = new User.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        Event event1 = new Event.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        Event event2 = new Event.Builder()
                .name("Fangeleg")
                .description("Fangeleg rundt om Snorrstrup sø")
                .participants(10)
                .imagePath("asdasdsa9d8dsa.jpg")
                .details(new Details(new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        WriteResult e1 = controller.createPlaygroundEvent(playground.getName(), event1);
        WriteResult e2 = controller.createPlaygroundEvent(playground.getName(), event2);

        WriteResult u1 = controller.createUser(user);
        controller.addUserToEvent(e1.getUpsertedId().toString(), user.getUsername());
        controller.addUserToEvent(e2.getUpsertedId().toString(), user.getUsername());

        // get user
        User fetchedUser = controller.getUser(user.getUsername());
        Event fetchedEvent1 = controller.getEvent(e1.getUpsertedId().toString());
        Event fetchedEvent2 = controller.getEvent(e2.getUpsertedId().toString());

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
    void getEvent() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        User user = new User.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        Event event = new Event.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        WriteResult ws = controller.createPlaygroundEvent(playground.getName(), event);

        controller.createUser(user);
        controller.addUserToEvent(ws.getUpsertedId().toString(), user.getUsername());

        // get event
        Event fetchedEvent = controller.getEvent(ws.getUpsertedId().toString());

        // check references
        Assertions.assertAll(
                () -> assertEquals(playground.getName(), fetchedEvent.getPlaygroundName()),
                () -> assertEquals(user , fetchedEvent.getAssignedUsers().iterator().next())
        );

        controller.deleteUser(user.getUsername());
        controller.deletePlayground(playground.getName());
    }

    @Test
    void createdPlaygroundMessageShouldHavePlaygroundID() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        Message playgroundMessage = new Message.Builder()
                .setCategory("Networking")
                .setIcon("asdasdads.jpg")
                .setOutDated(false)
                .setMessageString("I would like the parents to show up....")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        controller.createPlayground(playground);
        controller.addPlaygroundMessage(playground.getName(), playgroundMessage);

        // get message
        Playground fetchedPlayground = controller.getPlayground(playground.getName());
        Message message = fetchedPlayground.getMessages().iterator().next();

        // check references
        Assertions.assertAll(
                () -> assertEquals(playground.getName(), message.getPlaygroundName())
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void addedEventShouldBeInPlayground() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        Event playgroundEvent1 = new Event.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        Event playgroundEvent2 = new Event.Builder()
                .name("Fangeleg")
                .description("Fangeleg rundt om Snorrstrup sø")
                .participants(10)
                .imagePath("asdasdsa9d8dsa.jpg")
                .details(new Details(new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis()),new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        controller.createPlaygroundEvent(playground.getName(), playgroundEvent1);
        controller.createPlaygroundEvent(playground.getName(), playgroundEvent2);

        List<Event> eventList = controller.getEventsInPlayground(playground.getName());

        Assertions.assertAll(
                () -> assertEquals(playgroundEvent1, eventList.get(0)),
                () -> assertEquals(playgroundEvent2, eventList.get(1))
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void addedMessagesShouldBeInPlayground() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        Message playgroundMessage1 = new Message.Builder()
                .setCategory("Networking")
                .setIcon("asdasdads.jpg")
                .setOutDated(false)
                .setMessageString("I would like the parents to show up....")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        Message playgroundMessage2 = new Message.Builder()
                .setCategory("Corona")
                .setIcon("asdasd2323ads.jpg")
                .setOutDated(false)
                .setMessageString("Corono is le good shit")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        controller.createPlayground(playground);
        controller.addPlaygroundMessage(playground.getName(), playgroundMessage1);
        controller.addPlaygroundMessage(playground.getName(), playgroundMessage2);

        List<Message> messageList = controller.getMessagesInPlayground(playground.getName());
        Assertions.assertAll(
                () -> assertEquals(playgroundMessage1, messageList.get(0)),
                () -> assertEquals(playgroundMessage2, messageList.get(1))
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void updatePlaygroundEvent() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        Event playgroundEvent = new Event.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        controller.createPlaygroundEvent(playground.getName(), playgroundEvent);

        // check that playground and event is present in db, and the associations between them
        Playground fetchedPlayground = controller.getPlayground(playground.getName());
        Event fetchedEvent = fetchedPlayground.getEvents().iterator().next();
        Assertions.assertAll(
                () -> assertEquals(fetchedPlayground.getName(), fetchedEvent.getPlaygroundName()),
                () -> assertEquals(fetchedEvent, playgroundEvent)
        );

        // update values
        fetchedEvent.setDescription("Fodbold og snobrød i Søndermarken");
        fetchedEvent.setParticipants(40);
        fetchedEvent.setImagepath("as9d89a8sd98sa98dsa.jpg");
        controller.updatePlaygroundEvent(fetchedEvent);

        // check that event has updated values
        Event updatedEvent = controller.getEvent(fetchedEvent.getId());
        Assertions.assertAll(
                () -> assertEquals(fetchedEvent.getDescription(), updatedEvent.getDescription()),
                () -> assertEquals(fetchedEvent.getParticipants(), updatedEvent.getParticipants()),
                () -> assertEquals(fetchedEvent.getImagepath(), updatedEvent.getImagepath())
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void updatePlaygroundMessage() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        Message playgroundMessage = new Message.Builder()
                .setCategory("Networking")
                .setIcon("asdasdads.jpg")
                .setOutDated(false)
                .setMessageString("I would like the parents to show up....")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        controller.createPlayground(playground);
        controller.addPlaygroundMessage(playground.getName(), playgroundMessage);

        // check that playground and event is present in db, and the associations between them
        Playground fetchedPlayground = controller.getPlayground(playground.getName());
        Message fetchedMessage = fetchedPlayground.getMessages().iterator().next();
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
        Message updatedMessage = controller.getMessage(fetchedMessage.getId());
        Assertions.assertAll(
                () -> assertEquals(fetchedMessage.getCategory(), updatedMessage.getCategory()),
                () -> assertEquals(fetchedMessage.getIcon(), updatedMessage.getIcon()),
                () -> assertEquals(fetchedMessage.getMessageString(), updatedMessage.getMessageString())
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void deletePlaygroundShouldRemoveReferenceInUser() throws DALException, NoModificationException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        User user = new User.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        Event playgroundEvent = new Event.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        Message playgroundMessage = new Message.Builder()
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
        controller.addPlaygroundMessage(playground.getName(), playgroundMessage);

        // check all the references
        Playground fetchedPlayground = controller.getPlayground(playground.getName());
        User playPovPedagogue = fetchedPlayground.getAssignedPedagogue().iterator().next();
        Event playPovEvent = fetchedPlayground.getEvents().iterator().next();
        Message playPovMessage = fetchedPlayground.getMessages().iterator().next();

        User fetchedPedagogue = controller.getUser(playPovPedagogue.getUsername());
        Event fetchedEvent = controller.getEvent(playPovEvent.getId());
        Message fetchedMessage = controller.getMessage(playPovMessage.getId());

        Assertions.assertAll(
                () -> assertEquals(playPovPedagogue.getUsername(), fetchedPedagogue.getUsername()),
                () -> assertEquals(playPovEvent.getId(), fetchedEvent.getId()),
                () -> assertEquals(playPovMessage.getId(), fetchedMessage.getId())
        );

        // delete playground
        controller.deletePlayground(playground.getName());

        // check that the references are gone
        User updatedUser = controller.getUser(playPovPedagogue.getUsername());
        Assertions.assertAll(() -> assertFalse(updatedUser.getPlaygroundsIDs().iterator().hasNext()));

        controller.deleteUser(updatedUser.getUsername());
    }

    @Test
    void deleteUserShouldRemoveReferenceInPlayground() throws DALException, NoModificationException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        User user = new User.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        Event playgroundEvent = new Event.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        controller.createUser(user);

        // add references
        controller.addPedagogueToPlayground(playground.getName(), user.getUsername());

        WriteResult eventResult = controller.createPlaygroundEvent(playground.getName(), playgroundEvent);
        String eventID = eventResult.getUpsertedId().toString();
        controller.addUserToEvent(eventID, user.getUsername());


        // confirm the different references
        User fetchedUser = controller.getUser(user.getUsername());
        Playground fetchedPlayground = controller.getPlayground(playground.getName());
        Event fetchedEvent = controller.getEvent(eventID);

        Assertions.assertAll(
                () -> assertEquals(eventID, fetchedUser.getEvents().iterator().next().getId()),
                () -> assertEquals(playground.getName(), fetchedUser.getPlaygroundsIDs().iterator().next()),
                () -> assertEquals(fetchedPlayground.getAssignedPedagogue().iterator().next().getUsername(), user.getUsername()),
                () -> assertEquals(fetchedEvent.getAssignedUsers().iterator().next().getUsername(), user.getUsername())
        );

        // delete user and check that references are removed
        controller.deleteUser(user.getUsername());
        Playground updatedPlayground = controller.getPlayground(playground.getName());
        Event updatedEvent = controller.getEvent(eventID);

        Assertions.assertAll(
                () -> assertFalse(updatedPlayground.getAssignedPedagogue().iterator().hasNext()),
                () -> assertFalse(updatedEvent.getAssignedUsers().iterator().hasNext())
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void addPedagogueToPlayground() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        User user = new User.Builder("s175565")
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

        Playground fetchedPlayground = controller.getPlayground(playground.getName());
        assertFalse(fetchedPlayground.getAssignedPedagogue().iterator().hasNext());

        // add references
        controller.addPedagogueToPlayground(playground.getName(), user.getUsername());

        // check reference is correct
        Playground updatedPlayground = controller.getPlayground(playground.getName());
        Assertions.assertAll(
                () -> assertTrue(updatedPlayground.getAssignedPedagogue().iterator().hasNext()),
                () -> assertEquals(user, updatedPlayground.getAssignedPedagogue().iterator().next())
        );

        controller.deleteUser(user.getUsername());
        controller.deletePlayground(playground.getName());
    }

    @Test
    void addUserToPlaygroundEvent() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        User user = new User.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        Event playgroundEvent = new Event.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        WriteResult ws = controller.createPlaygroundEvent(playground.getName(), playgroundEvent);
        controller.createUser(user);

        // check that user is not in event
        Event fetchedEvent = controller.getEvent(ws.getUpsertedId().toString());
        assertFalse(fetchedEvent.getAssignedUsers().iterator().hasNext());

        controller.addUserToEvent(ws.getUpsertedId().toString(), user.getUsername());

        // check that user is in event
        Event updatedEvent = controller.getEvent(ws.getUpsertedId().toString());
        Assertions.assertAll(
                () -> assertTrue(updatedEvent.getAssignedUsers().iterator().hasNext()),
                () -> assertEquals(user, updatedEvent.getAssignedUsers().iterator().next())
        );

        controller.deleteUser(user.getUsername());
        controller.deletePlayground(playground.getName());
    }

    @Test
    void addPlaygroundEvent() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        Event playgroundEvent = new Event.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        controller.createPlaygroundEvent(playground.getName(), playgroundEvent);


        // check that playground and event is present in db, and the associations between them
        Playground fetchedPlayground = controller.getPlayground(playground.getName());
        Event fetchedEvent = fetchedPlayground.getEvents().iterator().next();
        Assertions.assertAll(
                () -> assertNotNull(fetchedPlayground),
                () -> assertNotNull(fetchedEvent),
                () -> assertEquals(fetchedPlayground.getName(), fetchedEvent.getPlaygroundName()),
                () -> assertEquals(fetchedEvent, playgroundEvent)
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void addPlaygroundMessage() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        Message playgroundMessage = new Message.Builder()
                .setCategory("Networking")
                .setIcon("asdasdads.jpg")
                .setOutDated(false)
                .setMessageString("I would like the parents to show up....")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        controller.createPlayground(playground);
        controller.addPlaygroundMessage(playground.getName(), playgroundMessage);

        // check that playground and message is present in db, and the associations between them
        Playground fetchedPlayground = controller.getPlayground(playground.getName());
        Message fetchedMessage = fetchedPlayground.getMessages().iterator().next();
        Assertions.assertAll(
                () -> assertNotNull(fetchedPlayground),
                () -> assertNotNull(fetchedMessage),
                () -> assertEquals(fetchedPlayground.getName(), fetchedMessage.getPlaygroundName()),
                () -> assertEquals(fetchedMessage, playgroundMessage)
        );

        controller.deletePlayground(playground.getName());
    }

    @Test
    void removePedagogueFromPlayground() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        User user = new User.Builder("s175565")
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
        Playground fetchedPlayground = controller.getPlayground(playground.getName());
        Assertions.assertAll(
                () -> assertTrue(fetchedPlayground.getAssignedPedagogue().iterator().hasNext()),
                () -> assertEquals(user, fetchedPlayground.getAssignedPedagogue().iterator().next())
        );

        // delete pedagogue from playground
        controller.removePedagoguePlaygroundAssociation(playground.getName(), user.getUsername());

        // check references are removed
        Playground updatedPlayground = controller.getPlayground(playground.getName());
        assertFalse(updatedPlayground.getAssignedPedagogue().iterator().hasNext());

        controller.deleteUser(user.getUsername());
        controller.deletePlayground(playground.getName());
    }

    @Test
    void removeUserFromPlaygroundEvent() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        User user = new User.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        Event playgroundEvent = new Event.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        WriteResult ws = controller.createPlaygroundEvent(playground.getName(), playgroundEvent);
        controller.createUser(user);

        // add user to event
        controller.addUserToEvent(ws.getUpsertedId().toString(), user.getUsername());

        // check that user is in event
        Event fetchedEvent = controller.getEvent(ws.getUpsertedId().toString());
        Assertions.assertAll(
                () -> assertTrue(fetchedEvent.getAssignedUsers().iterator().hasNext()),
                () -> assertEquals(user, fetchedEvent.getAssignedUsers().iterator().next())
        );

        // remove user from event
        controller.removeUserEventAssociation(ws.getUpsertedId().toString(), user.getUsername());

        // check that user is removed from event
        Event updatedEvent = controller.getEvent(ws.getUpsertedId().toString());
        assertFalse(updatedEvent.getAssignedUsers().iterator().hasNext());

        controller.deleteUser(user.getUsername());
        controller.deletePlayground(playground.getName());
    }

    @Test
    void removePlaygroundEvent() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        Event playgroundEvent = new Event.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        controller.createPlayground(playground);
        controller.createPlaygroundEvent(playground.getName(), playgroundEvent);

        // check that playground and event is present in db, and the associations between them
        Playground fetchedPlayground = controller.getPlayground(playground.getName());
        Event fetchedEvent = fetchedPlayground.getEvents().iterator().next();
        Assertions.assertAll(
                () -> assertNotNull(fetchedPlayground),
                () -> assertNotNull(fetchedEvent),
                () -> assertEquals(fetchedPlayground.getName(), fetchedEvent.getPlaygroundName()),
                () -> assertEquals(fetchedEvent, playgroundEvent)
        );

        // remove event from playground
        controller.removePlaygroundEvent(fetchedEvent.getId());

        // check that all event references are removed
        Playground updatedPlayground = controller.getPlayground(playground.getName());
        assertFalse(updatedPlayground.getEvents().iterator().hasNext());

        controller.deletePlayground(playground.getName());
    }

    @Test
    void removePlaygroundMessage() throws NoModificationException, DALException {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        Message playgroundMessage = new Message.Builder()
                .setCategory("Networking")
                .setIcon("asdasdads.jpg")
                .setOutDated(false)
                .setMessageString("I would like the parents to show up....")
                .setDate(new Date(System.currentTimeMillis()))
                .build();

        controller.createPlayground(playground);
        controller.addPlaygroundMessage(playground.getName(), playgroundMessage);

        // check that playground and message is present in db, and the associations between them
        Playground fetchedPlayground = controller.getPlayground(playground.getName());
        Message fetchedMessage = fetchedPlayground.getMessages().iterator().next();
        Assertions.assertAll(
                () -> assertNotNull(fetchedPlayground),
                () -> assertNotNull(fetchedMessage),
                () -> assertEquals(fetchedPlayground.getName(), fetchedMessage.getPlaygroundName()),
                () -> assertEquals(fetchedMessage, playgroundMessage)
        );

        // remove playground message
        controller.removeMessagePlaygroundAssociation(fetchedMessage.getId());

        // check message references are removed
        Playground updatedPlayground = controller.getPlayground(playground.getName());
        assertFalse(updatedPlayground.getMessages().iterator().hasNext());

        controller.deletePlayground(playground.getName());
    }
}