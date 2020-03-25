package database.dao;

import database.DALException;
import database.collections.Message;

import java.util.List;

public interface IMessageDAO {
    String COLLECTION = "messages";

    boolean createMessage(Message message)       throws DALException;
    Message getMessage(String id)           throws DALException;
    List<Message> getMessageList()          throws DALException;
    boolean updateMessage(Message message)    throws DALException;
    boolean deleteMessage(String id)      throws DALException;

    boolean deleteAllMessages() throws DALException;
}
