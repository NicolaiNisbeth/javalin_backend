package Database;

import Database.collections.Playground;
import Database.collections.User;
import Database.collections.Event;
import com.google.gson.Gson;
import com.mongodb.*;

import java.util.ArrayList;

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

    public static DB getDB() {
        if (database == null) {
            mongoClient = new MongoClient(host, port);
            database = mongoClient.getDB("cphPlaygroundsDB");
        }
        return database;
    }
}
