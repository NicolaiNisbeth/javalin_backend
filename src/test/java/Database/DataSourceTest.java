package Database;

import Database.collections.Playground;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataSourceTest {

    @Test
    void getDB() {

        System.out.println(
                DataSource.getDB().getName());

    }


}