package database;

import database.dao.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class InitTestData {
    static IController controller;
    static IEventDAO eventDAO;
    static IMessageDAO messageDAO;
    static IPlaygroundDAO playgroundDAO;
    static IUserDAO userDAO;

    @BeforeAll
    static void setup(){
        controller = Controller.getInstance();
        eventDAO = new EventDAO();
        messageDAO = new MessageDAO();
        playgroundDAO = new PlaygroundDAO();
        userDAO = new UserDAO();
    }

    @AfterAll
    static void tearDown(){
        controller = null;
        eventDAO = null;
        messageDAO = null;
        playgroundDAO = null;
        userDAO = null;
    }

    @Test
    void initFreshData() throws DALException {
        try {
            playgroundDAO.deleteAllPlaygrounds();
        } catch (DALException e){}

        try {
            eventDAO.deleteAllEvents();
        } catch (DALException e){}

        try {
            userDAO.deleteAllUsers();
        } catch (DALException e){}

        try {
            messageDAO.deleteAllMessages();
        } catch (DALException e){}


        // init users
        // init playgrounds
        // init playground events
        // init playground messages
    }



}
