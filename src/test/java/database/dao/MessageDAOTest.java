package database.dao;

import database.DALException;
import database.collections.Message;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MessageDAOTest {
    static MessageDAO messageDAO;

    @BeforeAll
    public static void init(){
        messageDAO = new MessageDAO();
    }

    @Test
    void createMessage() throws DALException {
        Message message = new Message.Builder()
                .setMessageString("Husk løbesko til fodbold")
                .build();
        messageDAO.createMessage(message);
    }

    @Test
    void getMessage() throws DALException {
        System.out.println(messageDAO.getMessage("5e752c8452ab03398d28d262"));
    }

    @Test
    void getMessageList() throws DALException {
        for (Message playground : messageDAO.getMessageList()) {
            System.out.println(playground);
    }}

    @Test
    void updateMessage() throws DALException {
        Message playground = messageDAO.getMessage("5e752c8452ab03398d28d262");
        System.out.println(playground);
        playground.setCategory("Ny cat");
        playground.setMessageString("Husk nu at slå det toiletbræt ned, tak!");
        messageDAO.updateMessage(playground);
        System.out.println(messageDAO.getMessage("5e752c8452ab03398d28d262"));

    }

    @Test
    void deleteMessage() throws DALException {
        messageDAO.deleteMessage("5e7500a29c55065cb293b635");

    }
}