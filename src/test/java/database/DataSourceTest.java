package database;

import org.junit.jupiter.api.Test;

class DataSourceTest {

    @Test
    void getDB() {

        System.out.println(DataSource.getDB().getName());


        for (String str : DataSource.getDB().getCollectionNames()) {
            System.out.println(str);
        }
    }


}