package database.dao;

import com.mongodb.WriteResult;
import com.mongodb.client.ClientSession;
import com.mongodb.client.TransactionBody;
import database.DALException;
import database.DataSource;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;
import database.utils.QueryUtils;
import javalin_resources.UserAdminResource;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Controller implements IController {
    private static Controller beta;
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

    public static Controller getInstance() {
        if (beta == null) {
            beta = new Controller();
        }
        return beta;
    }

    @Override
    public WriteResult createPlayground(Playground playground) {
        WriteResult writeResult = null;
        try {
            writeResult = playgroundDAO.createPlayground(playground);
        } catch (DALException e) {
            e.printStackTrace();
        }
        return writeResult;
    }

    @Override
    public WriteResult createUser(User user) {
        WriteResult writeResult = null;
        try {
            writeResult = userDAO.createUser(user);
        } catch (DALException e) {
            e.printStackTrace();
        }
        return writeResult;
    }

    @Override
    public Playground getPlayground(String playgroundName) {
        try {
            Playground playground = playgroundDAO.getPlayground(playgroundName);

            // fetch assigned pedagogues based on username
            Set<User> assignedPedagogue = playground.getAssignedPedagogue();
            Set<User> updatedPedagogue = new HashSet<>();
            if (!assignedPedagogue.isEmpty()) {
                for (User usernameObj : assignedPedagogue) {
                    User user = userDAO.getUser(usernameObj.getUsername());
                    updatedPedagogue.add(user);
                }
            }
            playground.setAssignedPedagogue(updatedPedagogue);

            // fetch events based on id
            Set<Event> events = playground.getEvents();
            Set<Event> updatedEvents = new HashSet<>();
            if (!events.isEmpty()) {
                for (Event idObj : events) {
                    Event event = eventDAO.getEvent(idObj.getId());
                    updatedEvents.add(event);
                }
            }
            playground.setEvents(updatedEvents);


            // fetch messages based on id
            Set<Message> messages = playground.getMessages();
            Set<Message> updatedMessage = new HashSet<>();
            if (!messages.isEmpty()) {
                for (Message idObj : messages) {
                    Message message = messageDAO.getMessage(idObj.getId());
                    updatedMessage.add(message);
                }
            }
            playground.setMessages(updatedMessage);
            return playground;

        } catch (DALException e) {
            e.printStackTrace();
        }
        return null;
    }


    // todo rettet af NJL
    @Override
    public User getUser(String username) throws DALException {
        User user = userDAO.getUser(username);

        // fetch all events based on id
        Set<Event> events = user.getEvents();
        Set<Event> updatedEvents = new HashSet<>();
        if (!events.isEmpty()) {
            for (Event value : events) {
                Event event = eventDAO.getEvent(value.getId());
                updatedEvents.add(event);
            }
        }
        user.setEvents(updatedEvents);
        return user;
    }

    @Override
    public Event getEvent(String eventID) {
        Event event = null;
        try {
            event = eventDAO.getEvent(eventID);

            // fetch all users based on id
            Set<User> users = event.getAssignedUsers();
            Set<User> updatedUser = new HashSet<>();
            if (!users.isEmpty()) {
                for (User user : users) {
                    User u = userDAO.getUser(user.getUsername());
                    updatedUser.add(u);
                }
            }
            event.setAssignedUsers(updatedUser);

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
        List<Playground> list = null;
        try {
            list = playgroundDAO.getPlaygroundList();
        } catch (DALException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<User> getUsers() {
        List<User> list = null;
        try {
            list = userDAO.getUserList();
        } catch (DALException e) {
            e.printStackTrace();
        }
        return list;
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
        //      final ClientSession session = DataSource.getClient().startSession();
        boolean isPlaygroundDeleted = false;
        //      session.startTransaction();
        try {
            Playground playground = playgroundDAO.getPlayground(playgroundName);

            // delete playground reference from pedagogues
            MongoCollection usersCollection = new Jongo(DataSource.getDB()).getCollection(IUserDAO.COLLECTION);
            for (User pedagogue : playground.getAssignedPedagogue())
                QueryUtils.updateWithPullSimple(usersCollection, "username", pedagogue.getUsername(), "playgroundsIDs", playgroundName);

            // delete playground events
            for (Event event : playground.getEvents())
                removePlaygroundEvent(event.getId());

            // delete playground messages
            for (Message message : playground.getMessages())
                removePlaygroundMessage(message.getId());

            // delete playground
            isPlaygroundDeleted = playgroundDAO.deletePlayground(playgroundName);

            //      session.commitTransaction();
        } catch (Exception e) {
            //       session.abortTransaction();
            e.printStackTrace();
        } finally {
            //       session.close();
        }

        return isPlaygroundDeleted;
    }

    @Override
    public boolean deleteUser(String username) {
        //  final ClientSession clientSession = DataSource.getClient().startSession();
        boolean isUserDeleted = false;
        //clientSession.startTransaction();
        try {
            User user = userDAO.getUser(username);

            // delete user reference from playground
            for (String playgroundName : user.getPlaygroundsIDs())
                removePedagogueFromPlayground(playgroundName, username);

            // delete user reference in events
            for (Event event : user.getEvents())
                removeUserFromPlaygroundEvent(event.getId(), username);

            // delete user
            isUserDeleted = userDAO.deleteUser(username);

            //  clientSession.commitTransaction();
        } catch (Exception e) {
            // clientSession.abortTransaction();
            e.printStackTrace();
        } finally {
            //clientSession.close();
        }

        return isUserDeleted;
    }

    // todo fix it
    @Override
    public boolean addPedagogueToPlayground(String plagroundName, String username) {
//        final ClientSession clientSession = DataSource.getClient().startSession();
        //  clientSession.startTransaction();
        try {
            // insert playground reference in user
            User pedagogue = userDAO.getUser(username);
            pedagogue.getPlaygroundsIDs().add(plagroundName);
            userDAO.updateUser(pedagogue);

            // insert user reference in playground
            MongoCollection playgrounds = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
            QueryUtils.updateWithPush(playgrounds, "name", plagroundName, "assignedPedagogue", pedagogue);

            //        clientSession.commitTransaction();
        } catch (Exception e) {
            //      clientSession.abortTransaction();
            e.printStackTrace();
        } finally {
            //    clientSession.close();
        }

        return true;
    }

    // to do fix it
    @Override
    public boolean addUserToPlaygroundEvent(String eventID, String username) {
//        ClientSession clientSession = DataSource.getClient().startSession();
        //       clientSession.startTransaction();
        try {
            // update user with event reference
            User user = userDAO.getUser(username);
            user.getEvents().add(new Event.Builder().id(new ObjectId(eventID).toString()).build());
            userDAO.updateUser(user);

            // insert user reference in event
            Jongo jongo = new Jongo(DataSource.getDB());
            MongoCollection events = jongo.getCollection(IEventDAO.COLLECTION);
            QueryUtils.updateWithPush(events, "_id", new ObjectId(eventID), "assignedUsers", user);

            //    clientSession.commitTransaction();
        } catch (Exception e) {
            //    clientSession.abortTransaction();
            e.printStackTrace();
        } finally {
            //clientSession.close();
        }

        return true;
    }

    @Override
    public WriteResult addPlaygroundEvent(String playgroundName, Event event) {
        //ClientSession clientSession = DataSource.getClient().startSession();
        WriteResult result = null;
        //clientSession.startTransaction();
        try {
            // create event in event collection
            event.setPlayground(playgroundName);
            result = eventDAO.createEvent(event);

            // insert event id in playground
            MongoCollection playgrounds = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
            QueryUtils.updateWithPush(playgrounds, "name", playgroundName, "events", event);

            //    clientSession.commitTransaction();
        } catch (Exception e) {
            //  clientSession.abortTransaction();
            e.printStackTrace();
        } finally {
            //  clientSession.close();
        }

        return result;
    }

    @Override
    public WriteResult addPlaygroundMessage(String playgroundName, Message message) {
        //ClientSession clientSession = DataSource.getClient().startSession();
        WriteResult result = null;
        //clientSession.startTransaction();
        try {
            // create message in message collection
            message.setPlaygroundID(playgroundName);
            result = messageDAO.createMessage(message);

            // update playground array with reference to message
            MongoCollection playgrounds = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
            QueryUtils.updateWithPush(playgrounds, "name", playgroundName, "messages", message);

            //   clientSession.commitTransaction();
        } catch (Exception e) {
            //  clientSession.abortTransaction();
            e.printStackTrace();
        } finally {
            //  clientSession.close();
        }

        return result;
    }

    @Override
    public boolean removePedagogueFromPlayground(String playgroundName, String username) {
        MongoCollection playground = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
        try {
            QueryUtils.updateWithPullObject(playground, "name", playgroundName, "assignedPedagogue", "username", username);
        } catch (DALException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean removeUserFromPlaygroundEvent(String eventID, String username) {
        MongoCollection events = new Jongo(DataSource.getDB()).getCollection(IEventDAO.COLLECTION);
        try {
            QueryUtils.updateWithPullObject(events, "_id", new ObjectId(eventID), "assignedUsers", "username", username);
        } catch (DALException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean removePlaygroundEvent(String eventID) {
        //ClientSession clientSession = DataSource.getClient().startSession();
        boolean isEventDeleted = false;
        //clientSession.startTransaction();
        try {
            Event event = eventDAO.getEvent(eventID);

            // delete event reference in users
            MongoCollection users = new Jongo(DataSource.getDB()).getCollection(IUserDAO.COLLECTION);
            for (User user : event.getAssignedUsers()) {
                QueryUtils.updateWithPullObject(users, "username", user.getUsername(), "events", "_id", new ObjectId(eventID));
            }

            // delete event reference in playground
            MongoCollection playgrounds = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
            QueryUtils.updateWithPullObject(playgrounds, "name", event.getPlaygroundName(), "events", "_id", new ObjectId(eventID));

            // delete event
            isEventDeleted = eventDAO.deleteEvent(eventID);

            //clientSession.commitTransaction();
        } catch (Exception e) {
            //clientSession.abortTransaction();
            e.printStackTrace();
        } finally {
            // clientSession.close();
        }

        return isEventDeleted;
    }

    @Override
    public boolean removePlaygroundMessage(String messageID) {
        //ClientSession clientSession = DataSource.getClient().startSession();
        boolean isMessageDeleted = false;
        //clientSession.startTransaction();
        try {
            Message message = messageDAO.getMessage(messageID);

            // delete message reference in playground
            MongoCollection playground = new Jongo(DataSource.getDB()).getCollection(IPlaygroundDAO.COLLECTION);
            QueryUtils.updateWithPullObject(playground, "name", message.getPlaygroundName(), "messages", "_id", new ObjectId(messageID));

            // delete message
            isMessageDeleted = messageDAO.deleteMessage(messageID);

            //clientSession.commitTransaction();
        } catch (Exception e) {
            //clientSession.abortTransaction();
            e.printStackTrace();
        } finally {
            //clientSession.close();
        }

        return isMessageDeleted;
    }
}
