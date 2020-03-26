package database.dao;

import com.mongodb.WriteResult;
import database.DALException;
import database.DataSource;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Beta  implements IBeta{
    private static Beta beta;
    private PlaygroundDAO playgroundDAO;
    private UserDAO userDAO;
    private MessageDAO messageDAO;
    private EventDAO eventDAO;

    private Beta() {
        this.playgroundDAO = new PlaygroundDAO();
        this.userDAO = new UserDAO();
        this.messageDAO = new MessageDAO();
        this.eventDAO = new EventDAO();
    }

    public static Beta getInstance() {
        if (beta == null) {
            beta = new Beta();
        }
        return beta;
    }

    private void updateWithPull(MongoCollection collection, String updateKey, String updateValue, String withKey, String withValue) throws DALException {
        WriteResult ws = collection
                .update("{# : #}", updateKey, updateValue)
                .with("{$pull : {# : #}}", withKey, withValue);

        if (ws.getN() == 0 || !ws.isUpdateOfExisting())
            throw new DALException(String.format("%s in %s was not updated with pull: %s", withKey, collection.getName(), withValue));
    }

    private void updateWithPush(MongoCollection collection, String updateKey, Object updateValue, String withKey, Object withValue) throws DALException {
        WriteResult ws = collection
                .update("{# : #}", updateKey, updateValue)
                .with("{$push : {# : #}}", withKey, withValue);

        if (ws.getN() == 0 ||!ws.isUpdateOfExisting())
            throw new DALException(String.format("%s in %s was not updated with push: %s", withKey, collection.getName(), withValue));

    }


    @Override
    public boolean createPlayground(Playground playground) {
        try {
            playgroundDAO.createPlayground(playground);
        } catch (DALException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean createUser(User user) {
        try {
            userDAO.createUser(user);
        } catch (DALException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public Playground getPlayground(String playgroundName) {

        try {
            Playground playground = playgroundDAO.getPlayground(playgroundName);

            // fetch assigned pedagogues based on username
            Set<User> assignedPedagogue = playground.getAssignedPedagogue();
            if (!assignedPedagogue.isEmpty()){
                for (User usernameObj : assignedPedagogue){
                    User user = userDAO.getUser(usernameObj.getUsername());
                    playground.getAssignedPedagogue().add(user);
                }
            }

            // fetch events based on id
            Set<Event> events = playground.getEvents();
            if (!events.isEmpty()){
                for (Event idObj : events){
                    Event event = eventDAO.getEvent(idObj.getId());
                    playground.getEvents().add(event);
                }
            }

            // fetch messages based on id
            Set<Message> messages = playground.getMessages();
            if (!messages.isEmpty()){
                for (Message idObj : messages){
                    Message message = messageDAO.getMessage(idObj.getId());
                    playground.getMessages().add(message);
                }
            }

            return playground;
        } catch (DALException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public User getUser(String username) {

        try {
            User user = userDAO.getUser(username);

            // fetch all events based on id
            Set<Event> events = user.getEvents();
            if (!events.isEmpty()){
                for (Event idObj : events){
                    Event event = eventDAO.getEvent(idObj.getId());
                    user.getEvents().add(event);
                }
            }

            return user;
        } catch (DALException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Event getEvent(String eventID) {
        Event event = null;

        try {
            event = eventDAO.getEvent(eventID);
            // TODO: maybe fetch assigned users
        } catch (DALException e) {
            e.printStackTrace();
        }

        return event;
    }

    @Override
    public Message getMessage(String messageID) {
        Message message = null;
        try {
            message = messageDAO.getMessage(messageID);
        } catch (DALException e) {
            e.printStackTrace();
        }
        return message;
    }

    @Override
    public List<Playground> getPlaygrounds() {
        return null;
    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public List<Event> getPlaygroundEvents(String playgroundName) {

        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection events = jongo.getCollection(IEventDAO.COLLECTION);
        MongoCursor<Event> cursor = events.find("{playground : #}", playgroundName).as(Event.class);
        List<Event> eventList = new ArrayList<>();
        for (Event event : cursor)
            eventList.add(event);

        return eventList;
    }

    @Override
    public List<Message> getPlaygroundMessages(String playgroundName) {

        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCollection messages = jongo.getCollection(IMessageDAO.COLLECTION);
        MongoCursor<Message> cursor = messages.find("{playgroundID : #}", playgroundName).as(Message.class);
        List<Message> messageList = new ArrayList<>();
        for (Message message : cursor)
            messageList.add(message);

        return messageList;
    }

    @Override
    public boolean updatePlayground(Playground playground) {
        boolean isUpdated = false;
        try {
            isUpdated = playgroundDAO.updatePlayground(playground);
        } catch (DALException e) {
            e.printStackTrace();
        }

        return isUpdated;
    }

    @Override
    public boolean updateUser(User user) {
        boolean isUpdated = false;
        try {
            isUpdated = userDAO.updateUser(user);
        } catch (DALException e) {
            e.printStackTrace();
        }
        return isUpdated;
    }

    @Override
    public boolean updatePlaygroundEvent(Event event) {
        boolean isUpdated = false;
        try {
            isUpdated = eventDAO.updateEvent(event);
        } catch (DALException e) {
            e.printStackTrace();
        }
        return isUpdated;
    }

    @Override
    public boolean updatePlaygroundMessage(Message message) {
        boolean isUpdated = false;
        try {
            isUpdated = messageDAO.updateMessage(message);
        } catch (DALException e) {
            e.printStackTrace();
        }
        return isUpdated;
    }

    @Override
    public boolean deletePlayground(String playgroundName) {
        boolean isPlaygroundDeleted = false;

        try {
            Playground playground = playgroundDAO.getPlayground(playgroundName);

            // delete playground reference from pedagogues
            MongoCollection usersCollection = new Jongo(DataSource.getDB()).getCollection(IUserDAO.COLLECTION);
            for (User pedagogue : playground.getAssignedPedagogue()){
                updateWithPull(usersCollection, "username", pedagogue.getUsername(), "playgroundsIDs", playgroundName);
            }

            // delete playground events
            for (Event event : playground.getEvents())
                removePlaygroundEvent(event.getId());

            // delete playground messages
            for (Message message : playground.getMessages())
                removePlaygroundMessage(message.getId());

            // delete playground
            isPlaygroundDeleted = playgroundDAO.deletePlayground(playgroundName);

        } catch (DALException e) {
            e.printStackTrace();
        }

        return isPlaygroundDeleted;
    }

    @Override
    public boolean deleteUser(String username) {
        boolean isUserDeleted = false;

        try {
            User user = userDAO.getUser(username);

            // delete user reference from playground
            for (String playgroundName : user.getPlaygroundNames()){
                removePedagogueFromPlayground(playgroundName, username);
            }

            // delete user reference in events
            for (Event event : user.getEvents()){
                removeUserFromPlaygroundEvent(event.getId(), username);
            }

            // delete user
            isUserDeleted = userDAO.deleteUser(username);

        } catch (DALException e) {
            e.printStackTrace();
        }

        return isUserDeleted;
    }

    @Override
    public boolean addPedagogueToPlayground(String plagroundName, String username) {

        try {
            // insert playground reference in user
            User pedagogue = userDAO.getUser(username);
            pedagogue.getPlaygroundNames().add(plagroundName);
            userDAO.updateUser(pedagogue);

            // insert user reference in playground
            MongoCollection playgrounds = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
            updateWithPush(playgrounds, "name", plagroundName, "assignedPedagogue", pedagogue);

        } catch (DALException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean addUserToPlaygroundEvent(String eventID, String username) {

        try {
            // update user with event reference
            User user = userDAO.getUser(username);
            user.getEvents().add(new Event.Builder().id(new ObjectId(eventID).toString()).build());
            userDAO.updateUser(user);

            Jongo jongo = new Jongo(DataSource.getDB());
            MongoCollection events = jongo.getCollection(IEventDAO.COLLECTION);
            updateWithPush(events, "_id", new ObjectId(eventID), "assignedUsers", user);
        } catch (DALException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public WriteResult addPlaygroundEvent(String playgroundName, Event event) {
        WriteResult result = null;

        try {
            // create event in event collection
            event.setPlayground(playgroundName);
            result = eventDAO.createEvent(event);

            // insert event id in playground
            MongoCollection playgrounds = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
            updateWithPush(playgrounds, "name", playgroundName, "events", event);


        } catch (DALException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public boolean addPlaygroundMessage(String playgroundName, Message message) {

        try {
            // create message in message collection
            message.setPlaygroundID(playgroundName);
            messageDAO.createMessage(message);

            // update playground array with reference to message
            MongoCollection playgrounds = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
            updateWithPush(playgrounds, "name", playgroundName, "messages", message);

        } catch (DALException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean removePedagogueFromPlayground(String playgroundName, String username) {
        MongoCollection playground = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
        try {
            updateWithPull(playground, "name", playgroundName, "assignedPedagogue", username);
        } catch (DALException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean removeUserFromPlaygroundEvent(String eventID, String username) {
        MongoCollection events = new Jongo(DataSource.getDB()).getCollection(IEventDAO.COLLECTION);
        try {
            updateWithPull(events, "id", eventID, "assignedUsers", username);
        } catch (DALException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean removePlaygroundEvent(String eventID) {
        boolean isEventDeleted = false;

        try {
            Event event = eventDAO.getEvent(eventID);

            // delete event reference in users
            MongoCollection users = new Jongo(DataSource.getDB()).getCollection(IUserDAO.COLLECTION);
            for (User user : event.getAssignedUsers()){
                updateWithPull(users, "username", user.getUsername(), "events", eventID);
            }

            // delete event reference in playground
            MongoCollection playgrounds = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
            updateWithPull(playgrounds, "name", event.getPlayground(), "events", eventID);

            // delete event
            isEventDeleted = eventDAO.deleteEvent(eventID);

        } catch (DALException e) {
            e.printStackTrace();
        }


        return isEventDeleted;
    }

    @Override
    public boolean removePlaygroundMessage(String messageID) {
        boolean isMessageDeleted = false;

        try {
            Message message = messageDAO.getMessage(messageID);

            // delete message reference in playground
            MongoCollection playground = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
            updateWithPull(playground, "name", message.getPlaygroundName(), "messages", messageID);

            // delete message
            isMessageDeleted = messageDAO.deleteMessage(messageID);

        } catch (DALException e) {
            e.printStackTrace();
        }

        return isMessageDeleted;
    }
}
