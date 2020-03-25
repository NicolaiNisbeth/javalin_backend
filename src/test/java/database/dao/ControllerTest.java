package database.dao;

import database.DALException;
import database.collections.Details;
import database.collections.Event;
import database.collections.Playground;
import database.collections.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Date;

class ControllerTest {
    static Controller controller;
    static IEventDAO eventDAO;
    static IMessageDAO messageDAO;
    static IPlaygroundDAO playgroundDAO;
    static IUserDAO userDAO;

    final static User ADMIN_USER = new User.Builder("admin").status("admin").build();;

    @BeforeAll
    static void setup(){
        controller = Controller.getInstance();
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
        controller = null;
    }


    @Test
    void getAllPlaygrounds() {
    }

    @Test
    void createPlayground() {
    }

    @Test
    void addPedagogueToPlayground() {
    }

    @Test
    void removePedagogueFromPlayground() {
    }

    @Test
    void getEventsInPlayground() {
    }

    @Test
    void addEventToPlayground() {
    }

    @Test
    void updateEvent() {
    }

    @Test
    void deleteEventInPlayground() {
    }

    @Test
    void signupForEventInPlayground() {
    }

    @Test
    void getPlaygroundMessage() {
    }

    @Test
    void createPlaygroundMessage() {
    }

    @Test
    void updatePlaygroundMessage() {
    }

    @Test
    void deletePlaygroundMessage() {
    }

    @Test
    void getUser() {
        // make sure all relationships are correct
    }

    @Test
    void registerUser() throws DALException {
        User user = new User.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .email("s175565@student.dtu.dk")
                .password("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        Assertions.assertThrows(DALException.class,
                () -> controller.getUser(user.getUsername()), "User already exists"
        );

        controller.registerUser(user);

        Assertions.assertDoesNotThrow(() -> controller.getUser(user.getUsername()), "User doesn't exist");

        User createdUser = controller.getUser(user.getUsername());
        Assertions.assertEquals(user, createdUser, "The correct user is not created");

        controller.deleteUser(ADMIN_USER, user.getUsername());
    }

    @Test
    void updateUser() {

    }

    @Test
    @DisplayName("Delete user without any references to other collections")
    void deleteVirginUser() throws DALException {
        User user = new User.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .email("s175565@student.dtu.dk")
                .password("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        controller.registerUser(user);
        Assertions.assertDoesNotThrow(() -> controller.getUser(user.getUsername()), "User doesn't exist");

        controller.deleteUser(ADMIN_USER, user.getUsername());
        Assertions.assertThrows(DALException.class,
                ()-> controller.getUser(user.getUsername()), "User is not deleted");
    }

    @Test
    @DisplayName("Delete pedagogue with references to playgrounds")
    void deletePedagogue() throws DALException {
        User pedagogue = new User.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .email("s175565@student.dtu.dk")
                .password("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        Playground playground = new Playground.Builder("Vandlegeparken")
                .setStreetName("Agervænget")
                .setStreetNumber(34)
                .setZipCode(3650)
                .setCommune("Egedal")
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("asd97a9s8d89asd.jpg")
                .build();

        /*
        Event event = new Event.Builder("Fodbold og snobrød")
                .description("Fodbold i lystrup park")
                .participants(30)
                .imagePath("asd98asd89asd.jpg")
                .details(new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis())))
                .build();

         */

        controller.registerUser(pedagogue);
        controller.createPlayground(ADMIN_USER, playground);

        Assertions.assertAll(
                () -> Assertions.assertDoesNotThrow(()->controller.getUser(pedagogue.getUsername())),
                () -> Assertions.assertDoesNotThrow(()->controller.getPlayground(playground.getName()))
        );

        controller.addPedagogueToPlayground(ADMIN_USER, playground.getName(), pedagogue.getUsername());


        controller.deleteUser(ADMIN_USER, pedagogue.getUsername());
        Assertions.assertAll(
                () -> Assertions.assertThrows(DALException.class, ()->controller.getUser(pedagogue.getUsername())),
                () -> Assertions.assertFalse(controller.getPlayground(playground.getName()).getAssignedPedagogue().contains(pedagogue.getUsername()))
        );

        playgroundDAO.deletePlayground(playground.getName());


    }
}