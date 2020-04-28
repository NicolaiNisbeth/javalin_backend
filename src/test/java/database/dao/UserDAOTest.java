package database.dao;

import database.DALException;
import database.DataSource;
import database.NoModificationException;
import database.collections.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

class UserDAOTest {
    private static IUserDAO userDAO = new UserDAO(DataSource.getTestDB());

    @BeforeAll
    static void killAll(){
        userDAO.deleteAllUsers();
    }

    @Test
    void createdUserShouldBeFetchedUser() throws NoModificationException {
        User user = new User.Builder("Nicolai")
                .status("Admin")
                .imagePath("asd97a9sd.jpg")
                .setEmail("nicolai.nisbeth@yahoo.com")
                .setPassword("asd123")
                .phoneNumbers("+45 12 34 23 12", "+45 45 74 56 32")
                .build();

        userDAO.createUser(user);
        User fetchedUser = userDAO.getUser(user.getUsername());
        Assertions.assertEquals(user, fetchedUser);
        userDAO.deleteUser(user.getUsername());
    }

    @Test
    void createTwoUsersShouldFetchListSizeTwo() throws NoModificationException {
        User user1 = new User.Builder("Nicolai")
                .status("Admin")
                .imagePath("asd97a9sd.jpg")
                .setEmail("nicolai.nisbeth@yahoo.com")
                .setPassword("asd123")
                .phoneNumbers("+45 12 34 23 12", "+45 45 74 56 32")
                .build();

        User user2 = new User.Builder("Peter")
                .status("Admin")
                .imagePath("asd97a9s343d.jpg")
                .setEmail("peter.pavlidou@yahoo.com")
                .setPassword("poggersinthechat3")
                .phoneNumbers("+45 45 74 56 32")
                .build();

        userDAO.createUser(user1);
        userDAO.createUser(user2);

        List<User> userList = userDAO.getUserList();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, userList.size()),
                () -> Assertions.assertEquals(user1, userList.get(0)),
                () -> Assertions.assertEquals(user2, userList.get(1))
        );

        userDAO.deleteUser(user1.getUsername());
        userDAO.deleteUser(user2.getUsername());
    }

    @Test
    void updateUserShouldFetchUpdatedUser() throws NoModificationException {
        User user1 = new User.Builder("Nicolai")
                .status("Admin")
                .imagePath("asd97a9sd.jpg")
                .setEmail("nicolai.nisbeth@yahoo.com")
                .setPassword("asd123")
                .phoneNumbers("+45 12 34 23 12", "+45 45 74 56 32")
                .build();

        userDAO.createUser(user1);
        user1.setPassword("ny string");
        userDAO.updateUser(user1);

        User updatedUser = userDAO.getUser(user1.getUsername());
        Assertions.assertEquals("ny string", updatedUser.getPassword());
        userDAO.deleteUser(user1.getUsername());
    }

    @Test
    void deleteAllUsersInCollection() throws NoModificationException {
        User user1 = new User.Builder("Nicolai")
                .status("Admin")
                .imagePath("asd97a9sd.jpg")
                .setEmail("nicolai.nisbeth@yahoo.com")
                .setPassword("asd123")
                .phoneNumbers("+45 12 34 23 12", "+45 45 74 56 32")
                .build();

        User user2 = new User.Builder("Peter")
                .status("Admin")
                .imagePath("asd97a9s343d.jpg")
                .setEmail("peter.pavlidou@yahoo.com")
                .setPassword("poggersinthechat3")
                .phoneNumbers("+45 45 74 56 32")
                .build();

        userDAO.createUser(user1);
        userDAO.createUser(user2);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(userDAO.getUser(user1.getUsername())),
                () -> Assertions.assertNotNull(userDAO.getUser(user2.getUsername()))
        );

        userDAO.deleteAllUsers();
        Assertions.assertThrows(NoSuchElementException.class, () -> userDAO.getUserList());
    }

    @Test
    void nullInCreateShouldThrowIllegalArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.createUser(null));
    }

    @Test
    void nullInGetShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.getUser(null));
    }
    @Test
    void emptyIdInGetShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.getUser(""));
    }

    @Test
    void noEventsInGetEventsShouldThrowNoSuchElements(){
        Assertions.assertThrows(NoSuchElementException.class, () -> userDAO.getUserList());
    }

    @Test
    void nullInUpdateShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.updateUser(null));
    }

    @Test
    void nullInDeleteShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.deleteUser(null));
    }

    @Test
    void emptyIdInDeleteShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.deleteUser(""));
    }
}