package Database;

import Database.DTOs.PlaygroundDTODum;
import Database.collections.Event;
import Database.collections.PlaygroundDum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.TransactionManager;

import static org.junit.jupiter.api.Assertions.*;

class DataSourceMongoDBTest {


    @BeforeEach
    public void init() {

    }


    @Test
    public void addAndPrintPlayground() throws Exception {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ogm-mongodb");
        TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        PlaygroundDum playground = new PlaygroundDum();
        playground.name = "DTU";
        playground.streetName = "Anker Engelunds Vej";
        playground.streetNumber = 1;
        playground.zipCode = 2800;
        playground.commune = "Kongens Lyngby";
        persistTestData(entityManagerFactory, playground);
        transactionManager.begin();
        PlaygroundDum loadedEditor = entityManager.find(PlaygroundDum.class, playground.id);
        System.out.println(loadedEditor.toString());
        //asset(loadedEditor).isNotNull();
// Other assertions to verify the entities and relations
    }

    @Test
    public void addAndPrintEvent() throws Exception {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ogm-mongodb");
        TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Event event = new Event("Fodbold");
        persistTestData(entityManagerFactory, event);
        transactionManager.begin();
        Event loadedEditor = entityManager.find(Event.class, event.getEditorId());
        System.out.println(loadedEditor.getEditorName());
        //asset(loadedEditor).isNotNull();
// Other assertions to verify the entities and relations
    }

   /* @Test
    public void addAndPrintUser() throws Exception {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ogm-mongodb");
        TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        User user = new User("Hans");
        persistTestData(entityManagerFactory, user);
        transactionManager.begin();
        User loadedEditor = entityManager.find(User.class, user.getAuthorId());
        System.out.println(loadedEditor.getAuthorName());
        System.out.println(loadedEditor.getAuthorId());
        //asset(loadedEditor).isNotNull();
// Other assertions to verify the entities and relations
    }*/

    private static void persistTestData(EntityManagerFactory entityManagerFactory, Object object)
            throws Exception {
        TransactionManager transactionManager =
                com.arjuna.ats.jta.TransactionManager.transactionManager();
        transactionManager.begin();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.persist(object);
        entityManager.close();
        transactionManager.commit();
    }

}