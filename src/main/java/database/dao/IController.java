package database.dao;

import database.DALException;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;
import io.javalin.apibuilder.CrudHandler;

import javax.naming.Context;
import java.util.List;
import java.util.Set;

public interface IController {
    String ADMIN = "admin";
    String PEDAGOGUE = "pedagogue";
    String CLIENT = "client";

    // PLAYGROUND, playgroundName must be unique!
    List<Playground> getAllPlaygrounds() throws DALException;
    void createPlayground(User activeUser, Playground playgroundToBeCreated) throws DALException;
    boolean addPedagogueToPlayground(User activeUser, String playgroundName, String userID) throws DALException;
    boolean removePedagogueFromPlayground(User activeUser, String playgroundName, String userID) throws DALException;

    // PLAYGROUND EVENTS, eventDAO has to be associated with playground id
    Set<Event> getEventsInPlayground(String playgroundName) throws DALException;
    boolean addEventToPlayground(User activeUser, String playgroundName, Event eventToBeAdded) throws DALException;
    boolean updateEvent(User activeUser, String playgroundName, Event updatedEvent) throws DALException;
    boolean deleteEventInPlayground(User activeUser, String playgroundName, String eventID) throws DALException;
    boolean signupForEventInPlayground(String playgroundName, String eventID, String userID) throws DALException;

    // PLAYGROUND MESSAGES, messageDAO has to associated with playground id
    Set<Message> getPlaygroundMessage(String playgroundName) throws DALException;
    boolean createPlaygroundMessage(User activeUser, String playgroundName, Message message) throws DALException;
    boolean updatePlaygroundMessage(User activeUser, String playgroundName, Message updatedMessage) throws DALException;
    boolean deletePlaygroundMessage(User activeUser, String playgroundName, String messageID) throws DALException;

    // USER, username/email is unique, since we dont know id at all times
    User getUser(String userID) throws DALException;
    void createUser(User userToBeCreated) throws DALException;
    void updateUser(User updatedUser) throws DALException;
    void deleteUser(String userID) throws DALException;
}
