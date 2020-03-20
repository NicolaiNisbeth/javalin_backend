package Database.collections;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;


public class Event implements Comparable<Event>{

    private String id;
    private String name;
    private String imagePath;
    private int participants;
    private String description;
    private Details details;
    private Set<User> assignedUsers = new HashSet<>();
    private Playground playground;

    private Event(Builder builder){
        this.id = builder.id;
        this.name = builder.name;
        this.imagePath = builder.imagePath;
        this.participants = builder.participants;
        this.description = builder.description;
        this.details = builder.details;
        this.assignedUsers = builder.assignedUsers;
        this.playground = builder.playground;
    }

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
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", title='" + name + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", participants=" + participants +
                ", description='" + description + '\'' +
                ", details=" + details +
                ", assignedUsers=" + assignedUsers +
                ", playground=" + playground +
                '}';
    }

    @Override
    public int compareTo(@NotNull Event event) {
        return this.details.getDate().compareTo(event.getDetails().getDate());
    }

    public static class Builder {
        private String id;
        private String name;
        private String imagePath;
        private int participants;
        private String description;
        private Details details;
        private Set<User> assignedUsers;
        private Playground playground;

        public Builder(String name){
            this.name = name;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder participants(int participants) {
            this.participants = participants;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder details(Details details) {
            this.details = details;
            return this;
        }

        public Builder assignedUsers(Set<User> assignedUsers) {
            this.assignedUsers = assignedUsers;
            return this;
        }

        public Builder playground(Playground playground) {
            this.playground = playground;
            return this;
        }

        public Event build(){
            return new Event(this);
        }
    }
}