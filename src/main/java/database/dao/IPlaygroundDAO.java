package database.dao;

import com.mongodb.WriteResult;
import database.DALException;
import database.NoModificationException;
import database.collections.Playground;

import java.util.List;
import java.util.NoSuchElementException;

public interface IPlaygroundDAO {
    String COLLECTION = "playgrounds";

    WriteResult createPlayground(Playground playground) throws IllegalArgumentException, NoModificationException;

    Playground getPlayground(String id) throws IllegalArgumentException, NoSuchElementException;

    List<Playground> getPlaygroundList() throws NoSuchElementException;

    WriteResult updatePlayground(Playground playground) throws IllegalArgumentException, NoModificationException;

    WriteResult deletePlayground(String id) throws IllegalArgumentException, NoModificationException;

    WriteResult deleteAllPlaygrounds();
}
