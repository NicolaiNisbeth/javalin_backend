package database;


import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.Collections;

public class DataSource {
    private final static String HOST = "18.185.121.182";
    private final static int PORT = 27017;
    private final static String DATABASE_NAME = "cphPlaygroundsDB";
    private static DB database;
    private static MongoClient mongoClient;

    private final static String user = "myAdmin"; // the user name
    private final static String adminDatabase = "admin"; // the name of the database in which the user is defined
    private final static char[] password = ("njl_nykode").toCharArray(); // the setPassword as a character array

    private DataSource() { }

    public static DB getProductionDB() {
        if (database == null) {
            MongoCredential credential = MongoCredential.createCredential(user, adminDatabase, password);
            mongoClient = new MongoClient(new ServerAddress(HOST, PORT), Collections.singletonList(credential));
            database = mongoClient.getDB(DATABASE_NAME);
        }
        return database;
    }

    public static MongoClient getProductionClient() {
        if (mongoClient == null) {
            MongoCredential credential = MongoCredential.createCredential(user, adminDatabase, password);
            mongoClient = new MongoClient(new ServerAddress(HOST, PORT), Collections.singletonList(credential));
        }
        return mongoClient;
    }

  public static DB getTestDB(){
        if (database == null)
            database = getTestClient().getDB("test");

        return database;
  }

  public static MongoClient getTestClient(){
      if (mongoClient == null)
          mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://s175565:qwe123@todoapp-cn8eq.mongodb.net/test"));

      return mongoClient;
  }
}
