package database.dao;

import database.IDataSource;
import database.exceptions.NoModificationException;
import database.dto.MessageDTO;
import com.mongodb.WriteResult;
import org.bson.types.ObjectId;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.jongo.Oid.withOid;

public class MessageDAO implements IMessageDAO {

    private IDataSource datasource;

    public MessageDAO(IDataSource datasource) {
        this.datasource = datasource;
    }

    /**
     * Create message in db
     * @param message to be created
     * @return writeResult where upsertedId can be derived
     * @throws IllegalArgumentException when message is null
     * @throws NoModificationException when message is not created
     */
    @Override
    public WriteResult createMessage(MessageDTO message) throws IllegalArgumentException, NoModificationException {
        if (message == null)
            throw new IllegalArgumentException(
                    String.format("Can't create message in %s collection when message is null", COLLECTION));

        Jongo jongo = new Jongo(datasource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        WriteResult wr = collection.save(message);

        if (wr.getN() == 0)
            throw new NoModificationException(
                    String.format("Message can't be created in %s collection", COLLECTION));

        return wr;
    }

    /**
     * Get message in db
     * @param id uniquely identifies a message in db
     * @return message with given id
     * @throws IllegalArgumentException when id is invalid
     * @throws NoSuchElementException when message is not found in db
     */
    @Override
    public MessageDTO getMessage(String id) throws IllegalArgumentException, NoSuchElementException {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException(
                    String.format("%s as ID is not valid in identifying an message", id));

        Jongo jongo = new Jongo(datasource.getDatabase());
        MessageDTO message = jongo.getCollection(COLLECTION).findOne(withOid(id)).as(MessageDTO.class);

        if (message == null)
            throw new NoSuchElementException(
                    String.format("No message in %s collection with id %s", COLLECTION, id));

        return message;
    }

    /**
     * Get list of all messages in db
     * @return list of messages
     * @throws NoSuchElementException when no messages are found in db
     */
    @Override
    public List<MessageDTO> getMessageList() throws NoSuchElementException {
        Jongo jongo = new Jongo(datasource.getDatabase());
        MongoCursor<MessageDTO> all = jongo.getCollection(COLLECTION).find("{}").as(MessageDTO.class);
        List<MessageDTO> messages = new ArrayList<>();
        while (all.hasNext()) {
            messages.add(all.next());
        }

        if (messages.isEmpty())
            throw new NoSuchElementException(
                    String.format("No messages in %s collection", COLLECTION));

        return messages;
    }

    /**
     * Update message in db
     * @param message with updated values
     * @return writeResult where id of updated message can be derived
     * @throws IllegalArgumentException when message is null
     * @throws NoModificationException when no message is updated
     */
    @Override
    public WriteResult updateMessage(MessageDTO message) throws IllegalArgumentException, NoModificationException {
        if (message == null || message.getID() == null)
            throw new IllegalArgumentException(
                    String.format("Can't update message in %s collection when param is null", COLLECTION));

        Jongo jongo = new Jongo(datasource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        WriteResult wr = collection
                .update(new ObjectId(message.getID()))
                .with(message);

        if (!wr.wasAcknowledged())
            throw new NoModificationException(
                    String.format("Event in %s collection with id %s was not updated", COLLECTION, message.getID()));

        return wr;
    }

    /**
     * Delete message in db
     * @param id uniquely identifies a message in db
     * @return writeResult whre id of deleted user can be derived
     * @throws IllegalArgumentException when no message is deleted
     * @throws NoModificationException  when id is invalid
     */
    @Override
    public WriteResult deleteMessage(String id) throws IllegalArgumentException, NoModificationException {
        if (id == null || id.isEmpty())
            throw new IllegalArgumentException(
                    String.format("%s as ID is not valid in identifying an message", id));

        Jongo jongo = new Jongo(datasource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        WriteResult wr = collection.remove(new ObjectId(id));

        if (!wr.wasAcknowledged())
            throw new NoModificationException(
                    String.format("No message in %s collection with id %s was deleted", COLLECTION, id));

        return wr;

    }

    /**
     * Delete all messages in db
     * @return writeResult where ids of deleted messages can be derived
     */
    @Override
    public WriteResult deleteAllMessages() {
        Jongo jongo = new Jongo(datasource.getDatabase());
        MongoCollection collection = jongo.getCollection(COLLECTION);
        return collection.remove("{}");
    }

    @Override
    public void setDataSource(IDataSource dataSource) {
        this.datasource = dataSource;
    }
}
