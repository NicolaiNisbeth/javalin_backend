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
                .commune("København Ø")
                .zipCode(2100)
                .streetName("Gunnar Nu Hansens Plads")
                .streetNumber(10)
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