package database;

import org.junit.jupiter.api.Test;

class DataSourceTest {

    @Test
    void getDB() {

        System.out.println(DataSource.getProductionDB().getName());


        for (String str : DataSource.getProductionDB().getCollectionNames()) {
            System.out.println(str);
        }

    }


}