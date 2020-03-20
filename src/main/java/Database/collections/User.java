package Database.collections;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {

    private String id;
    private String name;
    private String status;
    private String imagePath;
    private String email;
    private String password;

    private List<PhoneNumber> phoneNumbers;
    private Set<Database.collections.Event> events =  new HashSet<>();
    private Playground playground;

    public User() {
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public User setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getImagePath() {
        return imagePath;
    }

    public User setImagePath(String photo_url) {
        this.imagePath = photo_url;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public User setPhoneNumbers(List<PhoneNumber> sections) {
        this.phoneNumbers = sections;
        return this;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public User setEvents(Set<Event> events) {
        this.events = events;
        return this;
    }


    public User setPlayground(Playground playground) {
        this.playground = playground;
        return this;
    }

    public String getId() {
        return id;
    }


    /*
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "user_playground",
            joinColumns = @JoinColumn(name = "playground_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<Playground> playgrounds =  new HashSet<>();
     */

   /* @ManyToMany(mappedBy = "connectedUsers", cascade = CascadeType.PERSIST)
    private Set<Playground> playgrounds = new HashSet<>();*/


   /* @ManyToMany(mappedBy = "assignedUsers", cascade = CascadeType.PERSIST)
    private Set<Event> events = new HashSet<>();
*/

    /* @OneToOne
        private Playground playground;
        // constructors, getters and setters...
    */
}
