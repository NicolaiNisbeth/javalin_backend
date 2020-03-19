package Database;

import Database.DTOs.PlaygroundDTO;
import Database.DTOs.PlaygroundDTODum;
import Database.collections.User;
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class DataSourceMongoDB {

    // Since 2.10.0, uses MongoClient
    /*MongoClient mongoClient = new MongoClient();
	DB db = mongoClient.getDB("database name");
	boolean auth = db.authenticate("username", "password".toCharArray());*/

    //Mongo db admin: admin
    //password: admin123
    //Der er ingen security
    public static void main(String[] args) {
        try {
            MongoClient mongoClient = new MongoClient("130.225.170.204", 27027);
            DB database = mongoClient.getDB("cphPlaygroundsDB");
            //  System.out.pri ntln(database.getName());
            //database.createCollection("customers", null);
            //boolean auth = database.authenticate("admin", "admin123".toCharArray());

          /*  DBCollection collection = database.getCollection("Playground");
            BasicDBObject document = new BasicDBObject();
            document.append("name", "vandlegepladsen");
            document.append("imagePath", "https://berlingske.bmcdn.dk/media/cache/resolve/embedded_image_600x/image/29/297771/17762859-vandlegepladsen1.jpg");
            document.append("toiletPosibilities", "false");
            document.append("streetName", "Fælledparken ved Edel Sauntes Allé");
            document.append("streetNumber", "1");
            document.append("commune", "København Ø");
            document.append("zipCode", "2100");
            collection.insert(document);*/
/*
            document = new BasicDBObject();
            playground = new PlaygroundDTODum(
                    "Trafiklegepladsen",
                    "https://berlingske.bmcdn.dk/media/cache/resolve/embedded_image_600x/image/29/297772/17762891-.jpg",
                    false,
                    "Gunnar Nu Hansens Plads 10,",
                    3,
                    "København Ø",
                    2100);
            document.append("playground", playground);
            collection.insert(document);*/

            System.out.println(database.getCollection("Playground")
            );

            /*List<String> dbs = mongoClient.getDatabaseNames();
            for (String db : dbs) {
                System.out.println(db);
            }*/


           /* BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("playground", "");
            DBCursor cursor = collection.find(searchQuery);
            while (cursor.hasNext()) {
                System.out.println(cursor.next());
            }*/



        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }


}
