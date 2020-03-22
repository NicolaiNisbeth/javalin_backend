package database;

import com.mongodb.*;

public class DataSource {
    private static DB database;
    private static String host = "130.225.170.204";
    private static int port = 27027;
    private static String databaseName = "cphPlaygroundsDB";
    private static MongoClient mongoClient;

    private DataSource() {
    }
    // Since 2.10.0, uses MongoClient
    /*MongoClient mongoClient = new MongoClient();
	DB db = mongoClient.getDB("database name");
	boolean auth = db.authenticate("username", "password".toCharArray());*/

    /*
    public static DB getDB() {
        if (database == null) {
            mongoClient = new MongoClient(host, port);
            database = mongoClient.getDB("cphPlaygroundsDB");
        }

        return database;
    }

     */


   public static DB getDB(){
        if (database == null){
            mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://s175565:qwe123@todoapp-cn8eq.mongodb.net/test"));
            database = mongoClient.getDB("test");
        }

        return database;
    }
}
