package Database;

import Database.DTOs.PlaygroundDTODum;
import Database.collections.User;
import com.google.gson.Gson;
import com.mongodb.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;
import java.util.ArrayList;

public class DataSourceMongoDB {

    // Since 2.10.0, uses MongoClient
    /*MongoClient mongoClient = new MongoClient();
	DB db = mongoClient.getDB("database name");
	boolean auth = db.authenticate("username", "password".toCharArray());*/

    private static void persistTestData(EntityManagerFactory entityManagerFactory, User user)
            throws Exception {
        TransactionManager transactionManager =
                com.arjuna.ats.jta.TransactionManager.transactionManager();
        transactionManager.begin();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.persist(user);
        entityManager.close();
        transactionManager.commit();
    }

    //Mongo db admin: admin
    //password: admin123
    //Der er ingen security
    public static void main(String[] args) {
        try {
            MongoClient mongoClient = new MongoClient("130.225.170.204", 27027);
            DB database = mongoClient.getDB("cphPlaygroundsDB");
            Gson gson = new Gson();

            // srping hibernate mongodb
            // javalin mongo

          /*  EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ogm-mongodb");
            User user = new User("Hans");
            persistTestData(entityManagerFactory, user);
            TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
            transactionManager.begin();
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            User loadedEditor = entityManager.find(User.class, user.getAuthorId());
            System.out.println(loadedEditor.getAuthorName());
*/

            //  System.out.pri ntln(database.getName());
            //database.createCollection("customers", null);
            //boolean auth = database.authenticate("admin", "admin123".toCharArray());

            DBCollection collection = database.getCollection("playground");

            PlaygroundDTODum playground = new PlaygroundDTODum(
                    "wert",
                    "https://berlingske.bmcdn.dk/media/cache/resolve/embedded_image_600x/image/29/297772/17762891-.jpg",
                    true,
                    "Gunnar Nu Hansens Plads 10,",
                    3,
                    "København Ø",
                    2100);

            /*JSONObject rat = exRat.getJSONObject("fieldfromJson");
            String newrat = rat.toString();*/
            //Indsæt java objekt direkte i dm
           /* BasicDBObject doc = BasicDBObject.parse(gson.toJson(playground) );
            collection.insert(doc);*/
           // collection.insert(pojoToDoc(playground));

            Cursor cursor = collection.find();
            ArrayList<PlaygroundDTODum> playgrounds = new ArrayList<>();
            PlaygroundDTODum playgroundFromDB = null;

            while (cursor.hasNext()) {
                playgroundFromDB = gson.fromJson(cursor.next().toString(), PlaygroundDTODum.class);
                playgrounds.add(playgroundFromDB);
            }

            for (PlaygroundDTODum play: playgrounds) {
                System.out.println(play);
            }

            /*List<String> dbs = mongoClient.getDatabaseNames();
            for (String db : dbs) {
                System.out.println(db);
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BasicDBObject pojoToDoc(PlaygroundDTODum pojo) {
        BasicDBObject document = new BasicDBObject();
        document.append("name", pojo.getName());
        document.append("imagePath", pojo.getImagePath());
        document.append("toiletPosibilities", pojo.getIsToiletPossibilities());
        document.append("streetName", pojo.getStreetName());
        document.append("streetNumber", pojo.getStreetNumber());
        document.append("commune", pojo.getCommune());
        document.append("zipCode", pojo.getZipCode());
        return document;
    }
}
