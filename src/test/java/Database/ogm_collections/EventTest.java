/*
package Database.ogm_collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class EventTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void insertEventAndRetrieveEvent() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ogm-mongodb");
        TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        transactionManager.begin();

        Details details1 = new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()));
        Details details2 = new Details(new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()));
        Event event1 = new Event("Football", "2k3j2k323.jpg", 20, "Football near the bay...", details1);
        Event event2 = new Event("Boardgames", "a9sd8sa9d89as8d.jpg", 3, "Boardgames near the bay...", details2);

        List<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber("+45 23 12 23 45"));
        phoneNumbers.add(new PhoneNumber("+45 23 45 12 32"));
        User user1 = new User()
                .setName("Nicolai")
                .setStatus("Admin")
                .setPassword("qwe123")
                .setEmail("nicolai.nisbeth@yahoo.com")
                .setImagePath("asdasd7asdas98da.jpg")
                .setPhoneNumbers(phoneNumbers);

        List<PhoneNumber> phoneNumbers2 = new ArrayList<>();
        phoneNumbers2.add(new PhoneNumber("+45 89 76 78 34"));
        phoneNumbers2.add(new PhoneNumber("+45 75 23 12 32"));
        User user2 = new User()
                .setName("Peter")
                .setStatus("Admin")
                .setPassword("lksædjkf23")
                .setEmail("awq.pavlidi@google.com")
                .setImagePath("asdas33d732asddadds98da.jpg")
                .setPhoneNumbers(phoneNumbers2);

        event1.getAssignedUsers().add(user1);
        event1.getAssignedUsers().add(user2);
        user1.getEvents().add(event1);
        user2.getEvents().add(event1);

        event2.getAssignedUsers().add(user1);
        user1.getEvents().add(event2);



        entityManager.persist(event1);
        entityManager.persist(event2);

        transactionManager.commit();

        // get a new EM to make sure data is actually retrieved from the store and not Hibernate's internal cache
        entityManager.close();
        entityManager = entityManagerFactory.createEntityManager();

        // load it back
        entityManager.getTransaction().begin();

        Event loadedEvent = entityManager.find(Event.class, event1.getId());
        Event loadedEvent2 = entityManager.find(Event.class, event2.getId());

        User loadedUser = entityManager.find(User.class, user1.getId());
        User loadedUser2 = entityManager.find(User.class, user2.getId());



        entityManager.getTransaction().commit();
        entityManager.close();


    }
}*/
