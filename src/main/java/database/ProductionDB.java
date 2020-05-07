package database;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class ProductionDB implements IDataSource {
  private static final String PRODUCTION_DATABASE = "production";
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
    if (mongoClient == null)
      mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://s175565:qwe123@todoapp-cn8eq.mongodb.net/test"));

    return mongoClient;
  }

  @Override
  public DB getDatabase() {
    if (database == null)
      database = getClient().getDB(PRODUCTION_DATABASE);

    return database;
  }
}
