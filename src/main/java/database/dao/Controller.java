package database.dao;

import database.DALException;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;

import java.util.List;
import java.util.Set;

public class Controller implements IController{
    private PlaygroundDAO playgroundDAO;
    private UserDAO userDAO;
    private MessageDAO messageDAO;
    private EventDAO eventDAO;

    private static Controller controller;

    private Controller() {
        this.playgroundDAO = new PlaygroundDAO();
        this.userDAO = new UserDAO();
        this.messageDAO = new MessageDAO();
        this.eventDAO = new EventDAO();
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

    private boolean isUserValid(User activeUser, String... types){
        for (String type : types)
            if (!activeUser.getStatus().equalsIgnoreCase(type))
                return false;

        return true;
    }

    @Override
    public void createPlayground(User activeUser, Playground playgroundToBeCreated) throws DALException {
        if(isUserValid(activeUser, Controller.ADMIN))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.ADMIN));

        playgroundDAO.createPlayground(playgroundToBeCreated);
    }

    @Override
    public void assignPedagogueToPlayground(User activeUser, String playgroundName, String pedagogueID) throws DALException {
        if(isUserValid(activeUser, Controller.ADMIN))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.ADMIN));

        //TODO: are two calls necessary?
        Playground playground = playgroundDAO.getPlayground(playgroundName);
        User user = userDAO.getUser(pedagogueID);
        playground.getAssignedPedagogue().add(user);
    }

    @Override
    public void updatePedagogueInPlayground(User activeUser, String playgroundName, User updatedPedagogue) throws DALException {
        if(isUserValid(activeUser, Controller.ADMIN))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.ADMIN));

        Playground playground = playgroundDAO.getPlayground(playgroundName);
        playground.getAssignedPedagogue().remove(updatedPedagogue); // TODO: confirm that his works!
        playground.getAssignedPedagogue().add(updatedPedagogue);
    }

    @Override
    public void removePedagogueFromPlayground(User activeUser, String playgroundName, String pedagogueID) throws DALException {
        if(isUserValid(activeUser, Controller.ADMIN))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.ADMIN));

        Playground playground = playgroundDAO.getPlayground(playgroundName);
        User pedagogue = userDAO.getUser(pedagogueID);
        playground.getAssignedPedagogue().remove(pedagogue);
    }

    @Override
    public Set<Event> getEventsInPlayground(String playgroundName) throws DALException {
        return playgroundDAO.getPlayground(playgroundName).getEvents();
    }

    @Override
    public void addEventToPlayground(User activeUser, String playgroundName, Event eventToBeAdded) throws DALException {
        if(isUserValid(activeUser, Controller.PEDAGOGUE))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.PEDAGOGUE));

        Set<Event> events = playgroundDAO.getPlayground(playgroundName).getEvents();
        events.add(eventToBeAdded);
    }

    @Override
    public void updateEventInPlayground(User activeUser, String playgroundName, Event updatedEvent) throws DALException {
        if(isUserValid(activeUser, Controller.PEDAGOGUE))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.PEDAGOGUE));

        Set<Event> events = playgroundDAO.getPlayground(playgroundName).getEvents();
        events.remove(updatedEvent);
        events.add(updatedEvent);
    }

    @Override
    public void deleteEventInPlayground(User activeUser, String playgroundName, String eventID) throws DALException {
        if(isUserValid(activeUser, Controller.PEDAGOGUE))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.PEDAGOGUE));

        Set<Event> events = playgroundDAO.getPlayground(playgroundName).getEvents();
        Event eventToBeDeleted = eventDAO.getEvent(eventID);
        events.remove(eventToBeDeleted);
    }

    @Override
    public void signupForEventInPlayground(String playgroundName, String eventID, String userID) throws DALException {
        // TODO: does updates to event cascade to playground events?
        User user = userDAO.getUser(userID);
        Event event = eventDAO.getEvent(eventID);
        event.getAssignedUsers().add(user);
    }

    @Override
    public Set<Message> getPlaygroundMessage(String playgroundName) throws DALException {
        // maybe Date as 2nd param
        return playgroundDAO.getPlayground(playgroundName).getMessages();
    }

    @Override
    public void createPlaygroundMessage(User activeUser, String playgroundName, Message message) throws DALException {
        if(isUserValid(activeUser, Controller.PEDAGOGUE))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.PEDAGOGUE));

        // TODO: created message should cascade to playground
        //   should the playground message association logic be in the param or implemented here in logic?
        messageDAO.createMessage(message, playgroundName);
    }

    @Override
    public void updatePlaygroundMessage(User activeUser, String playgroundName, Message updatedMessage) throws DALException {
        if(isUserValid(activeUser, Controller.PEDAGOGUE))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.PEDAGOGUE));

        messageDAO.updateMessage(updatedMessage, playgroundName);
    }

    @Override
    public void deletePlaygroundMessage(User activeUser, String playgroundName, String messageID) throws DALException {
        messageDAO.deleteMessage(messageID, playgroundName);
    }

    @Override
    public User getUser(User activeUser, String userID) throws DALException {
        if(isUserValid(activeUser, Controller.ADMIN, Controller.PEDAGOGUE) || activeUser.getId().equals(userID)) {
            throw new DALException(String.format("User %s does not have the required privileges", activeUser.getId()));
        }

        //do we allow to fetch all user information like password etc?
        return userDAO.getUser(userID);
    }

    @Override
    public void createUser(User activeUser, User userToBeCreated) throws DALException {
        if(isUserValid(activeUser, Controller.ADMIN)) {
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.ADMIN));
        }

        userDAO.createUser(userToBeCreated);
    }

    @Override
    public void updateUser(User activeUser, User updatedUser) throws DALException {
        // do we allow updates on all fields? switch case ladder maybe?
        if(isUserValid(activeUser, Controller.ADMIN) || activeUser.getId().equals(updatedUser.getId())) {
            throw new DALException(String.format("User %s does not have the required privileges", activeUser.getId()));
        }




    }

    @Override
    public void deleteUser(User activeUser, String userID) throws DALException {

    }
}
