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

        User fetchedUser = userDAO.getUser(user.getUsername());
        Assertions.assertEquals(user, fetchedUser);

        userDAO.deleteUser(user.getUsername());

        // try to fetch deleted user and confirm that exception is thrown with msg: no event
        DALException thrown = Assertions.assertThrows(
                DALException.class, () -> userDAO.getUser(user.getUsername())
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

        userDAO.deleteUser(user1.getUsername());
        userDAO.deleteUser(user2.getUsername());
    }

    @Test
    void createUser_UpdateUser_deleteUser() throws DALException {
        User user = new User.Builder("Nicolai")
                .status("Admin")
                .imagePath("asd97a9sd.jpg")
                .email("nicolai.nisbeth@yahoo.com")
                .password("asd123")
                .phoneNumbers("+45 12 34 23 12", "+45 45 74 56 32")
                .build();

        userDAO.createUser(user);

        user.setPassword("123456789");
        userDAO.updateUser(user);

        User updatedUser = userDAO.getUser(user.getUsername());
        Assertions.assertEquals("123456789", updatedUser.getPassword());

        userDAO.deleteUser(user.getUsername());
    }

    @Disabled("This test is disabled because it deletes all users in collection")
    @Test
    void deleteAllUsersInCollection() throws DALException {
        for (User i : userDAO.getUserList()) {
            userDAO.deleteUser(i.getId());
        }
    }

    //Larsens test
    @Test
    void getUserWithName() throws DALException {
     /*   User user = userDAO.getUser("root");
        user.setPassword("root");
        userDAO.updateUser(user);
*/

       /*
        User user2 = new User.Builder("s123")
                .status("pedagog")
                .password("123")
                .setFirstname("Svend")
                .setLastname("Blåtand")
                .build();
        userDAO.createUser(user2);*/

       /* userDAO.deleteUser(user1.getUsername());
        userDAO.deleteUser(user2.getUsername());*/

     /*  User user = userDAO.getUserWithUserName("s185020");
       user.setStatus("admin");
        userDAO.updateUser(user);*/
       /* for (User user2 : userDAO.getUserList()) {
            userDAO.deleteUser(user2.getUsername());
        }
*/
        User user1 = new User.Builder("root")
                .status("admin")
                .password("root")
                .setFirstname("Base")
                .setLastname("Admin")
                .build();
        userDAO.createUser(user1);
    }
}