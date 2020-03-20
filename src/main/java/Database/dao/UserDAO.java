package Database.dao;

import Database.DALException;
import Database.DBConnector;
import Database.collections.User;
import com.mongodb.WriteResult;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserDAO implements IUserDAO {

    /**
     * Create a user in users collection
     * @param user
     * @throws DALException
     */
    @Override
    public boolean createUser(User user) throws DALException {
        if (user == null)
            throw new DALException(String.format("Can't create user in %s collection when user is null.", UserDAO.COLLECTION));

        Jongo jongo = new Jongo(DBConnector.getInstance());
        MongoCollection collection = jongo.getCollection(UserDAO.COLLECTION);

        boolean isUserCreated = collection.save(user).isUpdateOfExisting();

        if (!isUserCreated)
            throw new DALException(String.format("User can't be created %s collection.", UserDAO.COLLECTION));

        return true;
    }

    /**
     * Get user with given id
     * @param id
     * @return user
     * @throws DALException
     */
    @Override
    public User getUser(String id) throws DALException {
        Jongo jongo = new Jongo(DBConnector.getInstance());
        MongoCollection collection = jongo.getCollection(UserDAO.COLLECTION);

        String query = String.format("{id : %s}", id);
        User user = collection.findOne(query).as(User.class);

        if (user == null)
            throw new DALException(String.format("No user in %s collection with id %s.", UserDAO.COLLECTION, id));

        return user;
    }

    /**
     * Get all users in collection
     * @return list of users
     * @throws DALException
     */
    @Override
    public List<User> getUserList() throws DALException {
        Jongo jongo = new Jongo(DBConnector.getInstance());
        MongoCollection collection = jongo.getCollection(UserDAO.COLLECTION);

        List<User> userList = new ArrayList<>();
        for (User user : collection.find().as(User.class))
            userList.add(user);

        if (userList.isEmpty())
            throw new DALException(String.format("No users in %s collection.", UserDAO.COLLECTION));

        return userList;
    }

    @Override
    public boolean updateUser(User user) throws DALException {
        if (user == null)
            throw new DALException(String.format("Can't update user in %s collection when input user is null.", UserDAO.COLLECTION));

        Jongo jongo = new Jongo(DBConnector.getInstance());
        MongoCollection collection = jongo.getCollection(UserDAO.COLLECTION);

        boolean isUserUpdated = collection
                .update(String.format("{id : %s}", user.getId()))
                .with(user)
                .isUpdateOfExisting();

        if (!isUserUpdated)
            throw new DALException(String.format("No user in %s collection with id %s.", UserDAO.COLLECTION, user.getId()));

        return true;
    }


    /**
     * Delete user with given id
     * @param id
     * @return
     * @throws DALException
     */
    @Override
    public boolean deleteUser(int id) throws DALException {
        Jongo jongo = new Jongo(DBConnector.getInstance());
        MongoCollection collection = jongo.getCollection(UserDAO.COLLECTION);

        boolean isUserDeleted = collection
                .remove(String.format("{id : %s}", id))
                .isUpdateOfExisting();

        if (!isUserDeleted)
            throw new DALException(String.format("No user in %s collection with id %s.", UserDAO.COLLECTION, id));

        return true;
    }
}
