package database.dao;

import database.DALException;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;

import java.util.List;
import java.util.Set;

public interface IController {
    String ADMIN = "Admin";
    String PEDAGOGUE = "Pedagogue";
    String CLIENT = "Client";

    // PLAYGROUND, playgroundName must be unique!
    List<Playground> getAllPlaygrounds() throws DALException;
    void createPlayground(User activeUser, Playground playgroundToBeCreated) throws DALException;
    void assignPedagogueToPlayground(User activeUser, String playgroundName, String userID) throws DALException;
    void updatePedagogueInPlayground(User activeUser, String playgroundName, User updatedUser) throws DALException;
    void removePedagogueFromPlayground(User activeUser, String playgroundName, String userID) throws DALException;

    // PLAYGROUND EVENTS, eventDAO has to be associated with playground id
    Set<Event> getEventsInPlayground(String playgroundName) throws DALException;
    void addEventToPlayground(User activeUser, String playgroundName, Event eventToBeAdded) throws DALException;
    void updateEventInPlayground(User activeUser, String playgroundName, Event updatedEvent) throws DALException;
    void deleteEventInPlayground(User activeUser, String playgroundName, String eventID) throws DALException;
    void signupForEventInPlayground(String playgroundName, String eventID, String userID) throws DALException;

    // PLAYGROUND MESSAGES, messageDAO has to associated with playground id
    Set<Message> getPlaygroundMessage(String playgroundName) throws DALException;
    void createPlaygroundMessage(User activeUser, String playgroundName, Message message) throws DALException;
    void updatePlaygroundMessage(User activeUser, String playgroundName, Message updatedMessage) throws DALException;
    void deletePlaygroundMessage(User activeUser, String playgroundName, String messageID) throws DALException;

    // USER, username/email is unique, since we dont know id at all times
    User getUser(User activeUser, String userID) throws DALException;
    void createUser(User activeUser, User userToBeCreated) throws DALException;
    void updateUser(User activeUser, User updatedUser) throws DALException;
    void deleteUser(User activeUser, String userID) throws DALException;
}
