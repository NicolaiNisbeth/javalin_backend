package database.dao;

import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import database.IDataSource;
import database.exceptions.NoModificationException;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;

import java.util.List;
import java.util.NoSuchElementException;

public interface IController {

    // CREATE
    WriteResult createPlayground(Playground playground) throws IllegalArgumentException, NoModificationException;
    WriteResult createUser(User user) throws IllegalArgumentException, NoModificationException;

    // READ
    Playground getPlayground(String playgroundName) throws IllegalArgumentException, NoSuchElementException;
    User getUser(String username) throws IllegalArgumentException, NoSuchElementException;
    Event getEvent(String eventID) throws IllegalArgumentException, NoSuchElementException;
    Message getMessage(String messageID) throws IllegalArgumentException, NoSuchElementException;
    List<Playground> getPlaygrounds() throws NoSuchElementException;

    List<User> getUsers() throws NoSuchElementException;
    List<Event> getEventsInPlayground(String playgroundName);
    List<Message> getMessagesInPlayground(String playgroundName);

    // UPDATE
    WriteResult updatePlayground(Playground playground) throws IllegalArgumentException, NoModificationException ;
    WriteResult updateUser(User user) throws IllegalArgumentException, NoModificationException ;
    WriteResult updatePlaygroundEvent(Event event) throws IllegalArgumentException, NoModificationException ;
    WriteResult updatePlaygroundMessage(Message message) throws IllegalArgumentException, NoModificationException ;

    // DELETE
    WriteResult deletePlayground(String playgroundName) throws NoSuchElementException, NoModificationException, MongoException;
    WriteResult deleteUser(String username) throws NoSuchElementException, NoModificationException, MongoException;

    // ASSOCIATIONS
    WriteResult createPlaygroundEvent(String playgroundName, Event event) throws NoSuchElementException, NoModificationException, MongoException;
    WriteResult createPlaygroundMessage(String playgroundName, Message message) throws NoSuchElementException, NoModificationException, MongoException;
    WriteResult addPedagogueToPlayground(String plagroundName, String username) throws NoSuchElementException, NoModificationException, MongoException;
    WriteResult addUserToEvent(String eventID, String username) throws NoSuchElementException, NoModificationException, MongoException;
    void removePedagogueFromPlayground(String playgroundName, String username) throws NoModificationException, NoSuchElementException, MongoException;
    void removeUserFromEvent(String eventID, String username) throws NoModificationException, NoSuchElementException, MongoException;
    WriteResult deletePlaygroundEvent(String eventID) throws NoModificationException, NoSuchElementException, MongoException;
    WriteResult deletePlaygroundMessage(String messageID) throws NoModificationException, NoSuchElementException, MongoException;

    void setDataSource(IDataSource dataSource);
    void killAll();
}