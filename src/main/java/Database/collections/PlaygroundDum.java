package Database.collections;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class PlaygroundDum {
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

    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private User assignedUser;

    //@OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    //private Set<User> assignedUsers = new HashSet<>();

    public PlaygroundDum(String name, String imagePath, boolean toiletPossibilities, String streetName, int streetNumber, String commune, int zipCode) {
        this.name = name;
        this.imagePath = imagePath;
        this.toiletPossibilities = toiletPossibilities;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.commune = commune;
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "PlaygroundDum{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", toiletPossibilities=" + toiletPossibilities +
                ", streetName='" + streetName + '\'' +
                ", streetNumber=" + streetNumber +
                ", commune='" + commune + '\'' +
                ", zipCode=" + zipCode +
                ", assignedUser=" + assignedUser +
                '}';
    }

    public PlaygroundDum() {
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }

    /*    public Set<User> getAssignedUsers() {
            return assignedUsers;
        }

        public void setAssignedUsers(Set<User> assignedUsers) {
            this.assignedUsers = assignedUsers;
        }*/
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

    public boolean getIsToiletPossibilities() {
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
}
