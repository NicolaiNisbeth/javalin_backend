/*
package Database;

import Database.ogm_collections.Event;
import Database.ogm_collections.Playground;
import Database.ogm_collections.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.TransactionManager;

class DataSourceMongoDBTest {


    @BeforeEach
    public void init() {

    }


    @Test
    public void addAndPrintPlayground() throws Exception {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ogm-mongodb");
        TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        Playground playground = new Playground();
        playground.setName("DTU");
        playground.setStreetName("Anker Engelunds Vej");
        playground.setStreetNumber(1);
        playground.setZipCode(2800);
        playground.setCommune("Kongens Lyngby");

        User user = new User();
        user.setAuthorName("Lars");
        playground.addUserToSet(user);
        User user2 = new User();
        user2.setAuthorName("Svend");
        playground.addUserToSet(user2);

        persistTestData(entityManagerFactory, playground);
        transactionManager.begin();
        Playground loadedEditor = entityManager.find(Playground.class, playground.getId());
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
        User user = new User();
        user.setAuthorName("Snapsak");
        //event.addUserToSet(user);
        persistTestData(entityManagerFactory, event);
        transactionManager.begin();
        Event loadedEditor = entityManager.find(Event.class, event.getEditorId());
        System.out.println(loadedEditor);
        //asset(loadedEditor).isNotNull();
// Other assertions to verify the entities and relations
    }

    @Test
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
    }

    private static void persistTestData(EntityManagerFactory entityManagerFactory, Object object)
            throws Exception {
        TransactionManager transactionManager = com.arjuna.ats.jta.TransactionManager.transactionManager();
        transactionManager.begin();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.persist(object);
        entityManager.close();
        transactionManager.commit();
    }

}*/
