package Database.dao;

import Database.DALException;
import Database.collections.Event;
import Database.collections.Playground;

import java.util.List;

public interface IPlaygroundDAO {
    String COLLECTION = "playgrounds";


    void createPlayground()                             throws DALException;
    Playground getPlayground()                          throws DALException;
    List<Playground> getPlaygroundList()                throws DALException;
    boolean updatePlayground(Playground playground)     throws DALException;
    boolean deletePlayground(int id)                    throws DALException;
}
