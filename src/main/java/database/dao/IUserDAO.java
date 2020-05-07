package database.dao;

import com.mongodb.WriteResult;
import database.IDataSource;
import database.dto.UserDTO;
import database.exceptions.NoModificationException;

import java.util.List;
import java.util.NoSuchElementException;

public interface IUserDAO {
  String COLLECTION = "users";

  void setDataSource(IDataSource dataSource);

  WriteResult createUser(UserDTO user) throws IllegalArgumentException, NoModificationException;

  UserDTO getUser(String username) throws NoSuchElementException, IllegalArgumentException;

  List<UserDTO> getUserList() throws NoSuchElementException;

  WriteResult updateUser(UserDTO user) throws IllegalArgumentException, NoModificationException;

  WriteResult deleteUser(String username) throws NoModificationException;

  WriteResult deleteAllUsers();
}
