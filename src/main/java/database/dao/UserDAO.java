package database.dao;

import com.mongodb.WriteResult;
import database.DALException;
import database.DataSource;
import database.collections.User;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    /**
     * Create user in users collection
     *
     * @param user
     * @return write result
     * @throws DALException
     */
    @Override
    public WriteResult createUser(User user) throws DALException {
        if (user == null)
            throw new DALException(String.format("Can't create user in %s collection when user is null", COLLECTION));

        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        WriteResult writeResult = collection.save(user);

        if (writeResult.getN() == 0)
            throw new DALException(String.format("User can't be created in %s collection", COLLECTION));

        return writeResult;
    }

    /**
     * Get user with given username
     *
     * @param username
     * @return user
     * @throws DALException
     */
    @Override
    public User getUser(String username) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        User user = collection.findOne("{username: #}", username).as(User.class);

        if (user == null)
            throw new DALException(String.format("No user in %s collection with username %s", COLLECTION, username));
        return user;
    }

    /**
     * Get all users in collection
     *
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
     *
     * @param user
     * @return WriteResult
     * @throws DALException
     */
    @Override
    public WriteResult updateUser(User user) throws DALException {
        if (user == null)
            throw new DALException(String.format("Can't update user in %s collection when param is null", COLLECTION));

        if (user.getId() == null)
            throw new DALException(String.format("Can't find user to be updated in %s collection when id is null", COLLECTION));

        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);

        WriteResult writeResult = collection
                .update(new ObjectId(user.getId()))
                .with(user);

        if (!writeResult.wasAcknowledged())
            throw new DALException(String.format("No user in %s collection with id %s", COLLECTION, user.getId()));

        return writeResult;
    }

    /**
     * Delete user with given id
     *
     * @param username
     * @return WriteResult
     * @throws DALException
     */
    @Override
    public WriteResult deleteUser(String username) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);

        WriteResult writeResult = collection
                .remove("{username : #}", username);

        if (!writeResult.wasAcknowledged())
            throw new DALException(String.format("No user in %s collection with id %s", COLLECTION, username));

        return  writeResult;
    }

    @Override
    public WriteResult deleteAllUsers() throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);

        WriteResult writeResult = collection
                .remove("{}");

        return writeResult;
    }
}