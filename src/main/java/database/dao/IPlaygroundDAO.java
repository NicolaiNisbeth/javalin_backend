package database.dao;

import database.DALException;
import database.collections.Playground;

import java.util.List;

public interface IPlaygroundDAO {
    String COLLECTION = "playgrounds";

    void createPlayground(Playground playground)        throws DALException;
    Playground getPlayground(String id)                          throws DALException;
    List<Playground> getPlaygroundList()                throws DALException;
    boolean updatePlayground(Playground playground)     throws DALException;
    boolean deletePlayground(String id)                 throws DALException;
}
