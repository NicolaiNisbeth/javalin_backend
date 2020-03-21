package database.dao;

import database.DALException;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;

import java.util.List;

public interface IController {

    // PLAYGROUND, playgroundName must be unique!
    List<Playground> getAllPlaygrounds() throws DALException;
    void createPlayground(User activeUser, Playground playgroundToBeCreated) throws DALException;
    void assignUserToPlayground(User activeUser, String playgroundName, String userID) throws DALException;
    void updateUserInPlayground(User activeUser, String playgroundName, User updatedUser) throws DALException;
    void removeAssignedUserFromPlayground(User activeUser, String playgroundName, String userID) throws DALException;

    // PLAYGROUND EVENTS
    List<Event> getEventsInPlayground(String playgroundName) throws DALException;
    void addEventToPlayground(User activeUser, String playgroundName, Event eventToBeAdded) throws DALException;
    void updateEventInPlayground(User activeUser, String playgroundName, Event updatedEvent) throws DALException;
    void deleteEventInPlayground(User activeUser, String playgroundName, String eventID) throws DALException;
    void signupForEventInPlayground(String playgroundName, String userID) throws DALException;

    // PLAYGROUND MESSAGES
    List<Message> getPlaygroundMessage(String playgroundName) throws DALException;
    void createPlaygroundMessage(User activeUser, String playgroundName) throws DALException;
    void updatePlaygroundMessage(User activeUser, String playgroundName) throws DALException;
    void deletePlaygroundMessage(User activeUser, String playgroundName) throws DALException;

    // USER, username/email is unique, since we dont know id at all times
    User getUser(User activeUser, String userID) throws DALException;
    void createUser(User activeUser, User userToBeCreated) throws DALException;
    void updateUser(User activeUser, User updatedUser) throws DALException;
    void deleteUser(User activeUser, String userID) throws DALException;
}
