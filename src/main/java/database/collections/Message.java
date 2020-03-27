package database.collections;

import org.jetbrains.annotations.NotNull;
import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.Date;
import java.util.Objects;

public class Message implements Comparable<Message> {


    public static class Builder {
        @MongoId // auto
        @MongoObjectId
        private String _id;
        private String category;
        private String icon;
        private boolean outDated;
        private String writtenByID;
        private String messageString;
        private String playgroundID;
        private Date date;

        public String getMessageString() {
            return messageString;
        }

        public Builder setMessageString(String messageString) {
            this.messageString = messageString;
            return this;
        }

        public Builder() { }

        public String get_id() {
            return _id;
        }

        public Builder set_id(String _id) {
            this._id = _id;
            return this;
        }

        public String getPlaygroundID(){
            return playgroundID;
        }

        public Builder setPlaygroundID(String playgroundID){
            this.playgroundID = playgroundID;
            return this;
        }

        public Date getDate() {
            return date;
        }

        public Builder setDate(Date date) {
            this.date = date;
            return this;
        }

        public String getCategory() {
            return category;
        }

        public Builder setName(String name) {
            this.category = name;
            return this;
        }

        public String getIcon() {
            return icon;
        }

        public Builder setImagePath(String imagePath) {
            this.icon = imagePath;
            return this;
        }

        public boolean isOutDated() {
            return outDated;
        }

        public Builder setToiletPossibilities(boolean toiletPossibilities) {
            this.outDated = toiletPossibilities;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setIcon(String icon) {
            this.icon = icon;
            return this;

        }

        public Builder setOutDated(boolean outDated) {
            this.outDated = outDated;
            return this;

        }

        public String getWrittenByID() {
            return writtenByID;
        }

        public Builder setWrittenByID(String writtenByID) {
            this.writtenByID = writtenByID;
            return this;

        }

        public Message build() {
            //Here we create the actual playground object, which is always in a fully initialised state when it's returned.
            Message message = new Message();  //Since the builder is in the class, we can invoke its private constructor.
            message.id = this._id;
            if (this.category == null)
                message.category = "general";
            else
                message.category = this.category;
            message.icon = this.icon;
            message.outDated = this.outDated;
            message.writtenByID = this.writtenByID;
            message.messageString = this.messageString;
            message.date = this.date;

            return message;
        }
    }

    @MongoId // auto
    @MongoObjectId
    private String id;
    private String category;
    private String icon;
    private boolean outDated;
    private String writtenByID;
    private String messageString;
    private String playgroundID;
    private Date date;

    public String getMessageString() {
        return messageString;
    }

    public void setMessageString(String messageString) {
        this.messageString = messageString;
    }

    public String getPlaygroundName(){
        return playgroundID;
    }

    public void setPlaygroundID(String playgroundID){
        this.playgroundID = playgroundID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isOutDated() {
        return outDated;
    }

    public void setOutDated(boolean outDated) {
        this.outDated = outDated;
    }

    public String getWrittenByID() {
        return writtenByID;
    }

    public void setWrittenByID(String writtenByID) {
        this.writtenByID = writtenByID;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", messageString='" + messageString + '\'' +
                ", outDated=" + outDated +
                ", writtenByID='" + writtenByID + '\'' +
                '}';
    }

    public void setId(String id) {
        this.id = id;
    }


    private Message() {
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (outDated != message.outDated) return false;
        if (!Objects.equals(id, message.id)) return false;
        if (!Objects.equals(category, message.category)) return false;
        if (!Objects.equals(icon, message.icon)) return false;
        if (!Objects.equals(writtenByID, message.writtenByID)) return false;
        if (!Objects.equals(messageString, message.messageString))
            return false;
        if (!Objects.equals(playgroundID, message.playgroundID))
            return false;
        return Objects.equals(date, message.date);
    }

    @Override
    public int compareTo(@NotNull Message o) {
        return date.compareTo(o.date);
    }
}
