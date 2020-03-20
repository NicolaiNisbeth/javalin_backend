package Database.dao;

import Database.DALException;
import Database.DataSource;
import Database.collections.User;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import java.util.ArrayList;
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
            throw new DALException(String.format("Can't create user in %s collection when user is null", UserDAO.COLLECTION));

        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(UserDAO.COLLECTION);

        boolean isUserCreated = collection.save(user).wasAcknowledged();

        if (!isUserCreated)
            throw new DALException(String.format("User can't be created in %s collection", UserDAO.COLLECTION));

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
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(UserDAO.COLLECTION);

        User user = collection.findOne(new ObjectId(id)).as(User.class);

        if (user == null)
            throw new DALException(String.format("No user in %s collection with id %s", UserDAO.COLLECTION, id));

        return user;
    }

    /**
     * Get all users in collection
     * @return list of users
     * @throws DALException
     */
    @Override
    public List<User> getUserList() throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(UserDAO.COLLECTION);

        List<User> userList = new ArrayList<>();

        for (User user : collection.find().as(User.class)) {
            userList.add(user);
        }

        if (userList.isEmpty())
            throw new DALException(String.format("No users in %s collection", UserDAO.COLLECTION));

        return userList;
    }

    @Override
    public boolean updateUser(User user) throws DALException {

        if (user == null)
            throw new DALException(String.format("Can't update user in %s collection when input user is null", UserDAO.COLLECTION));

        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(UserDAO.COLLECTION);

        boolean isUserUpdated = collection
                .update(new ObjectId(user.getId()))
                .with(user)
                .wasAcknowledged();

        if (!isUserUpdated)
            throw new DALException(String.format("No user in %s collection with id %s", UserDAO.COLLECTION, user.getId()));

        return true;


    }

    /**
     * Delete user with given id
     * @param id
     * @return
     * @throws DALException
     */
    @Override
    public boolean deleteUser(String id) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(UserDAO.COLLECTION);

        boolean isUserDeleted = collection
                .remove(new ObjectId(id))
                .wasAcknowledged();

        if (!isUserDeleted)
            throw new DALException(String.format("No user in %s collection with id %s", UserDAO.COLLECTION, id));

        return true;
    }
}
