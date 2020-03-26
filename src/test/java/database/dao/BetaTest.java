package database.dao;

import com.mongodb.WriteResult;
import database.collections.*;
import org.junit.jupiter.api.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class BetaTest {

    static Beta beta;
    static IEventDAO eventDAO;
    static IMessageDAO messageDAO;
    static IPlaygroundDAO playgroundDAO;
    static IUserDAO userDAO;

    final static User ADMIN_USER = new User.Builder("admin").status("admin").build();;

    @BeforeAll
    static void setup(){
        beta = Beta.getInstance();
    }

    @BeforeEach
    void beforeEach(){
        eventDAO = new EventDAO();
        messageDAO = new MessageDAO();
        playgroundDAO = new PlaygroundDAO();
        userDAO = new UserDAO();
    }

    @AfterEach
    void afterEach(){
        eventDAO = null;
        messageDAO = null;
        playgroundDAO = null;
        userDAO = null;
    }

    @AfterAll
    static void tearDown(){
        beta = null;
    }

    @Test
    @DisplayName("Create and delete playground")
    void createPlayground() {
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
        Assertions.assertNull(beta.getPlayground(playground.getName()));

        beta.createPlayground(playground);
        Playground fetchedPlayground = beta.getPlayground(playground.getName());

        // check that playground is present in db
        Assertions.assertAll(
                () -> Assertions.assertNotNull(fetchedPlayground),
                () -> assertEquals(playground, fetchedPlayground)
        );

        beta.deletePlayground(playground.getName());
    }

    @Test
    @DisplayName("Create and delete user")
    void createUser() {
        User user = new User.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .email("s175565@student.dtu.dk")
                .password("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        // check that user is NOT present in db
        Assertions.assertNull(beta.getUser(user.getUsername()));

        beta.createUser(user);

        User fetchedUser = beta.getUser(user.getUsername());

        // check that playground is present in db
        Assertions.assertAll(
                () -> Assertions.assertNotNull(user),
                () -> assertEquals(user, fetchedUser)
        );

        beta.deleteUser(user.getUsername());
    }

    @Test
    void getPlayground() {
    }

    @Test
    void getUser() {
    }

    @Test
    void getEvent() {
    }

    @Test
    void getMessage() {
    }

    @Test
    void getPlaygrounds() {
    }

    @Test
    void getUsers() {
    }

    @Test
    void getPlaygroundEvents() {
    }

    @Test
    void getPlaygroundMessages() {
    }

    @Test
    void updatePlayground() {
        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        beta.createPlayground(playground);
        Playground fetchedPlayground = beta.getPlayground(playground.getName());

        // check that playground is present in db
        Assertions.assertAll(
                () -> Assertions.assertNotNull(fetchedPlayground),
                () -> assertEquals(playground, fetchedPlayground)
        );

        // update values
        fetchedPlayground.setStreetName("Sohoj");
        fetchedPlayground.setStreetNumber(12);
        fetchedPlayground.setZipCode(1223);
        fetchedPlayground.setCommune("Ballerup");
        beta.updatePlayground(fetchedPlayground);
        Playground updatedPlayground = beta.getPlayground(fetchedPlayground.getName());

        // check that playground has updated values
        Assertions.assertAll(
                () -> assertEquals("Sohoj", updatedPlayground.getStreetName()),
                () -> assertEquals(12, updatedPlayground.getStreetNumber()),
                () -> assertEquals(1223, updatedPlayground.getZipCode()),
                () -> assertEquals("Ballerup", updatedPlayground.getCommune())
        );

        beta.deletePlayground(updatedPlayground.getName());
    }

    @Test
    void updateUser() {
        User user = new User.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .email("s175565@student.dtu.dk")
                .password("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        beta.createUser(user);
        User fetchedUser = beta.getUser(user.getUsername());

        // check that playground is present in db
        Assertions.assertAll(
                () -> Assertions.assertNotNull(user),
                () -> assertEquals(user, fetchedUser)
        );

        // update values
        fetchedUser.setEmail("nicolai.nisbeth@hotmail.com");
        fetchedUser.setPassword("kodenwhn");
        fetchedUser.setStatus("pedagogue");
        beta.updateUser(fetchedUser);

        // check that user has updated values
        User updatedUser = beta.getUser(fetchedUser.getUsername());
        Assertions.assertAll(
                () -> assertEquals(fetchedUser.getEmail(), updatedUser.getEmail()),
                () -> assertEquals(fetchedUser.getPassword(), updatedUser.getPassword()),
                () -> assertEquals(fetchedUser.getStatus(), updatedUser.getStatus())
        );

        beta.deleteUser(user.getUsername());
    }

    @Test
    void updatePlaygroundEvent() {
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

        beta.createPlayground(playground);
        beta.addPlaygroundEvent(playground.getName(), playgroundEvent);

        // check that playground and event is present in db, and the associations between them
        Playground fetchedPlayground = beta.getPlayground(playground.getName());
        Event fetchedEvent = fetchedPlayground.getEvents().iterator().next();
        Assertions.assertAll(
                () -> assertEquals(fetchedPlayground.getName(), fetchedEvent.getPlayground()),
                () -> assertEquals(fetchedEvent, playgroundEvent)
        );

        // update values
        fetchedEvent.setDescription("Fodbold og snobrød i Søndermarken");
        fetchedEvent.setParticipants(40);
        fetchedEvent.setImagepath("as9d89a8sd98sa98dsa.jpg");
        beta.updatePlaygroundEvent(fetchedEvent);

        // check that event has updated values
        Event updatedEvent = beta.getEvent(fetchedEvent.getId());
        Assertions.assertAll(
                () -> assertEquals(fetchedEvent.getDescription(), updatedEvent.getDescription()),
                () -> assertEquals(fetchedEvent.getParticipants(), updatedEvent.getParticipants()),
                () -> assertEquals(fetchedEvent.getImagepath(), updatedEvent.getImagepath())
        );

        beta.deletePlayground(playground.getName());
    }

    @Test
    void updatePlaygroundMessage() {
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

        beta.createPlayground(playground);
        beta.addPlaygroundMessage(playground.getName(), playgroundMessage);

        // check that playground and event is present in db, and the associations between them
        Playground fetchedPlayground = beta.getPlayground(playground.getName());
        Message fetchedMessage = fetchedPlayground.getMessages().iterator().next();
        Assertions.assertAll(
                () -> assertEquals(fetchedPlayground.getName(), fetchedMessage.getPlaygroundName()),
                () -> assertEquals(fetchedMessage, playgroundMessage)
        );

        // update values
        fetchedMessage.setCategory("Alternativ leg");
        fetchedMessage.setIcon("as9dsa98dsa98d98sa.jpg");
        fetchedMessage.setMessageString("Ny forskning viser at børn under ....");
        beta.updatePlaygroundMessage(fetchedMessage);

        // check that event has updated values
        Message updatedMessage = beta.getMessage(fetchedMessage.getId());
        Assertions.assertAll(
                () -> assertEquals(fetchedMessage.getCategory(), updatedMessage.getCategory()),
                () -> assertEquals(fetchedMessage.getIcon(), updatedMessage.getIcon()),
                () -> assertEquals(fetchedMessage.getMessageString(), updatedMessage.getMessageString())
        );

        beta.deletePlayground(playground.getName());
    }

    @Test
    void deletePlayground() {
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
                .email("s175565@student.dtu.dk")
                .password("nicolai123456789")
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
        beta.createPlayground(playground);
        beta.createUser(user);
        beta.addPedagogueToPlayground(playground.getName(), user.getUsername());
        beta.addPlaygroundEvent(playground.getName(), playgroundEvent);
        beta.addPlaygroundMessage(playground.getName(), playgroundMessage);

        // check all the references
        Playground fetchedPlayground = beta.getPlayground(playground.getName());
        User playPovPedagogue = fetchedPlayground.getAssignedPedagogue().iterator().next();
        Event playPovEvent = fetchedPlayground.getEvents().iterator().next();
        Message playPovMessage = fetchedPlayground.getMessages().iterator().next();

        User fetchedPedagogue = beta.getUser(playPovPedagogue.getUsername());
        Event fetchedEvent = beta.getEvent(playPovEvent.getId());
        Message fetchedMessage = beta.getMessage(playPovMessage.getId());

        Assertions.assertAll(
                () -> assertEquals(playPovPedagogue.getUsername(), fetchedPedagogue.getUsername()),
                () -> assertEquals(playPovEvent.getId(), fetchedEvent.getId()),
                () -> assertEquals(playPovMessage.getId(), fetchedMessage.getId())
        );

        // delete playground
        beta.deletePlayground(playground.getName());

        // check that the references are gone
        User updatedUser = beta.getUser(playPovPedagogue.getUsername());

        Assertions.assertAll(
                () -> assertFalse(updatedUser.getPlaygroundNames().iterator().hasNext())
        );

        beta.deleteUser(updatedUser.getUsername());
    }

    @Test
    void deleteUser() {
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
                .email("s175565@student.dtu.dk")
                .password("nicolai123456789")
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

        beta.createPlayground(playground);
        beta.createUser(user);

        // add references
        beta.addPedagogueToPlayground(playground.getName(), user.getUsername());

        WriteResult eventResult = beta.addPlaygroundEvent(playground.getName(), playgroundEvent);
        String eventID = eventResult.getUpsertedId().toString();
        beta.addUserToPlaygroundEvent(eventID, user.getUsername());


        // confirm the different references
        User fetchedUser = beta.getUser(user.getUsername());
        Playground fetchedPlayground = beta.getPlayground(playground.getName());
        Event fetchedEvent = beta.getEvent(eventID);

        Assertions.assertAll(
                () -> assertEquals(eventID, fetchedUser.getEvents().iterator().next().getId()),
                () -> assertEquals(playground.getName(), fetchedUser.getPlaygroundNames().iterator().next()),
                () ->
        );




        // check that user reference is removed from playground and event
    }

    @Test
    void addPedagogueToPlayground() {
    }

    @Test
    void addUserToPlaygroundEvent() {
    }

    @Test
    void addPlaygroundEvent() {

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


        // check that playground and event is not already present in db
        Assertions.assertAll(
                () -> assertNull(beta.getPlayground(playground.getName())),
                () -> assertNull(beta.getEvent(playgroundEvent.getId()))
        );

        beta.createPlayground(playground);
        beta.addPlaygroundEvent(playground.getName(), playgroundEvent);


        // check that playground and event is present in db, and the associations between them
        Playground fetchedPlayground = beta.getPlayground(playground.getName());
        Event fetchedEvent = fetchedPlayground.getEvents().iterator().next();
        Assertions.assertAll(
                () -> assertNotNull(fetchedPlayground),
                () -> assertNotNull(fetchedEvent),
                () -> assertEquals(fetchedPlayground.getName(), fetchedEvent.getPlayground()),
                () -> assertEquals(fetchedEvent, playgroundEvent)
        );

        beta.deletePlayground(playground.getName());
    }

    @Test
    void addPlaygroundMessage() {

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


        // check that playground and message is not already present in db
        Assertions.assertAll(
                () -> assertNull(beta.getPlayground(playground.getName())),
                () -> assertNull(beta.getMessage(playgroundMessage.getId()))
        );

        beta.createPlayground(playground);
        beta.addPlaygroundMessage(playground.getName(), playgroundMessage);

        // check that playground and message is present in db, and the associations between them
        Playground fetchedPlayground = beta.getPlayground(playground.getName());
        Message fetchedMessage = fetchedPlayground.getMessages().iterator().next();
        Assertions.assertAll(
                () -> assertNotNull(fetchedPlayground),
                () -> assertNotNull(fetchedMessage),
                () -> assertEquals(fetchedPlayground.getName(), fetchedMessage.getPlaygroundName()),
                () -> assertEquals(fetchedMessage, playgroundMessage)
        );

        beta.deletePlayground(playground.getName());
    }

    @Test
    void removePedagogueFromPlayground() {
    }

    @Test
    void removeUserFromPlaygroundEvent() {
    }

    @Test
    void removePlaygroundEvent() {
    }

    @Test
    void removePlaygroundMessage() {
    }
}