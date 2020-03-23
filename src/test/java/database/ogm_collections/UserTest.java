/*
package database.ogm_collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.*;

import java.util.ArrayList;
import java.util.List;

class UserTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void insertUserAndRetrieveUser() throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ogm-mongodb");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();
        Bruger user = new Bruger()
                .setName("Nicolai")
                .setStatus("Admin")
                .setPassword("kode123")
                .email("nicolai.nisbeth@yahoo.com")
                .setImagePath("asddasdasd27a6sd78.jpg");

        List<PhoneNumber> numbers = new ArrayList<>();
        numbers.add(new PhoneNumber("+45 23 12 23 45"));
        numbers.add(new PhoneNumber("+45 23 45 12 32"));
        user.setPhoneNumbers(numbers);
        entityManager.persist(user);
        entityManager.getTransaction().commit();

        // get a new EM to make sure data is actually retrieved from the store and not Hibernate's internal cache
        entityManager.close();
        entityManager = entityManagerFactory.createEntityManager();

        // load it back
        entityManager.getTransaction().begin();

        Bruger loadedUser = entityManager.find( Bruger.class, user.get_id() );
        System.out.println(loadedUser);


        entityManager.getTransaction().commit();

        entityManager.close();


    }

}*/
