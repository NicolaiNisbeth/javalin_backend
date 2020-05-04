package database;

import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.mongodb.client.ClientSession;
import database.dao.EventDAO;
import database.dao.IEventDAO;
import database.dao.IMessageDAO;
import database.dao.IPlaygroundDAO;
import database.dao.IUserDAO;
import database.dao.MessageDAO;
import database.dao.PlaygroundDAO;
import database.dao.UserDAO;
import database.exceptions.NoModificationException;
import database.dto.EventDTO;
import database.dto.MessageDTO;
import database.dto.PlaygroundDTO;
import database.dto.UserDTO;
import database.utils.QueryUtils;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;

public class Controller implements IController {
    private static IController controller;
    private IDataSource datasource;
    private IPlaygroundDAO playgroundDAO;
    private IUserDAO userDAO;
    private IMessageDAO messageDAO;
    private IEventDAO eventDAO;

    private Controller() {
        this.datasource = ProductionDB.getInstance(); // production database by default
       // this.datasource = TestDB.getInstance(); // TODO: Use above line instead when ProductionDB supports transaction
        this.playgroundDAO = new PlaygroundDAO(datasource);
        this.userDAO = new UserDAO(datasource);
        this.messageDAO = new MessageDAO(datasource);
        this.eventDAO = new EventDAO(datasource);
    }

    public static IController getInstance() {
        if (controller == null) {
            controller = new Controller();
        }
        return controller;
    }

    @Override
    public WriteResult createPlayground(PlaygroundDTO playground) throws IllegalArgumentException, NoModificationException {
        return playgroundDAO.createPlayground(playground);
    }

    @Override
    public WriteResult createUser(UserDTO user) throws IllegalArgumentException, NoModificationException {
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        return userDAO.createUser(user);
    }

    @Override
    public PlaygroundDTO getPlayground(String playgroundName) throws IllegalArgumentException, NoSuchElementException {
        PlaygroundDTO playground = playgroundDAO.getPlayground(playgroundName);

        // fetch assigned pedagogues based on username
        Set<UserDTO> updatedPedagogue = new HashSet<>();
        Set<UserDTO> assignedPedagogue = playground.getAssignedPedagogue();
        if (!assignedPedagogue.isEmpty()) {
            for (UserDTO usernameObj : assignedPedagogue) {
                UserDTO user = userDAO.getUser(usernameObj.getUsername());
                updatedPedagogue.add(user);
            }
        }
        playground.setAssignedPedagogue(updatedPedagogue);

        // fetch events based on id
        Set<EventDTO> updatedEvents = new HashSet<>();
        Set<EventDTO> events = playground.getEvents();
        if (!events.isEmpty()) {
            for (EventDTO idObj : events) {
                EventDTO event = eventDAO.getEvent(idObj.getId());
                updatedEvents.add(event);
            }
        }
        playground.setEvents(updatedEvents);


        // fetch messages based on id
        Set<MessageDTO> messages = playground.getMessages();
        Set<MessageDTO> updatedMessage = new HashSet<>();
        if (!messages.isEmpty()) {
            for (MessageDTO idObj : messages) {
                MessageDTO message = messageDAO.getMessage(idObj.getId());
                updatedMessage.add(message);
            }
        }
        playground.setMessages(updatedMessage);
        return playground;
    }

    @Override
    public UserDTO getUser(String username) throws NoSuchElementException, IllegalArgumentException {
        UserDTO user = userDAO.getUser(username);

        // fetch all events based on id
        Set<EventDTO> updatedEvents = new HashSet<>();
        Set<EventDTO> events = user.getEvents();
        if (!events.isEmpty()) {
            for (EventDTO value : events) {
                EventDTO event = eventDAO.getEvent(value.getId());
                event.setAssignedUsers(null);
                updatedEvents.add(event);
            }
        }
        user.setEvents(updatedEvents);
        return user;
    }

    @Override
    public EventDTO getEvent(String eventID) throws IllegalArgumentException, NoSuchElementException {
        EventDTO event = eventDAO.getEvent(eventID);

        // fetch all users based on id
        Set<UserDTO> updatedUser = new HashSet<>();
        Set<UserDTO> users = event.getAssignedUsers();
        if (!users.isEmpty()) {
            for (UserDTO user : users) {
                UserDTO u = userDAO.getUser(user.getUsername());
                updatedUser.add(u);
            }
        }
        event.setAssignedUsers(updatedUser);


        return event;
    }

    @Override
    public MessageDTO getMessage(String messageID) throws IllegalArgumentException, NoSuchElementException {
        return messageDAO.getMessage(messageID);
    }

    @Override
    public List<PlaygroundDTO> getPlaygrounds() throws NoSuchElementException {
        List<PlaygroundDTO> playgroundDTOS = playgroundDAO.getPlaygroundList();
        for (PlaygroundDTO playground : playgroundDTOS){
            for (UserDTO pedagogue : playground.getAssignedPedagogue()){
                pedagogue.setEvents(null);
            }
            for (EventDTO eventDTO : playground.getEvents()){
                for (UserDTO pedagogue : eventDTO.getAssignedUsers()){
                    pedagogue.setEvents(null);
                }
            }
        }
        return playgroundDTOS;
    }

    @Override
    public List<UserDTO> getUsers() throws NoSuchElementException {
        return userDAO.getUserList();
    }

    @Override
    public List<EventDTO> getEventsInPlayground(String playgroundName) {
        Jongo jongo = new Jongo(datasource.getDatabase());
        MongoCollection events = jongo.getCollection(IEventDAO.COLLECTION);
        MongoCursor<EventDTO> cursor = events.find("{playground : #}", playgroundName).as(EventDTO.class);
        List<EventDTO> eventList = new ArrayList<>();
        for (EventDTO event : cursor)
            eventList.add(event);

        return eventList;
    }

    @Override
    public List<MessageDTO> getMessagesInPlayground(String playgroundName) {
        Jongo jongo = new Jongo(datasource.getDatabase());
        MongoCollection messages = jongo.getCollection(IMessageDAO.COLLECTION);
        MongoCursor<MessageDTO> cursor = messages.find("{playgroundID : #}", playgroundName).as(MessageDTO.class);
        List<MessageDTO> messageList = new ArrayList<>();
        for (MessageDTO message : cursor)
            messageList.add(message);

        return messageList;
    }

    @Override
    public WriteResult updatePlayground(PlaygroundDTO playground)
            throws IllegalArgumentException, NoModificationException {
        return playgroundDAO.updatePlayground(playground);
    }

    @Override
    public WriteResult updateUser(UserDTO user) throws IllegalArgumentException, NoModificationException {
        return userDAO.updateUser(user);
    }

    @Override
    public WriteResult updatePlaygroundEvent(EventDTO event)
            throws IllegalArgumentException, NoModificationException {
        return eventDAO.updateEvent(event);
    }

    @Override
    public WriteResult updatePlaygroundMessage(MessageDTO message)
            throws IllegalArgumentException, NoModificationException {
        return messageDAO.updateMessage(message);
    }

    @Override
    public WriteResult deletePlayground(String playgroundName)
            throws NoSuchElementException, NoModificationException, MongoException {

        WriteResult wr;
        final ClientSession session = datasource.getClient().startSession();
        try (session)   {
            session.startTransaction();
            PlaygroundDTO playground = playgroundDAO.getPlayground(playgroundName);

            // delete playground reference from pedagogues
            for (UserDTO pedagogue : playground.getAssignedPedagogue()){
                // remove user reference in playground
                MongoCollection collection = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
                QueryUtils.updateWithPullObject(collection, "name", playgroundName, "assignedPedagogue", "username", pedagogue.getUsername());

                // remove playground reference in user
                MongoCollection user = new Jongo(datasource.getDatabase()).getCollection(IUserDAO.COLLECTION);
                QueryUtils.updateWithPullSimple(user, "username", pedagogue.getUsername(), "playgroundsIDs", playgroundName);
            }

            // delete playground events
            for (EventDTO event : playground.getEvents()){
                // delete event reference in users
                MongoCollection users = new Jongo(datasource.getDatabase()).getCollection(IUserDAO.COLLECTION);
                for (UserDTO user : event.getAssignedUsers()) {
                    QueryUtils.updateWithPullObject(users, "username", user.getUsername(), "events", "_id", new ObjectId(event.getId()));
                }

                // delete event reference in playground
                MongoCollection playgrounds = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
                QueryUtils.updateWithPullObject(playgrounds, "name", event.getPlaygroundName(), "events", "_id", new ObjectId(event.getId()));

                // delete event
                eventDAO.deleteEvent(event.getId());
            }

            // delete playground messages
            for (MessageDTO message : playground.getMessages()){
                // delete message reference in playground
                MongoCollection collection = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
                QueryUtils.updateWithPullObject(collection, "name", message.getPlaygroundName(), "messages", "_id", new ObjectId(message.getId()));

                // delete message
                messageDAO.deleteMessage(message.getId());
            }


            // delete playground
            wr = playgroundDAO.deletePlayground(playgroundName);
            session.commitTransaction();

        } catch (NoSuchElementException e){
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        } catch (NoModificationException e){
            e.printStackTrace();
            throw new NoModificationException(e.getMessage());
        } catch (MongoException e){
            e.printStackTrace();
            throw new MongoException("Internal error");
        }

        return wr;
    }

    @Override
    public WriteResult deleteUser(String username)
            throws NoModificationException, NoSuchElementException, MongoException {

        final ClientSession session = datasource.getClient().startSession();
        WriteResult wr;
        try (session){
            session.startTransaction();
            UserDTO user = userDAO.getUser(username);

            // delete user reference from playground
            for (String playgroundName : user.getPlaygroundsIDs()){
                // remove user reference in playground
                MongoCollection collection = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
                QueryUtils.updateWithPullObject(collection, "name", playgroundName, "assignedPedagogue", "username", user.getUsername());

                // remove playground reference in user
                MongoCollection collection2 = new Jongo(datasource.getDatabase()).getCollection(IUserDAO.COLLECTION);
                QueryUtils.updateWithPullSimple(collection2, "username", user.getUsername(), "playgroundsIDs", playgroundName);
            }


            // delete user reference in events
            for (EventDTO event : user.getEvents()){
                // delete user reference in event
                MongoCollection events = new Jongo(datasource.getDatabase()).getCollection(IEventDAO.COLLECTION);
                QueryUtils.updateWithPullObject(events, "_id", new ObjectId(event.getId()), "assignedUsers", "username", username);

                // delete event reference in user
                MongoCollection users = new Jongo(datasource.getDatabase()).getCollection(IUserDAO.COLLECTION);
                QueryUtils.updateWithPullObject(users, "username", username, "events", "_id", new ObjectId(event.getId()));
            }

            // delete user
            wr = userDAO.deleteUser(username);
            session.commitTransaction();

        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        } catch (NoModificationException e){
            e.printStackTrace();
            throw new NoModificationException(e.getMessage());
        } catch (MongoException e){
            e.printStackTrace();
            throw new MongoException("Internal error");
        }

        return wr;
    }

    @Override
    public WriteResult addPedagogueToPlayground(String plagroundName, String username)
            throws NoModificationException, NoSuchElementException, MongoException {

        //final ClientSession session = datasource.getClient().startSession();
        WriteResult wr;
        try {
            //session.startTransaction();
            UserDTO pedagogue = userDAO.getUser(username);

            // insert playground reference in user
            pedagogue.getPlaygroundsIDs().add(plagroundName);
            wr = userDAO.updateUser(pedagogue);

            // insert user reference in playground
            MongoCollection playgrounds = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
            QueryUtils.updateWithPush(playgrounds, "name",
                    plagroundName, "assignedPedagogue", pedagogue);

            //session.commitTransaction();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        } catch (NoModificationException e){
            e.printStackTrace();
            throw new NoModificationException(e.getMessage());
        } catch (MongoException e){
            e.printStackTrace();
            throw new MongoException("Internal error");
        }

        return wr;
    }


    /* //TODO: what is this?
    public boolean addPedagogueToPlayground(User user) {
        final ClientSession session = DataSource.getProductionClient().startSession();
        WriteResult wr;
        try (session) {
            session.startTransaction();

            for (String playgroundName : user.getPlaygroundsIDs()) {
                Playground playground = Controller.getInstance(DataSource.getTestDB()).getPlayground(playgroundName);
                playground.getAssignedPedagogue().add(user);
                Controller.getInstance(DataSource.getTestDB()).updatePlayground(playground);
            }


            session.commitTransaction();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        } catch (NoModificationException e){
            e.printStackTrace();
            throw new NoModificationException(e.getMessage());
        } catch (MongoException e){
            e.printStackTrace();
            throw new MongoException("Internal error");
        }

        return wr;
    }
     */
    @Override
    public WriteResult addUserToEvent(String eventID, String username)
            throws NoModificationException, NoSuchElementException, MongoException {

        //ClientSession session = datasource.getClient().startSession();
        WriteResult wr;

        try {
            //session.startTransaction();
            UserDTO user = userDAO.getUser(username);

            // update user with event reference
            user.getEvents().add(new EventDTO.Builder().id(new ObjectId(eventID).toString()).build());
            userDAO.updateUser(user);

            // insert user reference in event
            Jongo jongo = new Jongo(datasource.getDatabase());
            MongoCollection events = jongo.getCollection(IEventDAO.COLLECTION);
            wr = QueryUtils.updateWithPush(events, "_id", new ObjectId(eventID), "assignedUsers", user);

            //session.commitTransaction();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        } catch (NoModificationException e){
            e.printStackTrace();
            throw new NoModificationException(e.getMessage());
        } catch (MongoException e){
            e.printStackTrace();
            throw new MongoException("Internal error");
        }

        return wr;
    }

    @Override
    public WriteResult createPlaygroundEvent(String playgroundName, EventDTO event)
            throws NoModificationException, NoSuchElementException, MongoException {

        //ClientSession session = datasource.getClient().startSession();
        WriteResult wr;

        try {
            //session.startTransaction();

            // create event in event collection
            event.setPlayground(playgroundName);
            wr = eventDAO.createEvent(event);

            // insert event id in playground
            MongoCollection playgrounds = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
            QueryUtils.updateWithPush(playgrounds, "name", playgroundName, "events", event);

            //session.commitTransaction();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        } catch (NoModificationException e){
            e.printStackTrace();
            throw new NoModificationException(e.getMessage());
        } catch (MongoException e){
            e.printStackTrace();
            throw new MongoException("Internal error");
        }

        return wr;
    }

    @Override
    public WriteResult createPlaygroundMessage(String playgroundName, MessageDTO message)
            throws NoModificationException, NoSuchElementException, MongoException {

        //ClientSession session = datasource.getClient().startSession();
        WriteResult result;

        try  {
            //session.startTransaction();

            // create message in message collection
            message.setPlaygroundID(playgroundName);
            result = messageDAO.createMessage(message);

            // update playground array with reference to message
            MongoCollection playgrounds = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
            QueryUtils.updateWithPush(playgrounds, "name", playgroundName, "messages", message);

            //session.commitTransaction();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        } catch (NoModificationException e){
            e.printStackTrace();
            throw new NoModificationException(e.getMessage());
        } catch (MongoException e){
            e.printStackTrace();
            throw new MongoException("Internal error");
        }

        return result;
    }

    @Override
    public void removePedagogueFromPlayground(String playgroundName, String username)
            throws NoModificationException, NoSuchElementException, MongoException {

        ClientSession session = datasource.getClient().startSession();
        try(session){
            session.startTransaction();

            // remove user reference in playground
            MongoCollection playground = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
            QueryUtils.updateWithPullObject(playground, "name", playgroundName, "assignedPedagogue", "username", username);

            // remove playground reference in user
            MongoCollection user = new Jongo(datasource.getDatabase()).getCollection(IUserDAO.COLLECTION);
            QueryUtils.updateWithPullSimple(user, "username", username, "playgroundsIDs", playgroundName);
            session.commitTransaction();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        } catch (NoModificationException e){
            e.printStackTrace();
            throw new NoModificationException(e.getMessage());
        } catch (MongoException e){
            e.printStackTrace();
            throw new MongoException("Internal error");
        }

       /*
        User removeUser = null;
        Playground playground = Controller.getInstance(DataSource.getTestDB()).getPlayground(playgroundName);
        for (User user : playground.getAssignedPedagogue()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                removeUser = user;
                break;
            }
        }
        playground.getAssignedPedagogue().remove(removeUser);
        Controller.getInstance(DataSource.getTestDB()).updatePlayground(playground);

        */
    }

    @Override
    public WriteResult deletePlaygroundMessage(String messageID)
            throws NoModificationException, NoSuchElementException, MongoException {

        ClientSession session = datasource.getClient().startSession();
        WriteResult wr;
        try (session){
            session.startTransaction();
            MessageDTO message = messageDAO.getMessage(messageID);

            // delete message reference in playground
            MongoCollection playground = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
            QueryUtils.updateWithPullObject(playground, "name", message.getPlaygroundName(), "messages", "_id", new ObjectId(messageID));

            // delete message
            wr = messageDAO.deleteMessage(messageID);
            session.commitTransaction();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        } catch (NoModificationException e){
            e.printStackTrace();
            throw new NoModificationException(e.getMessage());
        } catch (MongoException e){
            e.printStackTrace();
            throw new MongoException("Internal error");
        }

        return wr;
    }

    @Override
    public void removeUserFromEvent(String eventID, String username)
            throws NoModificationException, NoSuchElementException, MongoException {

        ClientSession session = datasource.getClient().startSession();
        try (session){
            session.startTransaction();

            // delete user reference in event
            MongoCollection events = new Jongo(datasource.getDatabase()).getCollection(IEventDAO.COLLECTION);
            QueryUtils.updateWithPullObject(events, "_id", new ObjectId(eventID), "assignedUsers", "username", username);

            // delete event reference in user
            MongoCollection users = new Jongo(datasource.getDatabase()).getCollection(IUserDAO.COLLECTION);
            QueryUtils.updateWithPullObject(users, "username", username, "events", "_id", new ObjectId(eventID));
            session.commitTransaction();

        }catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        } catch (NoModificationException e){
            e.printStackTrace();
            throw new NoModificationException(e.getMessage());
        } catch (MongoException e){
            e.printStackTrace();
            throw new MongoException("Internal error");
        }
    }

    @Override
    public WriteResult deletePlaygroundEvent(String eventID)
            throws NoModificationException, NoSuchElementException, MongoException {

        ClientSession session = datasource.getClient().startSession();
        WriteResult wr;

        try (session) {
            session.startTransaction();
            EventDTO event = eventDAO.getEvent(eventID);

            // delete event reference in users
            MongoCollection users = new Jongo(datasource.getDatabase()).getCollection(IUserDAO.COLLECTION);
            for (UserDTO user : event.getAssignedUsers()) {
                QueryUtils.updateWithPullObject(users, "username", user.getUsername(), "events", "_id", new ObjectId(eventID));
            }

            // delete event reference in playground
            MongoCollection playgrounds = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
            QueryUtils.updateWithPullObject(playgrounds, "name", event.getPlaygroundName(), "events", "_id", new ObjectId(eventID));

            // delete event
            wr = eventDAO.deleteEvent(eventID);
            session.commitTransaction();

        } catch (NoSuchElementException e) {
            e.printStackTrace();
            throw new NoSuchElementException(e.getMessage());
        } catch (NoModificationException e){
            e.printStackTrace();
            throw new NoModificationException(e.getMessage());
        } catch (MongoException e){
            e.printStackTrace();
            throw new MongoException("Internal error");
        }

        return wr;
    }

    @Override
    public void killAll(){
        playgroundDAO.deleteAllPlaygrounds();
        userDAO.deleteAllUsers();
        messageDAO.deleteAllMessages();
        eventDAO.deleteAllEvents();
    }

    @Override
    public void setDataSource(IDataSource dataSource) {
        this.datasource = dataSource;
        playgroundDAO.setDataSource(dataSource);
        userDAO.setDataSource(dataSource);
        messageDAO.setDataSource(dataSource);
        eventDAO.setDataSource(dataSource);
    }
}
