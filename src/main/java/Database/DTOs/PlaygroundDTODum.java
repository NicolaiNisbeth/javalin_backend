package Database.DTOs;

import java.io.Serializable;

public class PlaygroundDTODum implements Serializable {

    private static final long serialVersionUID = 12345;
    public int id;
    public String name;
    public String imagePath;
    public boolean toiletPossibilities;
    public String streetName;
    public int streetNumber;
    public String commune;
    public int zipCode;


    public PlaygroundDTODum(String name, String imagePath, boolean toiletPossibilities, String streetName, int streetNumber, String commune, int zipCode) {
        this.name = name;
        this.imagePath = imagePath;
        this.toiletPossibilities = toiletPossibilities;
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.commune = commune;
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "Playground{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", toiletPossibilities=" + toiletPossibilities +
                ", streetName='" + streetName + '\'' +
                ", streetNumber=" + streetNumber +
                ", commune='" + commune + '\'' +
                ", zipCode=" + zipCode +
                '}';
    }

    public PlaygroundDTODum() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public boolean getIsToiletPossibilities() {
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
}
