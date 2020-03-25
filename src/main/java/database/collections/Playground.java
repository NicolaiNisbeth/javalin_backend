package database.collections;

import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Playground {

    public static class Builder {
        @MongoId // auto
        @MongoObjectId
        private String _id;
        private String name;
        private String imagePath;
        private boolean toiletPossibilities;
        private boolean hasSoccerField;

        private String streetName;
        private int streetNumber;
        private String commune;
        private int zipCode;
        private Set<User> assignedPedagogue = new HashSet<>();
        private Set<Event> events = new HashSet<>();
        private Set<Message> messages = new HashSet<>();

        public Builder(String name) {
            this.name = name;
        }

        public String get_id() {
            return _id;
        }

        public Builder set_id(String _id) {
            this._id = _id;
            return this;
        }

        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        public boolean isHasSoccerField() {
            return hasSoccerField;
        }

        public Builder setHasSoccerField(boolean hasSoccerField) {
            this.hasSoccerField = hasSoccerField;
            return this;
        }

        public String getImagePath() {
            return imagePath;
        }

        public Builder setImagePath(String imagePath) {
            this.imagePath = imagePath;
            return this;
        }

        public boolean isToiletPossibilities() {
            return toiletPossibilities;
        }

        public Builder setToiletPossibilities(boolean toiletPossibilities) {
            this.toiletPossibilities = toiletPossibilities;
            return this;
        }

        public String getStreetName() {
            return streetName;
        }

        public Builder setStreetName(String streetName) {
            this.streetName = streetName;
            return this;
        }

        public int getStreetNumber() {
            return streetNumber;
        }

        public Builder setStreetNumber(int streetNumber) {
            this.streetNumber = streetNumber;
            return this;
        }

        public String getCommune() {
            return commune;
        }

        public Builder setCommune(String commune) {
            this.commune = commune;
            return this;
        }

        public int getZipCode() {
            return zipCode;
        }

        public Builder setZipCode(int zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public Set<User> getAssignedPedagogue() {
            return assignedPedagogue;
        }

        public Builder setAssignedPedagogue(Set<User> assignedPedagogue) {
            this.assignedPedagogue = assignedPedagogue;
            return this;
        }

        public Set<Event> getEvents() {
            return events;
        }

        public Builder setEvents(Set<Event> events) {
            this.events = events;
            return this;
        }

        public Set<Message> getMessages() {
            return messages;
        }

        public Builder setMessages(Set<Message> messages) {
            this.messages = messages;
            return this;
        }

        public Playground build() {
            //Here we create the actual playground object, which is always in a fully initialised state when it's returned.
            Playground playground = new Playground();  //Since the builder is in the class, we can invoke its private constructor.
            playground.id = this._id;
            playground.name = this.name;
            playground.imagePath = this.imagePath;
            playground.toiletPossibilities = this.toiletPossibilities;
            playground.hasSoccerField = this.hasSoccerField;
            playground.streetName = this.streetName;
            playground.streetNumber = this.streetNumber;
            playground.commune = this.commune;
            playground.zipCode = this.zipCode;
            playground.assignedPedagogue = this.assignedPedagogue;
            playground.events = this.events;
            return playground;
        }
    }

    @MongoId // auto
    @MongoObjectId
    private String id;
    private String name;

    private String imagePath;
    private boolean toiletPossibilities;
    private boolean hasSoccerField;

    private String streetName;
    private int streetNumber;
    private String commune;
    private int zipCode;
    private Set<User> assignedPedagogue = new HashSet<>();
    private Set<Event> events = new HashSet<>();
    private Set<Message> messages = new HashSet<>();


    public boolean isHasSoccerField() {
        return hasSoccerField;
    }

    public void setHasSoccerField(boolean hasSoccerField) {
        this.hasSoccerField = hasSoccerField;
    }

    @Override
    public String toString() {
        return "Playground{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", toiletPossibilities=" + toiletPossibilities +
                ", hasSoccerField=" + hasSoccerField +
                ", streetName='" + streetName + '\'' +
                ", streetNumber=" + streetNumber +
                ", commune='" + commune + '\'' +
                ", zipCode=" + zipCode +
                ", assignedPedagogue=" + assignedPedagogue +
                ", events=" + events +
                ", messages=" + messages +
                '}';
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

    public boolean isToiletPossibilities() {
        return toiletPossibilities;
    }

    public void setToiletPossibilities(boolean toiletPossibilities) {
        this.toiletPossibilities = toiletPossibilities;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public Set<User> getAssignedPedagogue() {
        return assignedPedagogue;
    }

    public void setAssignedPedagogue(Set<User> assignedPedagogue) {
        this.assignedPedagogue = assignedPedagogue;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    private Playground() {
    }
}
