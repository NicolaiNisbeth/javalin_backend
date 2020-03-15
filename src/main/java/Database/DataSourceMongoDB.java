package Database;

import com.mongodb.*;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class DataSourceMongoDB {

    //Mongo db admin: admin
    //password: admin123
    //Der er ingen security
    public static void main(String[] args) {
        try {
            MongoClient mongoClient = new MongoClient("130.225.170.204", 27027);
            DB database = mongoClient.getDB("cphPlaygroundsDB");
            System.out.println(database.getCollection("playgrounds").findOne());
            //  System.out.println(database.getName());
            //database.createCollection("customers", null);
            //boolean auth = database.authenticate("admin", "admin123".toCharArray());

          /*  database.createCollection("playgrounds", null);
            DBCollection collection = database.getCollection("playgrounds");
            BasicDBObject document = new BasicDBObject();
            document.put("name", "Shubham");
            document.put("company", "Baeldung");
            collection.insert(document);
            database.getCollectionNames().forEach(System.out::println);
            BasicDBObject query = new BasicDBObject();
            query.put("name", "Shubham");
            BasicDBObject newDocument = new BasicDBObject();
            newDocument.put("name", "John");
            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set", newDocument);
            collection.update(query, updateObject);
*/

            for (String name : database.getCollectionNames()
            ) {
                System.out.println(name);
            }

           /* List<String> dbs = mongoClient.getDatabaseNames();
            for (String db : dbs) {
                System.out.println(db);
            }
*/
            DBCollection collection = database.getCollection("playgrounds");

            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("name", "John");
            DBCursor cursor = collection.find(searchQuery);
            while (cursor.hasNext()) {
                System.out.println(cursor.next());
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
