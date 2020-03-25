package database.dao;

import database.collections.User;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class BetaTest {

    static IBeta beta;
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
    void createPlayground() {
    }

    @Test
    void createUser() {
    }

    @Test
    void addPlaygroundEvent() {
    }

    @Test
    void addPlaygroundMessage() {
    }

    @Test
    void getPlaygrounds() {
    }

    @Test
    void getPlayground() {
    }

    @Test
    void getUsers() {
    }

    @Test
    void getUser() {
    }

    @Test
    void getPlaygroundEvents() {
    }

    @Test
    void getEvent() {
    }

    @Test
    void getPlaygroundMessages() {
    }

    @Test
    void getMessage() {
    }

    @Test
    void updatePlayground() {
    }

    @Test
    void updateUser() {
    }

    @Test
    void updatePlaygroundEvent() {
    }

    @Test
    void updatePlaygroundMessage() {
    }

    @Test
    void deletePlayground() {
    }

    @Test
    void deleteUser() {
    }

    @Test
    void removePlaygroundEvent() {
    }

    @Test
    void removePlaygroundMessage() {
    }

    @Test
    void addPedagogueToPlayground() {
    }

    @Test
    void addUserToPlaygroundEvent() {
    }

    @Test
    void removePedagogueFromPlayground() {
    }

    @Test
    void removeUserFromPlaygroundEvent() {
    }
}