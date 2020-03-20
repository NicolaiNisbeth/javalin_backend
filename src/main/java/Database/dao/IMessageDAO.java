package Database.dao;

import Database.DALException;
import Database.collections.Event;
import Database.collections.Message;

import java.util.List;

public interface IMessageDAO {
    String COLLECTION = "messages";

    boolean createMessage(Message message)       throws DALException;
    Message getMessage(String id)           throws DALException;
    List<Message> getMessageList()          throws DALException;
    boolean updateMessage(Message message)    throws DALException;
    boolean deleteMessage(String id)      throws DALException;
}
