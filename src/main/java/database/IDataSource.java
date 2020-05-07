package database;


import com.mongodb.DB;
import com.mongodb.MongoClient;

public interface IDataSource {
  MongoClient getClient();

  DB getDatabase();
}
