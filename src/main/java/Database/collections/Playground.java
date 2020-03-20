package Database.collections;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name="playgrounds")
public class Playground {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;
    private String imagePath;
    private boolean toiletPossibilities;
    private String streetName;
    private int streetNumber;
    private String commune;
    private int zipCode;

    @OneToMany(mappedBy="playground", cascade=CascadeType.PERSIST)
    private Set<User> assignedUsers = new HashSet<>();

    @OneToMany(mappedBy="playground", cascade=CascadeType.PERSIST)
    private Set<Event> futureEvents = new TreeSet<>();

    public Playground(){ }

    public Playground(String name, String imagePath, boolean toiletPossibilities, String streetName, int streetNumber, String commune, int zipCode) {
        this.name = name;
        this.imagePath = imagePath;
        this.toiletPossibilities = toiletPossibilities;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.commune = commune;
        this.zipCode = zipCode;
    }

   /* @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private User assignedUser;*/

    //@ManyToMany(mappedBy = "playgrounds", cascade = CascadeType.PERSIST)
    //private Set<User> connectedUsers = new HashSet<>();

    //@OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    //private Set<User> assignedUsers = new HashSet<>();
/*
    @Override
    public String toString() {
        for (User user : connectedUsers) {
            System.out.println(user);
        }

        return "Playground{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", toiletPossibilities=" + toiletPossibilities +
                ", streetName='" + streetName + '\'' +
                ", streetNumber=" + streetNumber +
                ", commune='" + commune + '\'' +
                ", zipCode=" + zipCode +
                "\n, assignedUser=" + connectedUsers +
                '}';
    }

 */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isToiletPossibilities() {
        return toiletPossibilities;
    }

    public void setToiletPossibilities(boolean toiletPossibilities) {
        this.toiletPossibilities = toiletPossibilities;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public Set<User> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public Set<Event> getFutureEvents() {
        return futureEvents;
    }

    public void setFutureEvents(Set<Event> futureEvents) {
        this.futureEvents = futureEvents;
    }
}
