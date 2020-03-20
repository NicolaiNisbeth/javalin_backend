package Database.collections;





import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User {

    @MongoObjectId
    @MongoId
    private String id;
    private String name;
    private String status;
    private String imagepath;
    private String email;
    private String password;

    private String[] phonenumbers;
    private Set<Event> events = new HashSet<>();
    private Playground playground;

    //This constructor is used for MongoDB mapping
    private User(){}

    private User(Builder builder) {
        this.name = builder.name;
        this.status = builder.status;
        this.imagepath = builder.imagePath;
        this.email = builder.email;
        this.password = builder.password;
        this.phonenumbers = builder.phonenumbers;
        this.events = builder.events;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
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

    public void setPhonenumbers(String[] phonenumbers) {
        this.phonenumbers = phonenumbers;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(name, user.name) &&
                Objects.equals(status, user.status) &&
                Objects.equals(imagepath, user.imagepath) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Arrays.equals(phonenumbers, user.phonenumbers);
    }


    @Override
    public String toString() {
        return "User{" +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", imagePath='" + imagepath + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumbers=" + Arrays.toString(phonenumbers) +
                ", events=" + events +
                ", playground=" + playground +
                '}';
    }

    public static class Builder {
        private String name;
        private String status;
        private String imagePath;
        private String email;
        private String password;

        private String[] phonenumbers;
        private Set<Database.collections.Event> events = new HashSet<>();
        private Playground playground;

        public Builder(String name) {
            this.name = name;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder imagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder phoneNumbers(String... phoneNumbers) {
            this.phonenumbers = phoneNumbers;
            return this;
        }

        public Builder events(Set<Event> events) {
            this.events = events;
            return this;
        }

        public Builder playground(Playground playground) {
            this.playground = playground;
            return this;
        }

        public User build(){
            return new User(this);
        }
    }
}