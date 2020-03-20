package Database.dao;

import Database.DALException;
import Database.collections.User;

import java.util.List;

public interface IUserDAO {
    String COLLECTION = "users";

    boolean createUser(User user)          throws DALException;
    User getUser(String id)             throws DALException;
    List<User> getUserList()            throws DALException;
    boolean updateUser(User user)       throws DALException;
    boolean deleteUser(int id)          throws DALException;
}
