package Database.DTOs;

public class PlaygroundDTO {
    public int id;
    public String name;
    public Address address;
    public String imagePath;
    public boolean toiletPossibilities;


    public PlaygroundDTO(String name, Address address, String imagePath, boolean toiletPossibilities) {
        this.name = name;
        this.address = address;
        this.imagePath = imagePath;
        this.toiletPossibilities = toiletPossibilities;
    }

    public PlaygroundDTO() {
    }

    public static class Address{
        public String streetName;
        public int streetNumber;
        public String commune;
        public int zipCode;

        public Address(String streetName, int streetNumber, String commune, int zipCode) {
            this.streetName = streetName;
            this.streetNumber = streetNumber;
            this.commune = commune;
            this.zipCode = zipCode;
        }
    }
}
