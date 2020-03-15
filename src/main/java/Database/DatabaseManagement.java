package Database;

import Database.DTOs.Employee;
import Database.DTOs.PlaygroundDTODum;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DatabaseManagement {
    private static SessionFactory factory;

    public static void build() {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void main(String[] args) {
        DatabaseManagement dbm = new DatabaseManagement();
        build();

/*
        PlaygroundDTO playground = new PlaygroundDTO(
                "Vandlegepladsen",
                new PlaygroundDTO.Address(
                        "Fælledparken ved Edel Sauntes Allé",
                        1,
                        "København Ø",
                        2100),
                "https://berlingske.bmcdn.dk/media/cache/resolve/embedded_image_600x/image/29/297771/17762859-vandlegepladsen1.jpg",
        false
        );

        dbm.addPlayground(playground);

        playground = new PlaygroundDTO(
                "Trafiklegepladsen",
                new PlaygroundDTO.Address(
                        "Gunnar Nu Hansens Plads 10,",
                        3,
                        "København Ø",
                        2100),
                "https://berlingske.bmcdn.dk/media/cache/resolve/embedded_image_600x/image/29/297772/17762891-.jpg",
                false
        );
*/

        PlaygroundDTODum playground = new PlaygroundDTODum(
                "Vandlegepladsen",
                "https://berlingske.bmcdn.dk/media/cache/resolve/embedded_image_600x/image/29/297771/17762859-vandlegepladsen1.jpg",
                false,
                "Fælledparken ved Edel Sauntes Allé",
                1,
                "København Ø",
                2100);

        dbm.addPlayground(playground);

        playground = new PlaygroundDTODum(
                "Trafiklegepladsen",
                "https://berlingske.bmcdn.dk/media/cache/resolve/embedded_image_600x/image/29/297772/17762891-.jpg",
                false,
                "Gunnar Nu Hansens Plads 10,",
                3,
                "København Ø",
                2100);

        dbm.addPlayground(playground);

        /* List down all the employees */
        dbm.listPlaygrounds();
        /*
         *//* Update employee's records *//*
        dbm.updatePlayground(empID1, 5000);

        *//* Delete an employee from the database *//*
        dbm.deletePlayground(empID2);*/


    }

    /* Method to CREATE an employee in the database */
    public Integer addPlayground(PlaygroundDTODum playground) {
        Session session = factory.openSession();
        Transaction tx = null;
        Integer playgroundID = null;

        try {
            tx = session.beginTransaction();
            //Employee employee = new Employee(fname, lname, salary);
            playgroundID = (Integer) session.save(playground);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return playgroundID;
    }

    /* Method to  READ all the employees */
    public static void listPlaygrounds() {
        build();
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            List playgrounds = session.createQuery("FROM Database.DTOs.PlaygroundDTODum").list();
            for (Iterator iterator = playgrounds.iterator(); iterator.hasNext(); ) {
                PlaygroundDTODum playground = (PlaygroundDTODum) iterator.next();
                System.out.print("Name: " + playground.name);
                System.out.print("Street: " + playground.streetName);
                System.out.println("Toilet possibilities: " + playground.toiletPossibilities);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    public static ArrayList<PlaygroundDTODum> getPlaygrounds() {
        build();
        Session session = factory.openSession();
        Transaction tx = null;
        ArrayList playgrounds = null;
        try {
            tx = session.beginTransaction();
            playgrounds = (ArrayList) session.createQuery("FROM Database.DTOs.PlaygroundDTODum").list();
            for (Iterator iterator = playgrounds.iterator(); iterator.hasNext(); ) {
                PlaygroundDTODum playground = (PlaygroundDTODum) iterator.next();
                System.out.print("Name: " + playground.name);
                System.out.print("Street: " + playground.streetName);
                System.out.println("Toilet possibilities: " + playground.toiletPossibilities);
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return playgrounds;
    }

    /* Method to UPDATE salary for an employee */
    public void updatePlayground(Integer EmployeeID, int salary) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Employee employee = (Employee) session.get(Employee.class, EmployeeID);
            employee.setSalary(salary);
            session.update(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /* Method to DELETE an employee from the records */
    public void deletePlayground(Integer EmployeeID) {
        Session session = factory.openSession();
        Transaction tx = null;

        try {
            tx = session.beginTransaction();
            Employee employee = (Employee) session.get(Employee.class, EmployeeID);
            session.delete(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
