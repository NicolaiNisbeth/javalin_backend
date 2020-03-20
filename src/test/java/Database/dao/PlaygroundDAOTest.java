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
        Playground playground = new Playground.Builder("Trafiklegepladsen Fælledparken")
                .setCommune("København Ø")
                .setZipCode(2100)
                .setStreetName("Gunnar Nu Hansens Plads")
                .setStreetNumber(10)
                .build();
        playgroundDAO.createPlayground(playground);
    }

    @Test
    void getPlayground() {

    }

    @Test
    void getPlaygroundList() throws DALException {
        for (Playground playground : playgroundDAO.getPlaygroundList()) {
            System.out.println(playground);
        }
    }

    @Test
    void updatePlayground() {

    }

    @Test
    void deletePlayground() {

    }
}