package database.dao;

import com.mongodb.WriteResult;
import database.DALException;
import database.collections.*;
import org.junit.jupiter.api.*;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    static Controller beta;
    static IEventDAO eventDAO;
    static IMessageDAO messageDAO;
    static IPlaygroundDAO playgroundDAO;
    static IUserDAO userDAO;

    final static User ADMIN_USER = new User.Builder("admin").status("admin").build();;

    @BeforeAll
    static void setup(){
        beta = Controller.getInstance();
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
    void createUser() throws DALException {
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

        beta.createUser(user);
        beta.createPlayground(playground);
        beta.addPlaygroundEvent(playground.getName(), playgroundEvent);
        beta.addPlaygroundMessage(playground.getName(), playgroundMessage);
        beta.addPedagogueToPlayground(playground.getName(), user.getUsername());

        // get playground
        Playground fetchedPlayground = beta.getPlayground(playground.getName());

        // check references
        Assertions.assertAll(
                () -> assertEquals(user, fetchedPlayground.getAssignedPedagogue().iterator().next()),
                () -> assertEquals(playgroundEvent, fetchedPlayground.getEvents().iterator().next()),
                () -> assertEquals(playgroundMessage, fetchedPlayground.getMessages().iterator().next())
        );

        beta.deleteUser(user.getUsername());
        beta.deletePlayground(playground.getName());
    }

    @Test
    void getUser() throws DALException {
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

        beta.createUser(user);
        beta.createPlayground(playground);
        WriteResult e1 = beta.addPlaygroundEvent(playground.getName(), playgroundEvent1);
        WriteResult e2 = beta.addPlaygroundEvent(playground.getName(), playgroundEvent2);
        beta.addUserToPlaygroundEvent(e1.getUpsertedId().toString(), user.getUsername());
        beta.addUserToPlaygroundEvent(e2.getUpsertedId().toString(), user.getUsername());

        // get user
        User fetchedUser = beta.getUser(user.getUsername());

        // check references to events
        Assertions.assertAll(
                () -> assertEquals(playgroundEvent1, fetchedUser.getEvents().iterator().next()),
                () -> assertEquals(2, fetchedUser.getEvents().size())
        );

        beta.deleteUser(user.getUsername());
        beta.deletePlayground(playground.getName());
    }

    @Test
    void getEvent() {
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

        Event playgroundEvent1 = new Event.Builder()
                .name("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

        beta.createPlayground(playground);
        beta.createUser(user);
        WriteResult ws = beta.addPlaygroundEvent(playground.getName(), playgroundEvent1);
        beta.addUserToPlaygroundEvent(ws.getUpsertedId().toString(), user.getUsername());

        // get event
        Event event = beta.getEvent(ws.getUpsertedId().toString());

        // check references
        Assertions.assertAll(
                () -> assertEquals(playground.getName(), event.getPlaygroundName()),
                () -> assertEquals(user , event.getAssignedUsers().iterator().next())
        );

        beta.deleteUser(user.getUsername());
        beta.deletePlayground(playground.getName());

    }

    @Test
    void getMessage() {
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

        Message playgroundMessage = new Message.Builder()
                .setCategory("Networking")
                .setIcon("asdasdads.jpg")
                .setOutDated(false)
                .setMessageString("I would like the parents to show up....")
                .setDate(new Date(System.currentTimeMillis()))
                .build();


        beta.createPlayground(playground);
        beta.createUser(user);
        beta.addPlaygroundMessage(playground.getName(), playgroundMessage);

        // get message
        Playground fetchedPlayground = beta.getPlayground(playground.getName());

        Message message = fetchedPlayground.getMessages().iterator().next();

        // check references
        Assertions.assertAll(
                () -> assertEquals(playground.getName(), message.getPlaygroundName())
        );

        beta.deleteUser(user.getUsername());
        beta.deletePlayground(playground.getName());
    }

    @Test
    void getPlaygrounds() {
        Playground playground1 = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        Playground playground2 = new Playground.Builder("Sønderparken")
                .setStreetName("Bistrup")
                .setStreetNumber(22)
                .setZipCode(2378)
                .setCommune("Ølstykke")
                .setToiletPossibilities(true)
                .setHasSoccerField(false)
                .setImagePath("asd97a9s8123213d89asd.jpg")
                .build();

        beta.createPlayground(playground1);
        beta.createPlayground(playground2);

        List<Playground> playgroundList = beta.getPlaygrounds();

        Assertions.assertAll(
                () -> assertEquals(playground1, playgroundList.get(0)),
                () -> assertEquals(playground2, playgroundList.get(1))
        );

        beta.deletePlayground(playground1.getName());
        beta.deletePlayground(playground2.getName());
    }

    @Test
    void getUsers() {
        User user1 = new User.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .email("s175565@student.dtu.dk")
                .password("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        User user2 = new User.Builder("s123345")
                .setFirstname("Bente")
                .setLastname("Børge")
                .status("pedagogue")
                .email("s123345@student.dtu.dk")
                .password("bentabørge123321")
                .phoneNumbers("+45 12 32 21 32")
                .imagePath("asd9as9d8a89s323d.jpg")
                .build();

        beta.createUser(user1);
        beta.createUser(user2);

        List<User> userList = beta.getUsers();

        Assertions.assertAll(
                () -> assertEquals(user1, userList.get(0)),
                () -> assertEquals(user2, userList.get(1))
        );

        beta.deleteUser(user1.getUsername());
        beta.deleteUser(user2.getUsername());
    }

    @Test
    void getPlaygroundEvents() {
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

        beta.createPlayground(playground);
        beta.addPlaygroundEvent(playground.getName(), playgroundEvent1);
        beta.addPlaygroundEvent(playground.getName(), playgroundEvent2);

        List<Event> eventList = beta.getPlaygroundEvents(playground.getName());

        Assertions.assertAll(
                () -> assertEquals(playgroundEvent1, eventList.get(0)),
                () -> assertEquals(playgroundEvent2, eventList.get(1))
        );

        beta.deletePlayground(playground.getName());
    }

    @Test
    void getPlaygroundMessages() {
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

        beta.createPlayground(playground);
        beta.addPlaygroundMessage(playground.getName(), playgroundMessage1);
        beta.addPlaygroundMessage(playground.getName(), playgroundMessage2);

        List<Message> messageList = beta.getPlaygroundMessages(playground.getName());
        Assertions.assertAll(
                () -> assertEquals(playgroundMessage1, messageList.get(0)),
                () -> assertEquals(playgroundMessage2, messageList.get(1))
        );

        beta.deletePlayground(playground.getName());
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
    void updateUser() throws DALException {
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
                () -> assertEquals(fetchedPlayground.getName(), fetchedEvent.getPlaygroundName()),
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
    void deletePlayground() throws DALException {
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
                () -> assertFalse(updatedUser.getPlaygroundsIDs().iterator().hasNext())
        );

        beta.deleteUser(updatedUser.getUsername());
    }

    @Test
    void deleteUser() throws DALException {
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
                () -> assertEquals(playground.getName(), fetchedUser.getPlaygroundsIDs().iterator().next()),
                () -> assertEquals(fetchedPlayground.getAssignedPedagogue().iterator().next().getUsername(), user.getUsername()),
                () -> assertEquals(fetchedEvent.getAssignedUsers().iterator().next().getUsername(), user.getUsername())
        );

        // delete user and check that references are removed
        beta.deleteUser(user.getUsername());
        Playground updatedPlayground = beta.getPlayground(playground.getName());
        Event updatedEvent = beta.getEvent(eventID);

        Assertions.assertAll(
                () -> assertFalse(updatedPlayground.getAssignedPedagogue().iterator().hasNext()),
                () -> assertFalse(updatedEvent.getAssignedUsers().iterator().hasNext())
        );

        beta.deletePlayground(playground.getName());
    }

    @Test
    void addPedagogueToPlayground() {
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

        beta.createPlayground(playground);
        beta.createUser(user);

        Playground fetchedPlayground = beta.getPlayground(playground.getName());
        assertFalse(fetchedPlayground.getAssignedPedagogue().iterator().hasNext());

        // add references
        beta.addPedagogueToPlayground(playground.getName(), user.getUsername());

        // check reference is correct
        Playground updatedPlayground = beta.getPlayground(playground.getName());
        Assertions.assertAll(
                () -> assertTrue(updatedPlayground.getAssignedPedagogue().iterator().hasNext()),
                () -> assertEquals(user, updatedPlayground.getAssignedPedagogue().iterator().next())
        );

        beta.deleteUser(user.getUsername());
        beta.deletePlayground(playground.getName());
    }

    @Test
    void addUserToPlaygroundEvent() {
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
        WriteResult ws = beta.addPlaygroundEvent(playground.getName(), playgroundEvent);
        beta.createUser(user);

        // check that user is not in event
        Event fetchedEvent = beta.getEvent(ws.getUpsertedId().toString());
        assertFalse(fetchedEvent.getAssignedUsers().iterator().hasNext());

        beta.addUserToPlaygroundEvent(ws.getUpsertedId().toString(), user.getUsername());

        // check that user is in event
        Event updatedEvent = beta.getEvent(ws.getUpsertedId().toString());
        Assertions.assertAll(
                () -> assertTrue(updatedEvent.getAssignedUsers().iterator().hasNext()),
                () -> assertEquals(user, updatedEvent.getAssignedUsers().iterator().next())
        );

        beta.deleteUser(user.getUsername());
        beta.deletePlayground(playground.getName());
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
                () -> assertEquals(fetchedPlayground.getName(), fetchedEvent.getPlaygroundName()),
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

        beta.createPlayground(playground);
        beta.createUser(user);

        // add references
        beta.addPedagogueToPlayground(playground.getName(), user.getUsername());

        // check reference is correct
        Playground fetchedPlayground = beta.getPlayground(playground.getName());
        Assertions.assertAll(
                () -> assertTrue(fetchedPlayground.getAssignedPedagogue().iterator().hasNext()),
                () -> assertEquals(user, fetchedPlayground.getAssignedPedagogue().iterator().next())
        );

        // delete pedagogue from playground
        beta.removePedagogueFromPlayground(playground.getName(), user.getUsername());

        // check references are removed
        Playground updatedPlayground = beta.getPlayground(playground.getName());
        assertFalse(updatedPlayground.getAssignedPedagogue().iterator().hasNext());

        beta.deleteUser(user.getUsername());
        beta.deletePlayground(playground.getName());
    }

    @Test
    void removeUserFromPlaygroundEvent() {
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
        WriteResult ws = beta.addPlaygroundEvent(playground.getName(), playgroundEvent);
        beta.createUser(user);

        // add user to event
        beta.addUserToPlaygroundEvent(ws.getUpsertedId().toString(), user.getUsername());

        // check that user is in event
        Event fetchedEvent = beta.getEvent(ws.getUpsertedId().toString());
        Assertions.assertAll(
                () -> assertTrue(fetchedEvent.getAssignedUsers().iterator().hasNext()),
                () -> assertEquals(user, fetchedEvent.getAssignedUsers().iterator().next())
        );

        // remove user from event
        beta.removeUserFromPlaygroundEvent(ws.getUpsertedId().toString(), user.getUsername());

        // check that user is removed from event
        Event updatedEvent = beta.getEvent(ws.getUpsertedId().toString());
        assertFalse(updatedEvent.getAssignedUsers().iterator().hasNext());

        beta.deleteUser(user.getUsername());
        beta.deletePlayground(playground.getName());
    }

    @Test
    void removePlaygroundEvent() {
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
                () -> assertNotNull(fetchedPlayground),
                () -> assertNotNull(fetchedEvent),
                () -> assertEquals(fetchedPlayground.getName(), fetchedEvent.getPlaygroundName()),
                () -> assertEquals(fetchedEvent, playgroundEvent)
        );

        // remove event from playground
        beta.removePlaygroundEvent(fetchedEvent.getId());

        // check that all event references are removed
        Playground updatedPlayground = beta.getPlayground(playground.getName());
        assertFalse(updatedPlayground.getEvents().iterator().hasNext());

        beta.deletePlayground(playground.getName());
    }

    @Test
    void removePlaygroundMessage() {
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

        // check that playground and message is present in db, and the associations between them
        Playground fetchedPlayground = beta.getPlayground(playground.getName());
        Message fetchedMessage = fetchedPlayground.getMessages().iterator().next();
        Assertions.assertAll(
                () -> assertNotNull(fetchedPlayground),
                () -> assertNotNull(fetchedMessage),
                () -> assertEquals(fetchedPlayground.getName(), fetchedMessage.getPlaygroundName()),
                () -> assertEquals(fetchedMessage, playgroundMessage)
        );

        // remove playground message
        beta.removePlaygroundMessage(fetchedMessage.getId());

        // check message references are removed
        Playground updatedPlayground = beta.getPlayground(playground.getName());
        assertFalse(updatedPlayground.getMessages().iterator().hasNext());

        beta.deletePlayground(playground.getName());
    }
}