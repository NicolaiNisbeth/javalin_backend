package database.unit;

import database.exceptions.NoModificationException;
import database.TestDB;
import database.dto.UserDTO;
import database.dao.IUserDAO;
import database.dao.UserDAO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDAOTest {
    private static IUserDAO userDAO = new UserDAO(TestDB.getInstance());

    @BeforeAll
    static void killAll(){
        userDAO.deleteAllUsers();
    }

    @Test
    void created_user_should_be_fetched_user() throws NoModificationException {
        UserDTO user = new UserDTO.Builder("Nicolai")
                .status("Admin")
                .imagePath("asd97a9sd.jpg")
                .setEmail("nicolai.nisbeth@yahoo.com")
                .setPassword("asd123")
                .phoneNumbers("+45 12 34 23 12", "+45 45 74 56 32")
                .build();

        userDAO.createUser(user);
        UserDTO fetchedUser = userDAO.getUser(user.getUsername());
        Assertions.assertEquals(user, fetchedUser);
        userDAO.deleteUser(user.getUsername());
    }

    @Test
    void create_two_users_should_fetch_list_size_two() throws NoModificationException {
        UserDTO user1 = new UserDTO.Builder("Nicolai")
                .status("Admin")
                .imagePath("asd97a9sd.jpg")
                .setEmail("nicolai.nisbeth@yahoo.com")
                .setPassword("asd123")
                .phoneNumbers("+45 12 34 23 12", "+45 45 74 56 32")
                .build();

        UserDTO user2 = new UserDTO.Builder("Peter")
                .status("Admin")
                .imagePath("asd97a9s343d.jpg")
                .setEmail("peter.pavlidou@yahoo.com")
                .setPassword("poggersinthechat3")
                .phoneNumbers("+45 45 74 56 32")
                .build();

        userDAO.createUser(user1);
        userDAO.createUser(user2);

        List<UserDTO> userList = userDAO.getUserList();
        Assertions.assertAll(
                () -> Assertions.assertEquals(2, userList.size()),
                () -> Assertions.assertEquals(user1, userList.get(0)),
                () -> Assertions.assertEquals(user2, userList.get(1))
        );

        userDAO.deleteUser(user1.getUsername());
        userDAO.deleteUser(user2.getUsername());
    }

    @Test
    void update_user_should_fetch_updated_user() throws NoModificationException {
        UserDTO user = new UserDTO.Builder("s175565")
                .setFirstname("Nicolai")
                .setLastname("Nisbeth")
                .status("admin")
                .setEmail("s175565@student.dtu.dk")
                .setPassword("nicolai123456789")
                .phoneNumbers("+45 23 45 23 12", "+45 27 38 94 21")
                .imagePath("asd9as9d8a89sd.jpg")
                .build();

        userDAO.createUser(user);
        UserDTO fetchedUser = userDAO.getUser(user.getUsername());

        // update values
        fetchedUser.setEmail("nicolai.nisbeth@hotmail.com");
        fetchedUser.setPassword("kodenwhn");
        fetchedUser.setStatus("pedagogue");
        userDAO.updateUser(fetchedUser);

        // check that user has updated values
        UserDTO updatedUser = userDAO.getUser(fetchedUser.getUsername());
        Assertions.assertAll(
                () -> assertEquals(fetchedUser.getEmail(), updatedUser.getEmail()),
                () -> assertEquals(fetchedUser.getPassword(), updatedUser.getPassword()),
                () -> assertEquals(fetchedUser.getStatus(), updatedUser.getStatus())
        );

        userDAO.deleteUser(user.getUsername());
    }

    @Test
    void delete_all_users_in_collection() throws NoModificationException {
        UserDTO user1 = new UserDTO.Builder("Nicolai")
                .status("Admin")
                .imagePath("asd97a9sd.jpg")
                .setEmail("nicolai.nisbeth@yahoo.com")
                .setPassword("asd123")
                .phoneNumbers("+45 12 34 23 12", "+45 45 74 56 32")
                .build();

        UserDTO user2 = new UserDTO.Builder("Peter")
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
    void null_in_create_should_throw_illegalArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.createUser(null));
    }

    @Test
    void null_in_get_user_should_throw_illegalArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.getUser(null));
    }

    @Test
    void empty_input_in_get_should_throw_illegalArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.getUser(""));
    }

    @Test
    void no_events_in_get_users_should_throw_noSuchElements(){
        Assertions.assertThrows(NoSuchElementException.class, () -> userDAO.getUserList());
    }

    @Test
    void null_in_update_should_throw_illegalArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.updateUser(null));
    }

    @Test
    void null_in_delete_should_throw_illegalArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.deleteUser(null));
    }

    @Test
    void empty_id_in_delete_should_throw_illegalArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> userDAO.deleteUser(""));
    }
}