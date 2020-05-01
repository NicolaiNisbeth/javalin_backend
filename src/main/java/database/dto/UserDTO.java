package database.dto;


import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class UserDTO implements Serializable {
    private static final long serialVersionUID = 12233;

    @MongoObjectId
    @MongoId
    private String id;
    private String firstname;
    private String lastname;
    private String status;
    private String imagePath;
    private String email;
    private String password;
    private String username;
    private String website;
    private boolean loggedIn;
    private String[] phonenumbers;
    private Set<EventDTO> events = new HashSet<>();    // many-to-many, One-Way-Embedding (an event has few Users, but User has many events)
    private Set<String> playgroundsIDs = new HashSet<>();


    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }


    public Set<String> getPlaygroundsIDs() {
        return playgroundsIDs;
    }

    public void setPlaygroundsIDs(Set<String> playgroundsIDs) {
        this.playgroundsIDs = playgroundsIDs;
    }


    //This constructor is used for MongoDB mapping
    private UserDTO() {
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    private UserDTO(Builder builder) {
        this.id = builder.id;
        this.firstname = builder.firstname;
        this.lastname = builder.lastname;
        this.username = builder.username;
        this.status = builder.status;
        this.imagePath = builder.imagePath;
        this.email = builder.email;
        this.password = builder.password;
        this.phonenumbers = builder.phonenumbers;
        this.events = builder.events;
        this.playgroundsIDs = builder.playgroundsIDs;
        this.website = builder.website;
        this.loggedIn = builder.loggedIn;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImagepath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
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

    public String[] getPhonenumbers() {
        return phonenumbers;
    }

    public void setPhoneNumbers(String[] phonenumbers) {
        this.phonenumbers = phonenumbers;
    }

    public Set<EventDTO> getEvents() {
        return events;
    }

    public void setEvents(Set<EventDTO> events) {
        this.events = events;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO user = (UserDTO) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(firstname, user.firstname) &&
                Objects.equals(status, user.status) &&
                Objects.equals(imagePath, user.imagePath) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Arrays.equals(phonenumbers, user.phonenumbers);
    }


    @Override
    public String toString() {
        return "Bruger{" +
                "id " + id +
                ", name='" + firstname + " " + lastname + " " + '\'' +
                ", username='" + username + '\'' +
                ", status='" + status + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", setEmail='" + email + '\'' +
                ", setPassword='" + password + '\'' +
                ", phoneNumbers=" + Arrays.toString(phonenumbers) +
                ", events=" + events +
                '}';
    }

    public static class Builder {
        public boolean loggedIn;
        private String username;
        private String status;
        private String imagePath;
        private String email;
        private String password;
        private String[] phonenumbers;
        private Set<EventDTO> events = new HashSet<>();
        private Set<String> playgroundsIDs = new HashSet<>();
        private String firstname;
        private String lastname;
        private String id;
        private String website;

        public Builder(String username) {
            this.username = username;
        }

        public boolean isLoggedIn() {
            return loggedIn;
        }

        public Builder setLoggedIn(boolean loggedIn) {
            this.loggedIn = loggedIn;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Set<String> getPlaygroundsIDs() {
            return playgroundsIDs;
        }

        public void setPlaygroundsIDs(Set<String> playgroundsIDs) {
            this.playgroundsIDs = playgroundsIDs;
        }

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public String getStatus() {
            return status;
        }

        public Builder setStatus(String status) {
            this.status = status;
            return this;

        }

        public String getImagePath() {
            return imagePath;
        }

        public Builder setImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;

        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String[] getPhonenumbers() {
            return phonenumbers;
        }

        public Builder setPhonenumbers(String[] phonenumbers) {
            this.phonenumbers = phonenumbers;
            return this;

        }

        public Set<EventDTO> getEvents() {
            return events;
        }

        public Builder setEvents(Set<EventDTO> events) {
            this.events = events;
            return this;

        }

        public Set<String> getPlaygroundID2() {
            return playgroundsIDs;
        }

        public String getId() {
            return id;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;

        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder phoneNumbers(String... phoneNumbers) {
            this.phonenumbers = phoneNumbers;
            return this;
        }

        public Builder events(Set<EventDTO> events) {
            this.events = events;
            return this;
        }

        public String getUsername() {
            return username;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public String getFirstname() {
            return firstname;
        }

        public Builder setFirstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public String getLastname() {
            return lastname;
        }

        public Builder setLastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(this);
        }

        public String getWebsite() {
            return website;
        }

        public Builder setWebsite(String website) {
            this.website = website;
            return this;
        }
    }
}