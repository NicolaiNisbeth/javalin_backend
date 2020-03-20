package Database.dao;

import Database.DALException;
import Database.collections.User;

import java.util.List;

public interface IUserDAO {
    String COLLECTION = "users";

    void createUser()                   throws DALException;
    User getUser()                      throws DALException;
    List<User> getUserList()            throws DALException;
    boolean updateUser(User user)       throws DALException;
    boolean deleteUser(int id)          throws DALException;
}
