package database.dto;

import org.jetbrains.annotations.NotNull;
import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.Date;
import java.util.Objects;

public class MessageDTO implements Comparable<MessageDTO> {


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
  private boolean hasImage;

  private MessageDTO(Builder builder) {
    this.id = builder._id;
    this.category = builder.category;
    this.icon = builder.icon;
    this.outDated = builder.outDated;
    this.writtenByID = builder.writtenByID;
    this.messageString = builder.messageString;
    this.playgroundID = builder.playgroundID;
    this.date = builder.date;
    this.hasImage = builder.hasImage;
  }

  public String getMessageString() {
    return messageString;
  }

  public void setMessageString(String messageString) {
    this.messageString = messageString;
  }

  public String getPlaygroundName() {
    return playgroundID;
  }

  public String getPlaygroundID() {
    return playgroundID;
  }

  public void setPlaygroundID(String playgroundID) {
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

  public String getID() {
    return id;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public boolean getHasImage() {
    return hasImage;
  }

  public void setHasImage(boolean hasImage) {
    this.hasImage = hasImage;
  }

  @Override
  public String toString() {
    return "Message{" +
      "id='" + id + '\'' +
      ", category='" + category + '\'' +
      ", messageString='" + messageString + '\'' +
      ", outDated=" + outDated +
      ", writtenByID='" + writtenByID + '\'' +
      ", playgroundID='" + playgroundID + '\'' +
      ", date=" + date +
      '}';
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    MessageDTO message = (MessageDTO) o;

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
  public int compareTo(@NotNull MessageDTO o) {
    return date.compareTo(o.date);
  }

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
    private boolean hasImage;

    public Builder() {
    }

    public String getMessageString() {
      return messageString;
    }

    public Builder setMessageString(String messageString) {
      this.messageString = messageString;
      return this;
    }

    public String get_id() {
      return _id;
    }

    public Builder set_id(String _id) {
      this._id = _id;
      return this;
    }

    public String getPlaygroundID() {
      return playgroundID;
    }

    public Builder setPlaygroundID(String playgroundID) {
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

    public Builder setCategory(String category) {
      this.category = category;
      return this;
    }

    public String getIcon() {
      return icon;
    }

    public Builder setIcon(String icon) {
      this.icon = icon;
      return this;

    }

    public Builder setImagePath(String imagePath) {
      this.icon = imagePath;
      return this;
    }

    public boolean isOutDated() {
      return outDated;
    }

    public Builder setOutDated(boolean outDated) {
      this.outDated = outDated;
      return this;

    }

    public Builder setToiletPossibilities(boolean toiletPossibilities) {
      this.outDated = toiletPossibilities;
      return this;
    }

    public String getWrittenByID() {
      return writtenByID;
    }

    public Builder setWrittenByID(String writtenByID) {
      this.writtenByID = writtenByID;
      return this;

    }

    public Builder setHasImage(boolean hasImage) {
      this.hasImage = hasImage;
      return this;
    }

    public MessageDTO build() {
      //Here we create the actual playground object, which is always in a fully initialised state when it's returned.
      return new MessageDTO(this);
    }
  }
}
