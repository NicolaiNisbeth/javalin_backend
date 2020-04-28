package database.dao;

import com.mongodb.WriteResult;
import database.DALException;
import database.NoModificationException;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;

import java.util.List;

public interface IController {

    // CREATE
    WriteResult createPlayground(Playground playground) throws NoModificationException;

    WriteResult createUser(User user) throws NoModificationException;

    // READ
    Playground getPlayground(String playgroundName) throws NoModificationException;

    User getUser(String username) throws DALException;

    Event getEvent(String eventID);

    Message getMessage(String messageID) throws NoModificationException;

    List<Playground> getPlaygrounds();

    List<User> getUsers();

    List<Event> getPlaygroundEvents(String playgroundName);

    List<Message> getPlaygroundMessages(String playgroundName);

    // UPDATE
    WriteResult updatePlayground(Playground playground) throws NoModificationException;

    WriteResult updateUser(User user) throws NoModificationException;

    WriteResult updatePlaygroundEvent(Event event) throws NoModificationException;

    WriteResult updatePlaygroundMessage(Message message) throws NoModificationException;

    // DELETE
    WriteResult deletePlayground(String playgroundName) throws DALException, NoModificationException;

    WriteResult deleteUser(String username);

    // ASSOCIATIONS
    boolean addPedagogueToPlayground(String plagroundName, String username);

    boolean addUserToPlaygroundEvent(String eventID, String username);

    WriteResult addPlaygroundEvent(String playgroundName, Event event);

    WriteResult addPlaygroundMessage(String playgroundName, Message message);

    boolean removePedagogueFromPlayground(String playgroundName, String username) throws NoModificationException;

    boolean removeUserFromPlaygroundEvent(String eventID, String username);

    WriteResult removePlaygroundEvent(String eventID);

    WriteResult removePlaygroundMessage(String messageID);
}