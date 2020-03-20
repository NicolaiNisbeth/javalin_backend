package Database.collections;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class Playground {

    private String id;
    private String name;
    private String imagePath;
    private boolean toiletPossibilities;
    private String streetName;
    private int streetNumber;
    private String commune;
    private int zipCode;
    private Set<User> assignedUsers = new HashSet<>();
    private Set<Event> events = new TreeSet<>();

    private Playground(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.imagePath = builder.imagePath;
        this.toiletPossibilities = builder.toiletPossibilities;
        this.streetName = builder.streetName;
        this.streetNumber = builder.streetNumber;
        this.commune = builder.commune;
        this.zipCode = builder.zipCode;
        this.assignedUsers = builder.assignedUsers;
        this.events = builder.events;
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

    public Set<User> getAssignedUsers() {
        return assignedUsers;
    }

    public void setAssignedUsers(Set<User> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "Playground{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", toiletPossibilities=" + toiletPossibilities +
                ", streetName='" + streetName + '\'' +
                ", streetNumber=" + streetNumber +
                ", commune='" + commune + '\'' +
                ", zipCode=" + zipCode +
                ", assignedUsers=" + assignedUsers +
                ", events=" + events +
                '}';
    }

    public static class Builder {
        private String name;
        private String id;
        private String imagePath;
        private boolean toiletPossibilities;
        private String streetName;
        private int streetNumber;
        private String commune;
        private int zipCode;
        private Set<User> assignedUsers;
        private Set<Event> events;

        public Builder(String name) {
            this.name = name;
        }

        public Builder id(String id){
            this.id = id;
            return this;
        }

        public Builder imagePath(String imagePath){
            this.imagePath = imagePath;
            return this;
        }

        public Builder toiletPossibilities(boolean toiletPossibilities){
            this.toiletPossibilities = toiletPossibilities;
            return this;
        }

        public Builder streetName(String streetName){
            this.streetName = streetName;
            return this;
        }

        public Builder streetNumber(int streetNumber){
            this.streetNumber = streetNumber;
            return this;
        }

        public Builder commune(String commune){
            this.commune = commune;
            return this;
        }

        public Builder zipCode(int zipCode){
            this.zipCode = zipCode;
            return this;
        }

        public Builder assignedUsers(Set<User> assignedUsers){
            this.assignedUsers = assignedUsers;
            return this;
        }

        public Builder events(Set<Event> events){
            this.events = events;
            return this;
        }

        public Playground build(){
            return new Playground(this);
        }
    }
}
