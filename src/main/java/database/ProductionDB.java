package database;

import com.mongodb.*;

public class ProductionDB implements IDataSource {

  private static final String HOST = "18.185.121.182";
  private static final int PORT = 27017;
  private static final String DATABASE_NAME = "cphPlaygroundsDB";
  private static final String user = "myAdmin"; // the user name
  private static final String adminDatabase = "admin"; // the name of the database in which the user is defined
  private static final char[] password = ("njl_nykode").toCharArray(); // the setPassword as a character array
  private static ProductionDB instance;
  private static DB database;
  private static MongoClient mongoClient;

  private ProductionDB() {
  }

  public static ProductionDB getInstance() {
    if (instance == null)
      instance = new ProductionDB();

    return instance;
  }

  @Override
  public MongoClient getClient() {
    if (mongoClient == null) {
      MongoCredential credential = MongoCredential.createCredential(user, adminDatabase, password);
      mongoClient = new MongoClient(new ServerAddress(HOST, PORT), credential, MongoClientOptions.builder().build());
    }
    return mongoClient;
  }

  @Override
  public DB getDatabase() {
    if (database == null) {
      database = getClient().getDB(DATABASE_NAME);
    }
    return database;
  }
}
