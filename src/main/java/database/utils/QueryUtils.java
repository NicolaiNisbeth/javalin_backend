package database.utils;

import com.mongodb.WriteResult;
import database.DALException;
import org.jongo.MongoCollection;

public class QueryUtils {
    public static void updateWithPullObject(MongoCollection collection, String updateKey, Object updateValue, String withKey, String withField, Object withValue) throws DALException {
        WriteResult ws = collection
                .update("{# : #}", updateKey, updateValue)
                .with("{$pull : {# : {# : #}}}, {multi : true}", withKey, withField, withValue);

        if (ws.getN() == 0 || !ws.isUpdateOfExisting())
            throw new DALException(String.format("%s in %s was not updated with pull: %s", withKey, collection.getName(), withValue));
    }

    public static void updateWithPullSimple(MongoCollection collection, String updateKey, Object updateValue, String withKey, Object withValue) throws DALException {
        WriteResult ws = collection
                .update("{# : #}", updateKey, updateValue)
                .with("{$pull : {# : #}}", withKey, withValue);

        if (ws.getN() == 0 || !ws.isUpdateOfExisting())
            throw new DALException(String.format("%s in %s was not updated with pull: %s", withKey, collection.getName(), withValue));
    }

    public static void updateWithPush(MongoCollection collection, String updateKey, Object updateValue, String withKey, Object withValue) throws DALException {
        WriteResult ws = collection
                .update("{# : #}", updateKey, updateValue)
                .with("{$push : {# : #}}", withKey, withValue);

        if (ws.getN() == 0 || !ws.isUpdateOfExisting())
            throw new DALException(String.format("%s in %s was not updated with push: %s", withKey, collection.getName(), withValue));

    }

}
