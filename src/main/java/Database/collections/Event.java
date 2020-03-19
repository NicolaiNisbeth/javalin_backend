package Database.collections;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Event {

   /* @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", numOfParticipants=" + numOfParticipants +
                ", assignedUsers=" + assignedUsers +
                '}';
    }*/

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String name;
    private int numOfParticipants;

   /* @OneToMany(mappedBy = "event", cascade = CascadeType.PERSIST)
    private Set<User> assignedUsers = new HashSet<>();*/

    // constructors, getters and setters...

    public Event() {
    }

    public Event(String name) {
        this.name = name;
    }

    public String getEditorId() {
        return id;
    }

    public void setEditorId(String editorId) {
        this.id = editorId;
    }

    public String getEditorName() {
        return name;
    }

    public void setEditorName(String editorName) {
        this.name = editorName;
    }
/*
    public Set<User> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<User> assignedAuthors) {
        this.assignedUsers = assignedAuthors;
    }*/
}
