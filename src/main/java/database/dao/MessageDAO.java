package database.dao;

import database.DALException;
import database.DataSource;
import database.collections.Message;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.List;

import static org.jongo.Oid.withOid;

public class MessageDAO implements IMessageDAO {
    private final String COLLECTION = IMessageDAO.COLLECTION;
    private String MongoQueryTag = "_id";

    @Override
    public WriteResult createMessage(Message message) throws DALException {
        if (message == null)
            throw new DALException(String.format("Can't create message in %s collection when message is null", COLLECTION));

        Jongo jongo = new Jongo(DataSource.getDB());
        WriteResult result = jongo.getCollection(COLLECTION).save(message);

        if (result.getN() == 0)
            throw new DALException(String.format("Message can't be created in %s collection", COLLECTION));

        return result;
    }

    @Override
    public Message getMessage(String id) throws DALException {
        if (id == null)
            throw new DALException(String.format("Can't get message in %s collection when id is null", COLLECTION));

        Jongo jongo = new Jongo(DataSource.getDB());
        Message message = jongo.getCollection(COLLECTION).findOne(withOid(id)).as(Message.class);

        if (message == null)
            throw new DALException(String.format("No message in %s collection with id %s", COLLECTION, id));

        return message;
    }

    @Override
    public List<Message> getMessageList() throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());
        MongoCursor<Message> all = jongo.getCollection(COLLECTION).find("{}").as(Message.class);

        List<Message> messages = new ArrayList<>();
        while (all.hasNext()) {
            messages.add(all.next());
        }

        if (messages.isEmpty())
            throw new DALException(String.format("No messages in %s collection", COLLECTION));

        return messages;
    }

    @Override
    public boolean updateMessage(Message message) throws DALException {
        if (message == null)
            throw new DALException(String.format("Can't update message in %s collection when message is null", COLLECTION));

        if (message.getId() == null)
            throw new DALException(String.format("Can't find message to be updated in %s collection when id is null", COLLECTION));

        Jongo jongo = new Jongo(DataSource.getDB());
        WriteResult result = jongo.getCollection(COLLECTION).save(message);
        System.out.println(result);

        if (!result.wasAcknowledged())
            throw new DALException(String.format("Message can't be updated in %s collection", COLLECTION));

        return true;
    }

    @Override
    public boolean deleteMessage(String id) throws DALException {
        Jongo jongo = new Jongo(DataSource.getDB());

        boolean isMessageDeleted = jongo
                .getCollection(COLLECTION)
                .remove(new ObjectId(id))
                .wasAcknowledged();

        if (!isMessageDeleted)
            throw new DALException(String.format("No message in %s collection with id %s", COLLECTION, id));

        return true;

    }

    @Override
    public boolean deleteAllMessages() {
        Jongo jongo = new Jongo(DataSource.getDB());
        try {
            System.out.println(jongo.getCollection(COLLECTION)
                    .remove("{}"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
