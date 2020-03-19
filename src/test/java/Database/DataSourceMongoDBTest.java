package Database;

import Database.collections.User;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.TransactionManager;

import static org.junit.jupiter.api.Assertions.*;

class DataSourceMongoDBTest {


    @Test
    public void givenMongoDB_WhenEntitiesCreated_thenCanBeRetrieved() throws Exception {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ogm-mongodb");
        User user = new User("Hans");
        persistTestData(entityManagerFactory, user);
        TransactionManager transactionManager =
                com.arjuna.ats.jta.TransactionManager.transactionManager();
        transactionManager.begin();
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        User loadedEditor = entityManager.find(User.class, user.getAuthorId());
        System.out.println(loadedEditor.getAuthorName());
        System.out.println(loadedEditor.getAuthorId());
        //asset(loadedEditor).isNotNull();
// Other assertions to verify the entities and relations
    }

    private static void persistTestData(EntityManagerFactory entityManagerFactory, User user)
            throws Exception {
        TransactionManager transactionManager =
                com.arjuna.ats.jta.TransactionManager.transactionManager();
        transactionManager.begin();
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.persist(user);
        entityManager.close();
        transactionManager.commit();
    }

}