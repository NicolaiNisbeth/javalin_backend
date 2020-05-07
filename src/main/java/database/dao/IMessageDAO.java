package database.dao;

import com.mongodb.WriteResult;
import database.IDataSource;
import database.dto.MessageDTO;
import database.exceptions.NoModificationException;

import java.util.List;
import java.util.NoSuchElementException;

public interface IMessageDAO {
  String COLLECTION = "messages";

  void setDataSource(IDataSource dataSource);

  WriteResult createMessage(MessageDTO message) throws IllegalArgumentException, NoModificationException;

  MessageDTO getMessage(String id) throws IllegalArgumentException, NoSuchElementException;

  List<MessageDTO> getMessageList() throws NoSuchElementException;

  WriteResult updateMessage(MessageDTO message) throws IllegalArgumentException, NoModificationException;

  WriteResult deleteMessage(String id) throws IllegalArgumentException, NoModificationException;

  WriteResult deleteAllMessages();
}
