package database;


import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

import java.util.Collections;

public interface IDataSource {
    MongoClient getClient();
    DB getDatabase();
}
