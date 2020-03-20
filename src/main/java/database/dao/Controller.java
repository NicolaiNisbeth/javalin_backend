package database.dao;

import database.DALException;
import database.collections.Playground;

import java.util.List;

public class Controller implements IController{
    private PlaygroundDAO playgroundDAO;

    private static Controller controller;

    private Controller() {
        this.playgroundDAO = new PlaygroundDAO();
    }

    public static Controller getController() {
        if (controller == null) {
            controller = new Controller();
        }
        return controller;
    }

    public List<Playground> getAllPlaygrounds() {
        List<Playground> playgrounds = null;
        try {
            playgrounds = playgroundDAO.getPlaygroundList();
        } catch (DALException e) {
            e.printStackTrace();
        }
        return playgrounds;
    }
}
