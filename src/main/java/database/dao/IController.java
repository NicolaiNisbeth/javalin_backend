package database.dao;

import com.mongodb.WriteResult;
import database.DALException;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;

import java.util.List;

public interface IController {

    // CREATE
    WriteResult createPlayground(Playground playground);
    WriteResult createUser(User user);

    // READ
    Playground getPlayground(String playgroundName);
    User getUser(String username) throws DALException;
    Event getEvent(String eventID);
    Message getMessage(String messageID);

    List<Playground> getPlaygrounds();
    List<User> getUsers();
    List<Event> getPlaygroundEvents(String playgroundName);
    List<Message> getPlaygroundMessages(String playgroundName);

    // UPDATE
    boolean updatePlayground(Playground playground);
    boolean updateUser(User user);
    boolean updatePlaygroundEvent(Event event);
    boolean updatePlaygroundMessage(Message message);

    // DELETE
    boolean deletePlayground(String playgroundName);
    boolean deleteUser(String username);

    // ASSOCIATIONS
    boolean addPedagogueToPlayground(String plagroundName, String username);
    boolean addUserToPlaygroundEvent(String eventID, String username);
    WriteResult addPlaygroundEvent(String playgroundName, Event event);
    WriteResult addPlaygroundMessage(String playgroundName, Message message);

    boolean removePedagogueFromPlayground(String playgroundName, String username);
    boolean removeUserFromPlaygroundEvent(String eventID, String username);
    boolean removePlaygroundEvent(String eventID);
    boolean removePlaygroundMessage(String messageID);
}