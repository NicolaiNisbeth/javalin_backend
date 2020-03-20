package Database;

import Database.ogm_collections.Playground;
import org.junit.jupiter.api.Test;

class DatabaseManagementTest {

    @Test
    void getPlaygrounds() {
        for (Playground playground : DatabaseManagement.getPlaygrounds()) {
            System.out.println(playground);
        }
    }

    @Test
    void getPlaygrounds2() {
        for (Playground playground : DatabaseManagement.getPlaygrounds2()) {
            System.out.println(playground);
        }
    }
}