package database.dao;

import database.DALException;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;

import java.util.List;

public class Controller implements IController{
    private PlaygroundDAO playgroundDAO;

    private static Controller controller;

    private Controller() {
        this.playgroundDAO = new PlaygroundDAO();
    }

    public static Controller getController() {
        if (controller == null) {
            controller = new Controller();
        }
        return controller;
    }

    public List<Playground> getAllPlaygrounds() {
        List<Playground> playgrounds = null;
        try {
            playgrounds = playgroundDAO.getPlaygroundList();
        } catch (DALException e) {
            e.printStackTrace();
        }
        return playgrounds;
    }

    @Override
    public void createPlayground(User activeUser, Playground playgroundToBeCreated) throws DALException {

    }

    @Override
    public void assignUserToPlayground(User activeUser, String playgroundName, String userID) throws DALException {

    }

    @Override
    public void updateUserInPlayground(User activeUser, String playgroundName, User updatedUser) throws DALException {

    }

    @Override
    public void removeAssignedUserFromPlayground(User activeUser, String playgroundName, String userID) throws DALException {

    }

    @Override
    public List<Event> getEventsInPlayground(String playgroundName) throws DALException {
        return null;
    }

    @Override
    public void addEventToPlayground(User activeUser, String playgroundName, Event eventToBeAdded) throws DALException {

    }

    @Override
    public void updateEventInPlayground(User activeUser, String playgroundName, Event updatedEvent) throws DALException {

    }

    @Override
    public void deleteEventInPlayground(User activeUser, String playgroundName, String eventID) throws DALException {

    }

    @Override
    public void signupForEventInPlayground(String playgroundName, String userID) throws DALException {

    }

    @Override
    public List<Message> getPlaygroundMessage(String playgroundName) throws DALException {
        // maybe Date as 2nd param
        return null;
    }

    @Override
    public void createPlaygroundMessage(User activeUser, String playgroundName) throws DALException {

    }

    @Override
    public void updatePlaygroundMessage(User activeUser, String playgroundName) throws DALException {

    }

    @Override
    public void deletePlaygroundMessage(User activeUser, String playgroundName) throws DALException {

    }

    @Override
    public User getUser(User activeUser, String userID) throws DALException {
        //do we allow to fetch all user information like password etc?
        return null;
    }

    @Override
    public void createUser(User activeUser, User userToBeCreated) throws DALException {

    }

    @Override
    public void updateUser(User activeUser, User updatedUser) throws DALException {
        // do we allow updates on all fields? switch case ladder maybe?
    }

    @Override
    public void deleteUser(User activeUser, String userID) throws DALException {

    }
}
