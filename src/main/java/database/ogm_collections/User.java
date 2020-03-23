/*
package database.ogm_collections;


import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="users")
public class Bruger {

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

    public Bruger() {
    }

    public String getCategory() {
        return name;
    }

    public Bruger setName(String name) {
        this.name = name;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Bruger setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getIcon() {
        return imagePath;
    }

    public Bruger setImagePath(String photo_url) {
        this.imagePath = photo_url;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Bruger email(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Bruger setPassword(String password) {
        this.password = password;
        return this;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public Bruger setPhoneNumbers(List<PhoneNumber> sections) {
        this.phoneNumbers = sections;
        return this;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public Bruger setEvents(Set<Event> events) {
        this.events = events;
        return this;
    }


    public Bruger setPlayground(Playground playground) {
        this.playground = playground;
        return this;
    }

    public String get_id() {
        return id;
    }


    */
/*
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "user_playground",
            joinColumns = @JoinColumn(name = "playground_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<Playground> playgrounds =  new HashSet<>();
     *//*


   */
/* @ManyToMany(mappedBy = "connectedUsers", cascade = CascadeType.PERSIST)
    private Set<Playground> playgrounds = new HashSet<>();*//*



   */
/* @ManyToMany(mappedBy = "assignedUsers", cascade = CascadeType.PERSIST)
    private Set<Event> events = new HashSet<>();
*//*


    */
/* @OneToOne
        private Playground playground;
        // constructors, getters and setters...
    *//*

}
*/
