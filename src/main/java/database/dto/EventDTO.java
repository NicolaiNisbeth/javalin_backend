package database.dto;

import org.jetbrains.annotations.NotNull;
import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class EventDTO implements Comparable<EventDTO> {

    @MongoObjectId
    @MongoId
    private String id;
    private String name;
    private String imagepath;
    private int participants;
    private String description;
    private DetailsDTO detailsDTO;
    private Set<UserDTO> assignedUsers = new HashSet<>();  // many-to-many, One-Way-Embedding (an event has few Users, but User has many events)
    private String playground;                      // 1-to-many

    //This constructor is used for MongoDB mapping
    public EventDTO() {
    }

    private EventDTO(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.imagepath = builder.imagepath;
        this.participants = builder.participants;
        this.description = builder.description;
        this.detailsDTO = builder.detailsDTO;
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

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public int getParticipants() {
        return assignedUsers.size();
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

    public DetailsDTO getDetailsDTO() {
        return detailsDTO;
    }

    public void setDetailsDTO(DetailsDTO detailsDTO) {
        this.detailsDTO = detailsDTO;
    }

    public Set<UserDTO> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<UserDTO> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public String getPlaygroundName() {
        return playground;
    }

    public void setPlayground(String playground) {
        this.playground = playground;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EventDTO event = (EventDTO) o;
        return participants == event.participants &&
                Objects.equals(id, event.id) &&
                Objects.equals(name, event.name) &&
                Objects.equals(imagepath, event.imagepath) &&
                Objects.equals(description, event.description);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id='" + id + '\'' +
                ", title='" + name + '\'' +
                ", imagePath='" + imagepath + '\'' +
                ", participants=" + participants +
                ", description='" + description + '\'' +
                ", details=" + detailsDTO +
                ", assignedUsers=" + assignedUsers +
                ", playground=" + playground +
                '}';
    }

    @Override
    public int compareTo(@NotNull EventDTO event) {
        return this.detailsDTO.getDate().compareTo(event.getDetailsDTO().getDate());
    }

    public static class Builder {
        private String id;
        private String name;
        private String imagepath;
        private int participants;
        private String description;
        private DetailsDTO detailsDTO;
        private Set<UserDTO> assignedUsers = new HashSet<>();  // many-to-many, One-Way-Embedding (an event has few Users, but User has many events)
        private String playground;                      // 1-to-many

        public Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder imagePath(String imagepath) {
            this.imagepath = imagepath;
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

        public Builder details(DetailsDTO detailsModel) {
            this.detailsDTO = detailsModel;
            return this;
        }

        public Builder assignedUsers(Set<UserDTO> assignedUsers) {
            this.assignedUsers = assignedUsers;
            return this;
        }

        public Builder playground(String playground) {
            this.playground = playground;
            return this;
        }

        public EventDTO build() {
            return new EventDTO(this);
        }
    }
}