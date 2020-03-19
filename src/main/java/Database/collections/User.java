package Database.collections;


import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "User")
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String authorId;
    private String authorName;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "user_playground",
            joinColumns = @JoinColumn(name = "playground_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<Playground> playgrounds =  new HashSet<>();


   /* @ManyToMany(mappedBy = "connectedUsers", cascade = CascadeType.PERSIST)
    private Set<Playground> playgrounds = new HashSet<>();*/


   /* @ManyToMany(mappedBy = "assignedUsers", cascade = CascadeType.PERSIST)
    private Set<Event> events = new HashSet<>();
*/

    public Set<Playground> getPlaygrounds() {
        return playgrounds;
    }

    public void setPlaygrounds(Set<Playground> playgrounds) {
        this.playgrounds = playgrounds;
    }

   /* public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }
*/
    /* @OneToOne
        private Playground playground;
        // constructors, getters and setters...
    */

    // constructors, getters and setters...


    public User() {
    }

    public User(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    @Override
    public String toString() {
        return "User{" +
                "authorId='" + authorId + '\'' +
                ", authorName='" + authorName + '\'' +
                "\n, playground=" + playgrounds +
                '}';
    }

}
