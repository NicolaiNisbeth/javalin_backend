package Database.dao;

import Database.DALException;
import Database.collections.Playground;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlaygroundDAOTest {
    static PlaygroundDAO playgroundDAO;

    @BeforeAll
    public static void init() {
        playgroundDAO = new PlaygroundDAO();
    }

    @Test
    void createPlayground() throws DALException {
        Playground playground = new Playground.Builder("Vandlegepladsen")
                .setCommune("København Ø")
                .setZipCode(2100)
                .setStreetName("Gunnar Nu Hansens Plads")
                .setStreetNumber(10)
                .build();
        playgroundDAO.createPlayground(playground);
    }

    @Test
    void getPlayground() throws DALException {
        System.out.println(playgroundDAO.getPlayground("5e7500a29c55065cb293b635"));
    }

    @Test
    void getPlaygroundList() throws DALException {
        for (Playground playground : playgroundDAO.getPlaygroundList()) {
            System.out.println(playground);
        }
    }

    @Test
    void updatePlayground() throws DALException {
        Playground playground = playgroundDAO.getPlayground("5e7500a29c55065cb293b635");
        System.out.println(playground);
        playground.setName("Ny plads");
        playground.setCommune("SNaps");
        playgroundDAO.updatePlayground(playground);
        System.out.println(playgroundDAO.getPlayground("5e7500a29c55065cb293b635"));
    }

    @Test
    void deletePlayground() throws DALException {
            playgroundDAO.deletePlayground("5e7500a29c55065cb293b635");

        /*playgroundDAO.deleteAllPlaygrounds();
        getPlaygroundList();*/
    }
}