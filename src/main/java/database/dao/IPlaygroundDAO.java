package database.dao;

import com.mongodb.WriteResult;
import database.IDataSource;
import database.dto.PlaygroundDTO;
import database.exceptions.NoModificationException;

import java.util.List;
import java.util.NoSuchElementException;

public interface IPlaygroundDAO {
  String COLLECTION = "playgrounds";

  void setDataSource(IDataSource dataSource);

  WriteResult createPlayground(PlaygroundDTO playground) throws IllegalArgumentException, NoModificationException;

  PlaygroundDTO getPlayground(String id) throws IllegalArgumentException, NoSuchElementException;

  List<PlaygroundDTO> getPlaygroundList() throws NoSuchElementException;

  WriteResult updatePlayground(PlaygroundDTO playground) throws IllegalArgumentException, NoModificationException;

  WriteResult deletePlayground(String id) throws IllegalArgumentException, NoModificationException;

  WriteResult deleteAllPlaygrounds();
}
