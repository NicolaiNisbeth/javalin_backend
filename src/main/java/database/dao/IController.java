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

    List<Event> getEventsInPlayground(String playgroundName);

    List<Message> getMessagesInPlayground(String playgroundName);

    // UPDATE
    WriteResult updatePlayground(Playground playground) throws NoModificationException;

    WriteResult updateUser(User user) throws NoModificationException;

    WriteResult updatePlaygroundEvent(Event event) throws NoModificationException;

    WriteResult updatePlaygroundMessage(Message message) throws NoModificationException;

    // DELETE
    WriteResult deletePlayground(String playgroundName) throws DALException, NoModificationException;

    WriteResult deleteUser(String username) throws NoModificationException;

    // ASSOCIATIONS
    WriteResult addPedagogueToPlayground(String plagroundName, String username) throws NoModificationException;

    WriteResult addUserToEvent(String eventID, String username) throws NoModificationException;

    WriteResult createPlaygroundEvent(String playgroundName, Event event) throws NoModificationException;

    WriteResult addPlaygroundMessage(String playgroundName, Message message) throws NoModificationException;

    void removePedagoguePlaygroundAssociation(String playgroundName, String username) throws NoModificationException;

    void removeUserEventAssociation(String eventID, String username) throws NoModificationException;

    WriteResult removePlaygroundEvent(String eventID) throws NoModificationException;

    WriteResult removeMessagePlaygroundAssociation(String messageID) throws NoModificationException;
}