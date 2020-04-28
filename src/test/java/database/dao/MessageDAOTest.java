package database.dao;

import com.mongodb.WriteResult;
import database.DALException;
import database.DataSource;
import database.NoModificationException;
import database.collections.Details;
import database.collections.Event;
import database.collections.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

class MessageDAOTest {
    static IMessageDAO messageDAO  = new MessageDAO(DataSource.getTestDB());

    @BeforeAll
    static void killAll(){
        messageDAO.deleteAllMessages();
    }

    @Test
    void createdMessageShouldBeFetchedMessage() throws NoModificationException {
        Message message = new Message.Builder()
                .setMessageString("Husk løbesko til fodbold")
                .build();

        WriteResult ws = messageDAO.createMessage(message);
        Message fetchedMessage = messageDAO.getMessage(ws.getUpsertedId().toString());
        Assertions.assertEquals(message, fetchedMessage);

        messageDAO.deleteMessage(ws.getUpsertedId().toString());
    }

    @Test
    void createTwoMessagesShouldFetchListSizeTwo() throws NoModificationException {
        Message message = new Message.Builder()
                .setMessageString("Husk løbesko til fodbold")
                .build();

        Message message2 = new Message.Builder()
                .setMessageString("Husk badebukser")
                .build();

        WriteResult wr = messageDAO.createMessage(message);
        WriteResult wr2 = messageDAO.createMessage(message2);

        List<Message> messageList = messageDAO.getMessageList();
        Assertions.assertAll(
                () -> Assertions.assertEquals(messageList.size(), 2),
                () -> Assertions.assertEquals(messageList.get(0), message),
                () -> Assertions.assertEquals(messageList.get(1), message2)
        );

        messageDAO.deleteMessage(wr.getUpsertedId().toString());
        messageDAO.deleteMessage(wr2.getUpsertedId().toString());
    }

    @Test
    void updateEventShouldFetchUpdatedEvent() throws NoModificationException{
        Message message = new Message.Builder()
                .setMessageString("Husk løbesko til fodbold")
                .build();

        WriteResult ws = messageDAO.createMessage(message);
        message.setMessageString("ny string");
        messageDAO.updateMessage(message);

        Message updatedMessage = messageDAO.getMessage(ws.getUpsertedId().toString());
        Assertions.assertEquals("ny string", updatedMessage.getMessageString());

        messageDAO.deleteMessage(ws.getUpsertedId().toString());
    }

    @Test
    void deleteAllMessagesInCollection() throws NoModificationException {
        Message message = new Message.Builder()
                .setMessageString("Husk løbesko til fodbold")
                .build();

        Message message2 = new Message.Builder()
                .setMessageString("Husk badebukser")
                .build();

        WriteResult wr = messageDAO.createMessage(message);
        WriteResult wr2 = messageDAO.createMessage(message2);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(messageDAO.getMessage(wr.getUpsertedId().toString())),
                () -> Assertions.assertNotNull(messageDAO.getMessage(wr2.getUpsertedId().toString()))
        );

        messageDAO.deleteAllMessages();
        Assertions.assertThrows(NoSuchElementException.class, () -> messageDAO.getMessageList());
    }

    @Test
    void nullInCreateShouldThrowIllegalArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> messageDAO.createMessage(null));
    }

    @Test
    void nullInGetShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> messageDAO.getMessage(null));
    }
    @Test
    void emptyIdInGetShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> messageDAO.getMessage(""));
    }

    @Test
    void noEventsInGetEventsShouldThrowNoSuchElements(){
        Assertions.assertThrows(NoSuchElementException.class, () -> messageDAO.getMessageList());
    }

    @Test
    void nullInUpdateShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> messageDAO.updateMessage(null));
    }

    @Test
    void nullInDeleteShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> messageDAO.deleteMessage(null));
    }

    @Test
    void emptyIdInDeleteShouldThrowIlleArgument(){
        Assertions.assertThrows(IllegalArgumentException.class, () -> messageDAO.deleteMessage(""));
    }

}