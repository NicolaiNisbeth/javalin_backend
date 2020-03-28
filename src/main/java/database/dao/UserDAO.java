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

import static org.jongo.Oid.withOid;

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
        WriteResult wr = collection.save(user);

        if (wr.getN() == 0)
            throw new DALException(String.format("User can't be created in %s collection", COLLECTION));

        return wr;
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
     * @return true if updated else false
     * @throws DALException
     */
    @Override
    public boolean updateUser(User user) throws DALException {
        if (user == null)
            throw new DALException(String.format("Can't update user in %s collection when param is null", COLLECTION));

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
     *
     * @param username
     * @return true if deleted else false
     * @throws DALException
     */
    @Override
    public boolean deleteUser(String username) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection collection = jongo.getCollection(COLLECTION);

        boolean isUserDeleted = collection
                .remove("{username : #}", username)
                .wasAcknowledged();

        if (!isUserDeleted)
            throw new DALException(String.format("No user in %s collection with id %s", COLLECTION, username));

        return true;
    }

    @Override
    public boolean deleteAllUsers() throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        try {
            System.out.println(jongo.getCollection(COLLECTION)
                    .remove("{}"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}