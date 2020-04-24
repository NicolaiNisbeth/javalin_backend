package resources;

public class JsonModels {

    public static class LoginModel {
        String username;
        String password;
    }

    public static class UserModel {
        String usernameAdmin;
        String passwordAdmin;
        String username;
        String password;
        String firstname;
        String lastname;
        String email;
        String status;
        String phoneNumber;
        String website;
        String imagePath;
        String[] playgroundsIDs;
    }
}
