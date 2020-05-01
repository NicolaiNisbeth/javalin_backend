package database.dao;

import com.mongodb.WriteResult;
import database.IDataSource;
import database.exceptions.NoModificationException;
import database.dto.UserDTO;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class UserDAO implements IUserDAO {

    private IDataSource dataSource;

    public UserDAO(IDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Create user in db
     * @param user uniquely identifies a user in db
     * @return writeResult where upsertedId can be derived
     * @throws IllegalArgumentException when user is null
     * @throws NoModificationException when user is not created
     */
    @Override
    public WriteResult createUser(UserDTO user) throws IllegalArgumentException, NoModificationException {
        if (user == null)
            throw new IllegalArgumentException(
                    String.format("Can't create user in %s collection when user is null", COLLECTION));

        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        WriteResult wr = collection.save(user);

        if (wr.getN() == 0)
            throw new NoModificationException(
                    String.format("User can't be created in %s collection", COLLECTION));

        return wr;
    }

    /**
     * Get user in db
     * @param username unique id to find user
     * @return user with given username
     * @throws IllegalArgumentException when username is invalid
     * @throws NoSuchElementException when user is not found in db
     */
    @Override
    public UserDTO getUser(String username) throws NoSuchElementException, IllegalArgumentException {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException(
                    String.format("%s as ID is not valid in identifying a user", username));

        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        UserDTO user = collection.findOne("{username: #}", username).as(UserDTO.class);

        if (user == null)
            throw new NoSuchElementException(
                    String.format("No user in %s collection with username %s", COLLECTION, username));

        return user;
    }

    /**
     * Get list of all users in db
     * @return list of users
     * @throws NoSuchElementException when no users are found in db
     */
    @Override
    public List<UserDTO> getUserList() throws NoSuchElementException {
        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        List<UserDTO> userList = new ArrayList<>();
        for (UserDTO user : collection.find().as(UserDTO.class)) {
            userList.add(user);
        }

        if (userList.isEmpty())
            throw new NoSuchElementException(
                    String.format("No users in %s collection", COLLECTION));

        return userList;
    }

    /**
     * Update user in db
     * @param user with updated values
     * @return writeResult where id of updated user can be derived
     * @throws IllegalArgumentException when user is null
     * @throws NoModificationException when no user is updated
     */
    @Override
    public WriteResult updateUser(UserDTO user) throws IllegalArgumentException, NoModificationException {
        if (user == null || user.getId() == null)
            throw new IllegalArgumentException(
                    String.format("Can't update user in %s collection when param is null", COLLECTION));

        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);

        WriteResult wr = collection
                .update(new ObjectId(user.getId()))
                .with(user);

        if (!wr.wasAcknowledged())
            throw new NoModificationException(
                    String.format("User in %s collection with id %s was not updated", COLLECTION, user.getId()));

        return wr;
    }

    /**
     * Delete user in db
     * @param username unique id to find user
     * @return writeResult where id of deleted user can be derived
     * @throws NoModificationException when no user is deleted
     */
    @Override
    public WriteResult deleteUser(String username) throws NoModificationException {
        if (username == null || username.isEmpty())
            throw new IllegalArgumentException(
                    String.format("%s as ID is not valid in identifying a user", username));

        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        WriteResult wr = collection.remove("{username : #}", username);

        if (!wr.wasAcknowledged())
            throw new NoModificationException(
                    String.format("No user in %s collection with id was no updated %s", COLLECTION, username));

        return  wr;
    }

    /**
     * Delete all users in db
     * @return writeResult where ids of deleted users can be derived
     */
    @Override
    public WriteResult deleteAllUsers() {
        Jongo jongo = new Jongo(dataSource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        return collection.remove("{}");
    }

    @Override
    public void setDataSource(IDataSource dataSource) {
        this.dataSource = dataSource;
    }
}