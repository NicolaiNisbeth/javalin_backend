package database.dao;

import com.mongodb.WriteResult;
import database.DALException;
import database.collections.User;

import java.util.List;

public interface IUserDAO {
    String COLLECTION = "users";

    WriteResult createUser(User user) throws DALException;

    User getUser(String username) throws DALException;

    List<User> getUserList() throws DALException;

    WriteResult updateUser(User user) throws DALException;

    WriteResult deleteUser(String username) throws DALException;

    WriteResult deleteAllUsers() throws DALException;
}
