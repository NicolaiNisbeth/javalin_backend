package database.dao;

import database.DALException;
import database.DataSource;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;
import org.jongo.Jongo;
import org.jongo.MongoCollection;

import java.util.List;
import java.util.Set;

import static org.jongo.Oid.withOid;

public class Controller implements IController{
    private static Controller controller;
    private PlaygroundDAO playgroundDAO;
    private UserDAO userDAO;
    private MessageDAO messageDAO;
    private EventDAO eventDAO;

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

    @Override
    public void createPlayground(User activeUser, Playground playgroundToBeCreated) throws DALException {
        if(!hasStatus(activeUser, Controller.ADMIN))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.ADMIN));

        try {
            playgroundDAO.createPlayground(playgroundToBeCreated);
        } catch (DALException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws DALException {
        Controller controller = new Controller();

        PlaygroundDAO playgroundDAO = new PlaygroundDAO();
        Playground playground = new Playground.Builder("testlegepladsen").build();
        playgroundDAO.createPlayground(playground);


        User user = new User.Builder("nicolai").status("admin").build();
        UserDAO userDAO = new UserDAO();
        userDAO.createUser(user);

        controller.addPedagogueToPlayground(user, playground.getName(), user.getId());

        List<Playground> playgrounds = controller.getAllPlaygrounds();
        System.out.println("hej");
        User asd = userDAO.getUser(user.getId());
    }

    @Override
    public boolean addPedagogueToPlayground(User activeUser, String playgroundName, String pedagogueID) throws DALException {
        if(!hasStatus(activeUser, Controller.ADMIN))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.ADMIN));

        boolean isUserUpdated = false, isPedagogueAdded = false;
        try {
            User user = userDAO.getUser(pedagogueID);
            user.setPlaygroundID(playgroundName);
            isUserUpdated = userDAO.updateUser(user);

            Jongo jongo = new Jongo(DataSource.getDB()); // TODO: do we create new connection or reuse?
            MongoCollection playgrounds = jongo.getCollection(IPlaygroundDAO.COLLECTION);
            isPedagogueAdded = playgrounds
                    .update("{name : #}", playgroundName)
                    .with("{$push: {assignedPedagogue : #}}", user)
                    .wasAcknowledged();

        } catch (DALException e){
            e.printStackTrace();
            // TODO: how do we handle if user is updated successfully but playground is not?
        }

        return isUserUpdated && isPedagogueAdded;
    }

    @Override
    public boolean removePedagogueFromPlayground(User activeUser, String playgroundName, String pedagogueID) throws DALException {
        if(!hasStatus(activeUser, Controller.ADMIN))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.ADMIN));

        boolean isUserUpdated = false, isPedagogueRemoved = false;
        try {
            User user = userDAO.getUser(pedagogueID);
            user.setPlaygroundID(null);
            isUserUpdated = userDAO.updateUser(user);

            Jongo jongo = new Jongo(DataSource.getDB());
            MongoCollection playgrounds = jongo.getCollection(IPlaygroundDAO.COLLECTION);
            isPedagogueRemoved = playgrounds
                    .update("{name : #}", playgroundName)
                    .with("{$pull: {assignedUsers : {_id : #}}}", withOid(pedagogueID))
                    .wasAcknowledged();

        } catch (DALException e){
            e.printStackTrace();
        }

        return isUserUpdated && isPedagogueRemoved;
    }

    @Override
    public Set<Event> getEventsInPlayground(String playgroundName) throws DALException {
        return playgroundDAO.getPlayground(playgroundName).getEvents();
    }

    @Override
    public void addEventToPlayground(User activeUser, String playgroundName, Event eventToBeAdded) throws DALException {
        if(!hasStatus(activeUser, Controller.PEDAGOGUE))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.PEDAGOGUE));

        //eventDAO.createEvent(eventToBeAdded, playgroundName);
        // TODO: make mongo query to add event to playground
    }

    @Override
    public void updateEvent(User activeUser, String playgroundName, Event updatedEvent) throws DALException {
        if(!hasStatus(activeUser, Controller.PEDAGOGUE))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.PEDAGOGUE));

        //eventDAO.updateEvent();
    }

    @Override
    public void deleteEventInPlayground(User activeUser, String playgroundName, String eventID) throws DALException {
        if(!hasStatus(activeUser, Controller.PEDAGOGUE))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.PEDAGOGUE));

        eventDAO.deleteEvent(eventID);
    }

    @Override
    public void signupForEventInPlayground(String playgroundName, String eventID, String userID) throws DALException {
        User user = userDAO.getUser(userID);
        Event event = eventDAO.getEvent(eventID);

        // TODO: make mongo query to add user to Event;

    }

    @Override
    public Set<Message> getPlaygroundMessage(String playgroundName) throws DALException {
        // maybe Date as 2nd param
        return playgroundDAO.getPlayground(playgroundName).getMessages();
    }

    @Override
    public void createPlaygroundMessage(User activeUser, String playgroundName, Message message) throws DALException {
        if(!hasStatus(activeUser, Controller.PEDAGOGUE))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.PEDAGOGUE));

        // TODO: created message should cascade to playground
        //   should the playground message association logic be in the param or implemented here in logic?
        //messageDAO.createMessage(message, playgroundName);
    }

    @Override
    public void updatePlaygroundMessage(User activeUser, String playgroundName, Message updatedMessage) throws DALException {
        if(!hasStatus(activeUser, Controller.PEDAGOGUE))
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.PEDAGOGUE));

        //messageDAO.updateMessage(updatedMessage, playgroundName);
    }

    @Override
    public void deletePlaygroundMessage(User activeUser, String playgroundName, String messageID) throws DALException {
        //messageDAO.deleteMessage(messageID, playgroundName);
    }

    @Override
    public User getUser(User activeUser, String userID) throws DALException {
        if(!hasStatus(activeUser, Controller.ADMIN, Controller.PEDAGOGUE) || activeUser.getId().equals(userID)) {
            throw new DALException(String.format("User %s does not have the required privileges", activeUser.getId()));
        }

        //do we allow to fetch all user information like password etc?
        return userDAO.getUser(userID);
    }

    @Override
    public void createUser(User activeUser, User userToBeCreated) throws DALException {
        if(!hasStatus(activeUser, Controller.ADMIN)) {
            throw new DALException(String.format("User %s does not have %s privileges", activeUser.getId(), Controller.ADMIN));
        }

        userDAO.createUser(userToBeCreated);
    }

    @Override
    public void updateUser(User activeUser, User updatedUser) throws DALException {
        // do we allow updates on all fields? switch case ladder maybe?
        if(!hasStatus(activeUser, Controller.ADMIN) || activeUser.getId().equals(updatedUser.getId())) {
            throw new DALException(String.format("User %s does not have the required privileges", activeUser.getId()));
        }

        userDAO.updateUser(updatedUser);
    }

    @Override
    public void deleteUser(User activeUser, String userID) throws DALException {
        if(!hasStatus(activeUser, Controller.ADMIN)) {
            throw new DALException(String.format("User %s does not have the required privileges", activeUser.getId()));
        }

        userDAO.deleteUser(userID);
    }

    private boolean hasStatus(User activeUser, String... types){
        for (String type : types)
            if (activeUser.getStatus().equalsIgnoreCase(type))
                return true;

        return false;
    }
}
