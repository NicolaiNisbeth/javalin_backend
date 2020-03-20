package Database.collections;

import org.hibernate.annotations.GenericGenerator;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


public class Event implements Comparable<Event>{

    private String id;
    private String title;
    private String imagePath;
    private int participants;
    private String description;
    private Details details;
    private Set<User> assignedUsers = new HashSet<>();
    private Playground playground;

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", participants=" + participants +
                ", description='" + description + '\'' +
                ", details=" + details +
                ", assignedUsers=" + assignedUsers +
                ", playground=" + playground +
                '}';
    }

    public Event(){}

    public Event(String title, String photoUrl, int participants, String description, Details details){
        this.title = title;
        this.imagePath = photoUrl;
        this.participants = participants;
        this.description = description;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String photo_url) {
        this.imagePath = photo_url;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    public Set<User> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public Playground getPlayground() {
        return playground;
    }

    public void setPlayground(Playground playground) {
        this.playground = playground;
    }

    @Override
    public int compareTo(@NotNull Event event) {
        return this.details.getDate().compareTo(event.getDetails().getDate());
    }


}
