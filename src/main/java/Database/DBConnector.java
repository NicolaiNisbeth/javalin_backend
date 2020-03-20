/*
package Database;

import com.mongodb.MongoClient;

public class DBConnector {
    static DBConnector instance;
    private final static String database = "GalgeLeg";
    private MongoClient mongoClient;
    private DBConnector(){

        this.mongoClient = new MongoClient(new MongoClientURI(""));


    }

    public static DB getInstance(){
        if(instance == null){
            instance = new DBConnector();
            return instance.mongoClient.getDB(database);
        }else{
            return instance.mongoClient.getDB(database);
        }
    }

}
*/
