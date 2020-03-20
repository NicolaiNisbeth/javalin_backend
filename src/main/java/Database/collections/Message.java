package Database.collections;

import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

public class Message {

    public static class Builder {
        @MongoId // auto
        @MongoObjectId
        private String _id;
        private String category;
        private String icon;
        private boolean outDated;
        private String writtenByID;
        private String messageString;

        public String getMessageString() {
            return messageString;
        }

        public Builder setMessageString(String messageString) {
            this.messageString = messageString;
            return this;
        }

        public Builder() {
        }

        public String get_id() {
            return _id;
        }

        public Builder set_id(String _id) {
            this._id = _id;
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

    public String getMessageString() {
        return messageString;
    }

    public void setMessageString(String messageString) {
        this.messageString = messageString;
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
}
