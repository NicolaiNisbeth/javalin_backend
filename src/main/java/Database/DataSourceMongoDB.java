package Database;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;

public class DataSourceMongoDB {

    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource hikari;

    public static void main(String[] args) {
        try {
            // MongoClient mongoClient = new MongoClient("", 27017);
            //String url = "mongodb://130.225.170.214:27017";
            //MongoClient mongoClient = new MongoClient(url);

            MongoClient mongoClient = new MongoClient("130.225.170.204", 27027);
           DB db1 = mongoClient.getDB("cphPlaygrounds");
            System.out.println(db1.getName());;
           boolean auth = db1.authenticate("admin", "admin123".toCharArray());
            List<String> dbs = mongoClient.getDatabaseNames();
            for(String db : dbs){
                System.out.println(db);
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }



        /*	MongoClient mongoClient = new MongoClient();
	DB db = mongoClient.getDB("database name");
	boolean auth = db.authenticate("username", "password".toCharArray());
*/

        // Since 2.10.0, uses MongoClient

    }


}
