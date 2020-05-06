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
import resources.Message;
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
        /*this.datasource = ProductionDB.getInstance(); // production database by default*/
        this.datasource = ProductionDBnjl.getInstance(); // production database by default
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
        Set<UserDTO> pedagogues = new HashSet<>(getUsersInPlayground(playgroundName));
        playground.setAssignedPedagogue(pedagogues);

        // fetch events based on id
        Set<EventDTO> events = new HashSet<>(getEventsInPlayground(playgroundName));
        playground.setEvents(events);

        // fetch messages based on id
        Set<MessageDTO> messages = new HashSet<>(getMessagesInPlayground(playgroundName));
        playground.setMessages(messages);
        return playground;
    }

    @Override
    public UserDTO getUser(String username) throws NoSuchElementException, IllegalArgumentException {
        UserDTO user = userDAO.getUser(username);

        // fetch all events based on id
        Set<EventDTO> updatedEvents = new HashSet<>();
        Set<EventDTO> events = user.getEvents();
        for (EventDTO value : events) {
            try {
                EventDTO event = eventDAO.getEvent(value.getID());
                updatedEvents.add(event);
            } catch (NoSuchElementException | IllegalArgumentException e){ }
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
                try {
                    UserDTO u = userDAO.getUser(user.getUsername());
                    u.setEvents(null); //TODO: njl does angular depend on this information
                    updatedUser.add(u);
                } catch (NoSuchElementException | IllegalArgumentException e){ }
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
        List<PlaygroundDTO> playgrounds = new ArrayList<>();
        for (PlaygroundDTO playgroundNameObj : playgroundDAO.getPlaygroundList()){
            String playgroundName = playgroundNameObj.getName();
            PlaygroundDTO playground = getPlayground(playgroundName);
            playgrounds.add(playground);
        }

        // to avoid unnecessary deep nesting
        for (PlaygroundDTO playground : playgrounds){
            for (UserDTO pedagogue : playground.getAssignedPedagogue()){
                pedagogue.setEvents(null); //TODO: njl does angular depend on this information
            }
            for (EventDTO eventDTO : playground.getEvents()){
                eventDTO.setAssignedUsers(null); //TODO: njl does angular depend on this information
            }
        }
        return playgrounds;
    }

    @Override
    public List<UserDTO> getUsers() throws NoSuchElementException {
        return userDAO.getUserList();
        /*
        List<UserDTO> users = new ArrayList<>();
        for (UserDTO usernameObj : userDAO.getUserList()){
            String username = usernameObj.getUsername();
            UserDTO user = getUser(username);
            users.add(user);
        }
        return users;

         */
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
    public List<UserDTO> getUsersInPlayground(String playgroundName){
        Jongo jongo = new Jongo(datasource.getDatabase());
        MongoCollection collection = jongo.getCollection(IUserDAO.COLLECTION);
        MongoCursor<UserDTO> cursor = collection.find("{playgroundsNames : #}",playgroundName).as(UserDTO.class);
        List<UserDTO> users = new ArrayList<>();
        for (UserDTO user : cursor){
            users.add(user);
        }
        return users;
    }

    @Override
    public WriteResult updatePlayground(PlaygroundDTO playground)
            throws IllegalArgumentException, NoModificationException {
        return playgroundDAO.updatePlayground(playground);
    }

    @Override
    public WriteResult updateUser(UserDTO user)
            throws IllegalArgumentException, NoModificationException {
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
        try (session){
            session.startTransaction();

            PlaygroundDTO playground = playgroundDAO.getPlayground(playgroundName);
            for (UserDTO pedagogue : playground.getAssignedPedagogue()){
                String username = pedagogue.getUsername();
                removeUserRefInPlayground(username, playgroundName);
                removePlaygroundRefInUser(username, playgroundName);
            }

            for (EventDTO event : playground.getEvents()){
                String eventID = event.getID();
                for (UserDTO user : event.getAssignedUsers())
                    removeEventRefInUser(eventID, user.getUsername());

                removeEventRefInPlayground(eventID, playgroundName);
                eventDAO.deleteEvent(event.getID());
            }

            for (MessageDTO message : playground.getMessages()){
                String messageID = message.getID();
                removeMessageRefInPlayground(messageID, playgroundName);
                messageDAO.deleteMessage(message.getID());
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
            for (String playgroundName : user.getPlaygroundsNames()){
                removeUserRefInPlayground(username, playgroundName);
            }

            for (EventDTO event : user.getEvents()){
                String eventID = event.getID();
                removeUserRefInEvent(eventID, username);
                removeEventRefInUser(eventID, username);
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

        final ClientSession session = datasource.getClient().startSession();
        WriteResult wr;
        try (session) {
            session.startTransaction();
            UserDTO pedagogue = userDAO.getUser(username);

            // insert playground reference in user
            pedagogue.getPlaygroundsNames().add(plagroundName);
            wr = userDAO.updateUser(pedagogue);

            // insert user reference in playground
            MongoCollection playgrounds = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
            UserDTO usernameObj = new UserDTO.Builder(username).build();
            QueryUtils.updateWithPush(playgrounds, "name",
                    plagroundName, "assignedPedagogue", usernameObj);

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
    public WriteResult addUserToEvent(String eventID, String username)
            throws NoModificationException, NoSuchElementException, MongoException {

        ClientSession session = datasource.getClient().startSession();
        WriteResult wr;

        try {
            session.startTransaction();
            UserDTO user = userDAO.getUser(username);
            System.out.println("asd " + user);

            // update user with event reference
            user.getEvents().add(new EventDTO.Builder().id(new ObjectId(eventID).toString()).build());
            userDAO.updateUser(user);

            // insert user reference in event
            Jongo jongo = new Jongo(datasource.getDatabase());
            MongoCollection events = jongo.getCollection(IEventDAO.COLLECTION);
            UserDTO usernameObj = new UserDTO.Builder(username).build();
            wr = QueryUtils.updateWithPush(events, "_id", new ObjectId(eventID), "assignedUsers", usernameObj);

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
    public WriteResult createPlaygroundEvent(String playgroundName, EventDTO event)
            throws NoModificationException, NoSuchElementException, MongoException {

        ClientSession session = datasource.getClient().startSession();
        WriteResult wr;

        try (session) {
            session.startTransaction();

            // create event in event collection
            event.setPlayground(playgroundName);
            wr = eventDAO.createEvent(event);

            // insert event id in playground
            MongoCollection playgrounds = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
            EventDTO idObj = new EventDTO.Builder().id(new ObjectId(event.getID()).toString()).build();
            QueryUtils.updateWithPush(playgrounds, "name", playgroundName, "events", idObj);

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
    public WriteResult createPlaygroundMessage(String playgroundName, MessageDTO message)
            throws NoModificationException, NoSuchElementException, MongoException {

        ClientSession session = datasource.getClient().startSession();
        WriteResult result;

        try (session)  {
            session.startTransaction();

            // create message in message collection
            message.setPlaygroundID(playgroundName);
            result = messageDAO.createMessage(message);

            // update playground array with reference to message
            MongoCollection playgrounds = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
            MessageDTO idObj = new MessageDTO.Builder().set_id(new ObjectId(result.getUpsertedId().toString()).toString()).build();
            QueryUtils.updateWithPush(playgrounds, "name", playgroundName, "messages", idObj);

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

        return result;
    }

    @Override
    public void removePedagogueFromPlayground(String playgroundName, String username)
            throws NoModificationException, NoSuchElementException, MongoException {

        ClientSession session = datasource.getClient().startSession();
        try(session){
            session.startTransaction();

            removeUserRefInPlayground(username, playgroundName);
            removePlaygroundRefInUser(username, playgroundName);

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
    }

    @Override
    public WriteResult deletePlaygroundMessage(String messageID)
            throws NoModificationException, NoSuchElementException, MongoException {

        ClientSession session = datasource.getClient().startSession();
        WriteResult wr;
        try (session){
            session.startTransaction();
            MessageDTO message = messageDAO.getMessage(messageID);
            String playgroundName = message.getPlaygroundName();

            // delete message reference in playground
            removeMessageRefInPlayground(messageID, playgroundName);

            // delete message
            wr = messageDAO.deleteMessage(messageID);

            //delete message image
            Message.deleteMessageImage(messageID);


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
            removeUserRefInEvent(eventID, username);

            // delete event reference in user
            removeEventRefInUser(eventID, username);

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
            for (UserDTO user : event.getAssignedUsers()) {
                String username = user.getUsername();
                removeEventRefInUser(eventID, username);
            }

            // delete event reference in playground
            String playgroundName = event.getPlaygroundName();
            removeEventRefInPlayground(eventID, playgroundName);

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
    public List<EventDTO> getEvents() {
        try {
            return eventDAO.getEventList();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<MessageDTO> getmessages() {
        try {
            return messageDAO.getMessageList();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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

    private void removeUserRefInPlayground(String username, String playgroundName) throws NoModificationException {
        // remove user reference in playground
        MongoCollection collection = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
        QueryUtils.updateWithPullObject(collection, "name", playgroundName, "assignedPedagogue",
                "username", username);
    }

    private void removeUserRefInEvent(String eventID, String username) throws NoModificationException{
        MongoCollection events = new Jongo(datasource.getDatabase()).getCollection(IEventDAO.COLLECTION);
        QueryUtils.updateWithPullObject(events, "_id", new ObjectId(eventID),
                "assignedUsers", "username", username);
    }

    private void removePlaygroundRefInUser(String username, String playgroundName) throws NoModificationException {
        // remove playground reference in user
        MongoCollection collection = new Jongo(datasource.getDatabase()).getCollection(IUserDAO.COLLECTION);
        QueryUtils.updateWithPullSimple(collection, "username", username,
                "playgroundsNames", playgroundName);
    }

    private void removeEventRefInUser(String eventID, String username)
            throws NoModificationException, IllegalArgumentException{

        // delete event reference in users
        MongoCollection collection = new Jongo(datasource.getDatabase()).getCollection(IUserDAO.COLLECTION);
        QueryUtils.updateWithPullObject(collection, "username", username,
                "events", "_id", new ObjectId(eventID));
    }

    private void removeEventRefInPlayground(String eventID, String playgroundName)
            throws NoModificationException, IllegalArgumentException{

        // delete event reference in playground
        MongoCollection playgrounds = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
        QueryUtils.updateWithPullObject(playgrounds, "name", playgroundName, "events",
                "_id", new ObjectId(eventID));
    }


    private void removeMessageRefInPlayground(String messageID, String playgroundName)
            throws NoModificationException, IllegalArgumentException {

        // delete message reference in playground
        MongoCollection collection = new Jongo(datasource.getDatabase()).getCollection(IPlaygroundDAO.COLLECTION);
        QueryUtils.updateWithPullObject(collection, "name", playgroundName,
                "messages", "_id", new ObjectId(messageID));
    }


}
