package database.dao;

import com.mongodb.WriteResult;
import database.DALException;
import database.NoModificationException;
import database.collections.User;

import java.util.List;
import java.util.NoSuchElementException;

public interface IUserDAO {
    String COLLECTION = "users";

    WriteResult createUser(User user) throws IllegalArgumentException, NoModificationException;

    User getUser(String username) throws NoSuchElementException, IllegalArgumentException;

    List<User> getUserList() throws NoSuchElementException;

    WriteResult updateUser(User user) throws IllegalArgumentException, NoModificationException;

    WriteResult deleteUser(String username) throws NoModificationException;

    WriteResult deleteAllUsers();
}
