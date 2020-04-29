package database.utils;

import com.mongodb.WriteResult;
import database.NoModificationException;
import org.jongo.MongoCollection;

public class QueryUtils {
    public static WriteResult updateWithPullObject(MongoCollection collection, String updateKey, Object updateValue,
                                                   String withKey, String withField, Object withValue) throws NoModificationException {
        WriteResult wr = collection
                .update("{# : #}", updateKey, updateValue)
                .with("{$pull : {# : {# : #}}}, {multi : true}", withKey, withField, withValue);

        if (wr.getN() == 0 || !wr.isUpdateOfExisting())
            throw new NoModificationException(
                    String.format("%s in %s was not updated with pull: %s", withKey, collection.getName(), withValue));

        return wr;
    }

    public static WriteResult updateWithPullSimple(MongoCollection collection, String updateKey, Object updateValue,
                                                   String withKey, Object withValue) throws NoModificationException {
        WriteResult wr = collection
                .update("{# : #}", updateKey, updateValue)
                .with("{$pull : {# : #}}", withKey, withValue);

        if (wr.getN() == 0 || !wr.isUpdateOfExisting())
            throw new NoModificationException(
                    String.format("%s in %s was not updated with pull: %s", withKey, collection.getName(), withValue));

        return wr;
    }

    public static WriteResult updateWithPush(MongoCollection collection, String updateKey, Object updateValue,
                                             String withKey, Object withValue) throws NoModificationException {
        WriteResult wr = collection
                .update("{# : #}", updateKey, updateValue)
                .with("{$push : {# : #}}", withKey, withValue);

        if (wr.getN() == 0 || !wr.isUpdateOfExisting())
            throw new NoModificationException(
                    String.format("%s in %s was not updated with push: %s", withKey, collection.getName(), withValue));

        return wr;

    }

}
