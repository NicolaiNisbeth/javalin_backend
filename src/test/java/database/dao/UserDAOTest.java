package database.dao;

import database.DALException;
import database.collections.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

class UserDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    @AfterEach
    void tearDown() {
        userDAO = null;
    }

    @Test
    void createUser_DeleteUser() throws DALException {
        User user = new User.Builder("Nicolai")
                .status("Admin")
                .imagePath("asd97a9sd.jpg")
                .email("nicolai.nisbeth@yahoo.com")
                .password("asd123")
                .phoneNumbers("+45 12 34 23 12", "+45 45 74 56 32")
                .build();

        userDAO.createUser(user);

        User fetchedUser = userDAO.getUser(user.getId());
        Assertions.assertEquals(user, fetchedUser);

        userDAO.deleteUser(user.getId());

        // try to fetch deleted user and confirm that exception is thrown with msg: no event
        DALException thrown = Assertions.assertThrows(
                DALException.class, () -> userDAO.getUser(user.getId())
        );
        Assertions.assertTrue(thrown.getMessage().contains("No user"));
    }

    @Test
    void createUsers_GetUserList_DeleteUsers() throws DALException {
        User user1 = new User.Builder("Nicolai")
                .status("Admin")
                .imagePath("asd97a9sd.jpg")
                .email("nicolai.nisbeth@yahoo.com")
                .password("asd123")
                .phoneNumbers("+45 12 34 23 12", "+45 45 74 56 32")
                .build();

        User user2 = new User.Builder("Peter")
                .status("Admin")
                .imagePath("asd97a9s343d.jpg")
                .email("peter.pavlidou@yahoo.com")
                .password("poggersinthechat3")
                .phoneNumbers("+45 45 74 56 32")
                .build();

        userDAO.createUser(user1);
        userDAO.createUser(user2);

        List<User> userList = userDAO.getUserList();
        Assertions.assertEquals(userList.size(), 2);

        Assertions.assertEquals(userList.get(0), user1);
        Assertions.assertEquals(userList.get(1), user2);

        userDAO.deleteUser(user1.getId());
        userDAO.deleteUser(user2.getId());
    }

    @Test
    void createUser_UpdateUser_deleteUser() throws DALException {
        User event = new User.Builder("Nicolai")
                .status("Admin")
                .imagePath("asd97a9sd.jpg")
                .email("nicolai.nisbeth@yahoo.com")
                .password("asd123")
                .phoneNumbers("+45 12 34 23 12", "+45 45 74 56 32")
                .build();

        userDAO.createUser(event);

        event.setPassword("123456789");
        userDAO.updateUser(event);

        User updatedUser = userDAO.getUser(event.getId());
        Assertions.assertEquals("123456789", updatedUser.getPassword());

        userDAO.deleteUser(event.getId());
    }

    @Disabled("This test is disabled because it deletes all users in collection")
    @Test
    void deleteAllUsersInCollection() throws DALException {
        for (User i: userDAO.getUserList()) {
            userDAO.deleteUser(i.getId());
        }
    }
}