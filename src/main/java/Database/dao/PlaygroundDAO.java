package Database.dao;

import Database.DALException;
import Database.collections.Playground;

import java.util.List;

public class PlaygroundDAO implements IPlaygroundDAO {
    @Override
    public void createPlayground() throws DALException {

    }

    @Override
    public Playground getPlayground() throws DALException {
        return null;
    }

    @Override
    public List<Playground> getPlaygroundList() throws DALException {
        return null;
    }

    @Override
    public boolean updatePlayground(Playground playground) throws DALException {
        return false;
    }

    @Override
    public boolean deletePlayground(int id) throws DALException {
        return false;
    }
}
