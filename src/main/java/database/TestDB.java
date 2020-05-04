package database;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class TestDB implements IDataSource {
    private static TestDB instance;
    private static DB database;
    private static MongoClient mongoClient;
    private static final String TEST_DATABASE = "test";

    private TestDB(){}

    public static TestDB getInstance(){
        if (instance == null)
            instance = new TestDB();

        return instance;
    }

    @Override
    public MongoClient getClient() {
        if (mongoClient == null)
            mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://s175565:qwe123@todoapp-cn8eq.mongodb.net/test"));

        return mongoClient;
    }

    @Override
    public DB getDatabase() {
        if (database == null)
            database = getClient().getDB(TEST_DATABASE);

        return database;
    }

}
