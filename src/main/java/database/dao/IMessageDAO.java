package database.dao;

import com.mongodb.WriteResult;
import database.IDataSource;
import database.exceptions.NoModificationException;
import database.collections.Message;

import java.util.List;
import java.util.NoSuchElementException;

public interface IMessageDAO {
    String COLLECTION = "messages";

    void setDataSource(IDataSource dataSource);

    WriteResult createMessage(Message message) throws IllegalArgumentException, NoModificationException;

    Message getMessage(String id) throws IllegalArgumentException, NoSuchElementException;

    List<Message> getMessageList() throws NoSuchElementException;

    WriteResult updateMessage(Message message) throws IllegalArgumentException, NoModificationException;

    WriteResult deleteMessage(String id) throws IllegalArgumentException, NoModificationException;

    WriteResult deleteAllMessages();
}
