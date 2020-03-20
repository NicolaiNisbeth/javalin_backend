package Database.dao;

import Database.DALException;
import Database.collections.User;

import java.util.List;

public class UserDAO implements IUserDAO {
    @Override
    public void createUser() throws DALException {

    }

    @Override
    public User getUser() throws DALException {
        return null;
    }

    @Override
    public List<User> getUserList() throws DALException {
        return null;
    }

    @Override
    public boolean updateUser(User user) throws DALException {
        return false;
    }

    @Override
    public boolean deleteUser(int id) throws DALException {
        return false;
    }
}
