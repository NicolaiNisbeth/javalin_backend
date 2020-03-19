package Database.collections;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Event {



    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    private String name;
    private int numOfParticipants;

  /*  @OneToMany(mappedBy = "Event", cascade = CascadeType.PERSIST)
    private Set<User> assignedUsers = new HashSet<>();*/

    @OneToOne
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private User assignedUser;

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

    public int getNumOfParticipants() {
        return numOfParticipants;
    }

    public void setNumOfParticipants(int numOfParticipants) {
        this.numOfParticipants = numOfParticipants;
    }

    public User getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(User assignedUser) {
        this.assignedUser = assignedUser;
    }
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

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", numOfParticipants=" + numOfParticipants +
                "\n, assignedUsers=" + assignedUser +
                '}';
    }
}
