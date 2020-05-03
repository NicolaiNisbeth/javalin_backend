package database;

import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import database.exceptions.NoModificationException;
import database.dto.EventDTO;
import database.dto.MessageDTO;
import database.dto.PlaygroundDTO;
import database.dto.UserDTO;

import java.util.List;
import java.util.NoSuchElementException;

public interface IController {

    // CREATE
    WriteResult createPlayground(PlaygroundDTO playground) throws IllegalArgumentException, NoModificationException;
    WriteResult createUser(UserDTO user) throws IllegalArgumentException, NoModificationException;

    // READ
    PlaygroundDTO getPlayground(String playgroundName) throws IllegalArgumentException, NoSuchElementException;
    UserDTO getUser(String username) throws IllegalArgumentException, NoSuchElementException;
    EventDTO getEvent(String eventID) throws IllegalArgumentException, NoSuchElementException;
    MessageDTO getMessage(String messageID) throws IllegalArgumentException, NoSuchElementException;
    List<PlaygroundDTO> getPlaygrounds() throws NoSuchElementException;

    List<UserDTO> getUsers() throws NoSuchElementException;
    List<EventDTO> getEventsInPlayground(String playgroundName);
    List<MessageDTO> getMessagesInPlayground(String playgroundName);

    List<UserDTO> getUsersInPlayground(String playgroundName);

    // UPDATE
    WriteResult updatePlayground(PlaygroundDTO playground) throws IllegalArgumentException, NoModificationException ;
    WriteResult updateUser(UserDTO user) throws IllegalArgumentException, NoModificationException ;
    WriteResult updatePlaygroundEvent(EventDTO event) throws IllegalArgumentException, NoModificationException ;
    WriteResult updatePlaygroundMessage(MessageDTO message) throws IllegalArgumentException, NoModificationException ;

    // DELETE
    WriteResult deletePlayground(String playgroundName) throws NoSuchElementException, NoModificationException, MongoException;
    WriteResult deleteUser(String username) throws NoSuchElementException, NoModificationException, MongoException;

    // ASSOCIATIONS
    WriteResult createPlaygroundEvent(String playgroundName, EventDTO event) throws NoSuchElementException, NoModificationException, MongoException;
    WriteResult createPlaygroundMessage(String playgroundName, MessageDTO message) throws NoSuchElementException, NoModificationException, MongoException;
    WriteResult addPedagogueToPlayground(String plagroundName, String username) throws NoSuchElementException, NoModificationException, MongoException;
    WriteResult addUserToEvent(String eventID, String username) throws NoSuchElementException, NoModificationException, MongoException;
    void removePedagogueFromPlayground(String playgroundName, String username) throws NoModificationException, NoSuchElementException, MongoException;
    void removeUserFromEvent(String eventID, String username) throws NoModificationException, NoSuchElementException, MongoException;
    WriteResult deletePlaygroundEvent(String eventID) throws NoModificationException, NoSuchElementException, MongoException;
    WriteResult deletePlaygroundMessage(String messageID) throws NoModificationException, NoSuchElementException, MongoException;

    void setDataSource(IDataSource dataSource);
    void killAll();
}