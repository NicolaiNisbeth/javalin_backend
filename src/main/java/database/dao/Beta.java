package database.dao;

import database.DALException;
import database.DataSource;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;
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

    private boolean updateWithPull(MongoCollection collection, String updateKey, String updateValue, String withKey, String withValue){
        return collection
                .update("{# : #}", updateKey, updateValue)
                .with("{$pull : {# : #}}", withKey, withValue)
                .wasAcknowledged();
    }

    private boolean updateWithPush(MongoCollection collection, String updateKey, String updateValue, String withKey, String withValue){
        return collection
                .update("{# : #}", updateKey, updateValue)
                .with("{$push : {# : #}}", withKey, withValue)
                .wasAcknowledged();
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
    public boolean addPlaygroundEvent(String playgroundName, Event event) {
        boolean eventIsAdded = false;

        try {
            // create event in event collection
            event.setPlayground(playgroundName);
            eventDAO.createEvent(event);

            // update playground array with reference to event ID
            MongoCollection playgrounds = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
            eventIsAdded = updateWithPush(playgrounds, "name", playgroundName, "events", event.getId());

        } catch (DALException e) {
            e.printStackTrace();
        }
        return eventIsAdded;
    }

    @Override
    public boolean addPlaygroundMessage(String playgroundName, Message message) {
        boolean messageIsAdded = false;

        try {
            // create message in message collection
            message.setPlaygroundID(playgroundName);
            messageDAO.createMessage(message);

            // update playground array with reference to message
            MongoCollection playgrounds = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
            messageIsAdded = updateWithPush(playgrounds, "name", playgroundName, "messages", message.getId());

        } catch (DALException e) {
            e.printStackTrace();
        }

        return messageIsAdded;
    }

    @Override
    public List<Playground> getPlaygrounds() {
        return null;
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
    public List<User> getUsers() {
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
            /*
            MongoCollection usersCollection = new Jongo(DataSource.getDB()).getCollection(IUserDAO.COLLECTION);
            MongoCursor<User> cursor = usersCollection.find("{playgroundsIDs : #}", playgroundName).as(User.class);
            for (User user : cursor){
                updateWithPull(usersCollection, "username", user.getUsername(), "playgroundsIDs", playgroundName);
            }
             */
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
            updateWithPull(playground, "name", message.getPlaygroundID(), "messages", messageID);

            // delete message
            isMessageDeleted = messageDAO.deleteMessage(messageID);

        } catch (DALException e) {
            e.printStackTrace();
        }

        return isMessageDeleted;
    }

    @Override
    public boolean addPedagogueToPlayground(String plagroundName, String username) {
        boolean isPedagogueAdded = false;

        try {
            // update user with playground reference
            User pedagogue = userDAO.getUser(username);
            pedagogue.getPlaygroundNames().add(plagroundName);
            userDAO.updateUser(pedagogue);

            // update playground with user reference
            MongoCollection playgrounds = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
            isPedagogueAdded = updateWithPush(playgrounds, "name", plagroundName, "assignedPedagogue", username);

        } catch (DALException e) {
            e.printStackTrace();
        }

        return isPedagogueAdded;
    }

    @Override
    public boolean addUserToPlaygroundEvent(String eventID, String username) {
        boolean isUserAdded = false;

        try {
            // update user with event reference
            User user = userDAO.getUser(username);
            user.getEvents().add(new Event.Builder(eventID).build());
            userDAO.updateUser(user);

            Jongo jongo = new Jongo(DataSource.getDB());
            MongoCollection evenets = jongo.getCollection(IEventDAO.COLLECTION);
            isUserAdded = updateWithPush(evenets, "id", eventID, "assignedUsers", username);
        } catch (DALException e) {
            e.printStackTrace();
        }

        return isUserAdded;
    }

    @Override
    public boolean removePedagogueFromPlayground(String playgroundName, String username) {
        boolean isPedagogueRemoved;
        MongoCollection playground = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
        isPedagogueRemoved = updateWithPull(playground, "name", playgroundName, "assignedPedagogue", username);
        return isPedagogueRemoved;
    }

    @Override
    public boolean removeUserFromPlaygroundEvent(String eventID, String username) {
        boolean isUserRemoved;
        MongoCollection events = new Jongo(DataSource.getDB()).getCollection(IEventDAO.COLLECTION);
        isUserRemoved = updateWithPull(events, "id", eventID, "assignedUsers", username);
        return isUserRemoved;
    }
}
