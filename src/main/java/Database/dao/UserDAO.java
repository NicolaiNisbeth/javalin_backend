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
     * Create user in users collection
     * @param user
     * @return true if created else false
     * @throws DALException
     */
    @Override
    public boolean createUser(User user) throws DALException {
        if (user == null)
            throw new DALException(String.format("Can't create user in %s collection when user is null", COLLECTION));

        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);

        boolean isUserCreated = collection.save(user).wasAcknowledged();

        if (!isUserCreated)
            throw new DALException(String.format("User can't be created in %s collection", COLLECTION));

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
        MongoCollection collection = jongo.getCollection(COLLECTION);

        User user = collection.findOne(new ObjectId(id)).as(User.class);

        if (user == null)
            throw new DALException(String.format("No user in %s collection with id %s", COLLECTION, id));

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
        MongoCollection collection = jongo.getCollection(COLLECTION);

        List<User> userList = new ArrayList<>();
        for (User user : collection.find().as(User.class)) {
            userList.add(user);
        }

        if (userList.isEmpty())
            throw new DALException(String.format("No users in %s collection", COLLECTION));

        return userList;
    }

    /**
     * Replace user with same id
     * @param user
     * @return true if updated else false
     * @throws DALException
     */
    @Override
    public boolean updateUser(User user) throws DALException {
        if (user == null)
            throw new DALException(String.format("Can't update user in %s collection when input user is null", COLLECTION));

        if (user.getId() == null)
            throw new DALException(String.format("Can't find user to be updated in %s collection when id is null", COLLECTION));

        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);

        boolean isUserUpdated = collection
                .update(new ObjectId(user.getId()))
                .with(user)
                .wasAcknowledged();

        if (!isUserUpdated)
            throw new DALException(String.format("No user in %s collection with id %s", COLLECTION, user.getId()));

        return true;
    }

    /**
     * Delete user with given id
     * @param id
     * @return true if deleted else false
     * @throws DALException
     */
    @Override
    public boolean deleteUser(String id) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);

        boolean isUserDeleted = collection
                .remove(new ObjectId(id))
                .wasAcknowledged();

        if (!isUserDeleted)
            throw new DALException(String.format("No user in %s collection with id %s", COLLECTION, id));

        return true;
    }
}