package database.dao;

import com.mongodb.WriteResult;
import database.collections.Event;
import database.collections.Message;
import database.collections.Playground;
import database.collections.User;

import java.util.List;

public interface IController {

    // CREATE
    WriteResult createPlayground(Playground playground); // done
    WriteResult createUser(User user);

    // READ
    Playground getPlayground(String playgroundName); // done
    User getUser(String username);
    Event getEvent(String eventID); // done
    Message getMessage(String messageID); // done

    List<Playground> getPlaygrounds(); // done
    List<User> getUsers();
    List<Event> getPlaygroundEvents(String playgroundName); // done
    List<Message> getPlaygroundMessages(String playgroundName); // done

    // UPDATE
    boolean updatePlayground(Playground playground); // done
    boolean updateUser(User user);
    boolean updatePlaygroundEvent(Event event); // done
    boolean updatePlaygroundMessage(Message message); // done

    // DELETE
    boolean deletePlayground(String playgroundName); // done
    boolean deleteUser(String username);

    // ASSOCIATIONS
    boolean addPedagogueToPlayground(String plagroundName, String username); // done
    boolean addUserToPlaygroundEvent(String eventID, String username); // done
    WriteResult addPlaygroundEvent(String playgroundName, Event event); // done
    WriteResult addPlaygroundMessage(String playgroundName, Message message); // done

    boolean removePedagogueFromPlayground(String playgroundName, String username); // done
    boolean removeUserFromPlaygroundEvent(String eventID, String username); // done
    boolean removePlaygroundEvent(String eventID); // done
    boolean removePlaygroundMessage(String messageID);
}