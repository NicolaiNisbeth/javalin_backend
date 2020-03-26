package database.dao;

import com.mongodb.WriteResult;
import database.DALException;
import database.collections.User;

import java.util.List;

public interface IUserDAO {
    String COLLECTION = "users";

    WriteResult createUser(User user)          throws DALException;
    User getUser(String username)             throws DALException;
    List<User> getUserList()            throws DALException;
    boolean updateUser(User user)       throws DALException;
    boolean deleteUser(String username)          throws DALException;
    boolean deleteAllUsers()                      throws DALException;
}
