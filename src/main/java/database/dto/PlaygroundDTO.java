package database.dto;

import org.jongo.marshall.jackson.oid.MongoId;
import org.jongo.marshall.jackson.oid.MongoObjectId;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PlaygroundDTO {

  @MongoId // auto
  @MongoObjectId
  private String id;
  private String name;
  private String imagePath;

  public String getImageText() {
    return imageText;
  }

  public void setImageText(String imageText) {
    this.imageText = imageText;
  }

  private String imageText;

  public String getDescriptionText() {
    return descriptionText;
  }

  public void setDescriptionText(String descriptionText) {
    this.descriptionText = descriptionText;
  }

  private String descriptionText;
  private boolean toiletPossibilities;
  private boolean hasSoccerField;
  private String streetName;
  private int streetNumber;
  private String commune;
  private int zipCode;
  private Set<UserDTO> assignedPedagogue = new HashSet<>();
  private Set<EventDTO> events = new HashSet<>();
  private Set<MessageDTO> messages = new HashSet<>();

  private PlaygroundDTO() {
  }

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

  public Set<UserDTO> getAssignedPedagogue() {
    return assignedPedagogue;
  }

  public void setAssignedPedagogue(Set<UserDTO> assignedPedagogue) {
    this.assignedPedagogue = assignedPedagogue;
  }

  public Set<EventDTO> getEvents() {
    return events;
  }

  public void setEvents(Set<EventDTO> events) {
    this.events = events;
  }

  public Set<MessageDTO> getMessages() {
    return messages;
  }

  public void setMessages(Set<MessageDTO> messages) {
    this.messages = messages;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PlaygroundDTO that = (PlaygroundDTO) o;

    if (toiletPossibilities != that.toiletPossibilities) return false;
    if (hasSoccerField != that.hasSoccerField) return false;
    if (streetNumber != that.streetNumber) return false;
    if (zipCode != that.zipCode) return false;
    if (!Objects.equals(id, that.id)) return false;
    if (!Objects.equals(name, that.name)) return false;
    if (!Objects.equals(imagePath, that.imagePath)) return false;
    if (!Objects.equals(streetName, that.streetName)) return false;
    if (!Objects.equals(commune, that.commune)) return false;
    if (!Objects.equals(assignedPedagogue, that.assignedPedagogue))
      return false;
    if (!Objects.equals(events, that.events)) return false;
    return Objects.equals(messages, that.messages);
  }

  public static class Builder {
    @MongoId // auto
    @MongoObjectId
    private String _id;
    private String name;
    private String imagePath;
    private boolean toiletPossibilities;
    private boolean hasSoccerField;

    public String getImageText() {
      return imageText;
    }

    public Builder setImageText(String imageText) {
      this.imageText = imageText;
      return this;
    }

    private String imageText;

    public String getDescriptionText() {
      return descriptionText;
    }

    public Builder setDescriptionText(String descriptionText) {
      this.descriptionText = descriptionText;
      return this;
    }

    private String descriptionText;
    private String streetName;
    private int streetNumber;
    private String commune;
    private int zipCode;
    private Set<UserDTO> assignedPedagogue = new HashSet<>();
    private Set<EventDTO> events = new HashSet<>();
    private Set<MessageDTO> messages = new HashSet<>();

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

    public Set<UserDTO> getAssignedPedagogue() {
      return assignedPedagogue;
    }

    public Builder setAssignedPedagogue(Set<UserDTO> assignedPedagogue) {
      this.assignedPedagogue = assignedPedagogue;
      return this;
    }

    public Set<EventDTO> getEvents() {
      return events;
    }

    public Builder setEvents(Set<EventDTO> events) {
      this.events = events;
      return this;
    }

    public Set<MessageDTO> getMessages() {
      return messages;
    }

    public Builder setMessages(Set<MessageDTO> messages) {
      this.messages = messages;
      return this;
    }

    public PlaygroundDTO build() {
      //Here we create the actual playground object, which is always in a fully initialised state when it's returned.
      PlaygroundDTO playground = new PlaygroundDTO();  //Since the builder is in the class, we can invoke its private constructor.
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
      playground.imageText = this.imageText;
      playground.descriptionText = this.descriptionText;
      return playground;
    }
  }
}
