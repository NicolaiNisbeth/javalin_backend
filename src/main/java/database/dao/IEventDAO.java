package database.dao;

import com.mongodb.WriteResult;
import database.IDataSource;
import database.dto.EventDTO;
import database.exceptions.NoModificationException;

import java.util.List;
import java.util.NoSuchElementException;

public interface IEventDAO {
  String COLLECTION = "events";

  void setDataSource(IDataSource dataSource);

  WriteResult createEvent(EventDTO event) throws IllegalArgumentException, NoModificationException;

  EventDTO getEvent(String id) throws IllegalArgumentException, NoSuchElementException;

  List<EventDTO> getEventList() throws NoSuchElementException;

  WriteResult updateEvent(EventDTO event) throws IllegalArgumentException, NoModificationException;

  WriteResult deleteEvent(String id) throws IllegalArgumentException, NoModificationException;

  WriteResult deleteAllEvents();
}
