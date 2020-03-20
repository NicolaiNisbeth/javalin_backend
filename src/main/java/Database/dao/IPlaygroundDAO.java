package Database.dao;

import Database.DALException;
import Database.collections.Event;
import Database.collections.Playground;

import java.util.List;

public interface IPlaygroundDAO {
    String COLLECTION = "playgrounds";

    void createPlayground(Playground playground)        throws DALException;
    Playground getPlayground(String id)                          throws DALException;
    List<Playground> getPlaygroundList()                throws DALException;
    boolean updatePlayground(Playground playground)     throws DALException;
    boolean deletePlayground(String id)                 throws DALException;
}
