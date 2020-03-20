package Database.collections;


import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(generator="uuid")
    @GenericGenerator(name="uuid", strategy="uuid2")
    private String id;

    private String name;
    private String status;
    private String imagePath;
    private String email;
    private String password;

    @ElementCollection
    private List<PhoneNumber> phoneNumbers;

    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name="users_events",
               joinColumns=@JoinColumn(name="event_id"),
               inverseJoinColumns=@JoinColumn(name="user_id"))
    private Set<Event> events =  new HashSet<>();

    @ManyToOne
    private Playground playground;

    public User() {
    }

    public User(String name, String status, String imagePath, String email, String password, List<PhoneNumber> phoneNumbers){
        this.name = name;
        this.status = status;
        this.imagePath = imagePath;
        this.email = email;
        this.password = password;
        this.phoneNumbers = phoneNumbers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String photo_url) {
        this.imagePath = photo_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> sections) {
        this.phoneNumbers = sections;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Playground getPlayground() {
        return playground;
    }

    public void setPlayground(Playground playground) {
        this.playground = playground;
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
