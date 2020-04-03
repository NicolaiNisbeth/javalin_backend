package resources;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import database.DALException;
import database.collections.User;
import database.dao.Controller;
import org.json.JSONObject;
import io.javalin.http.Context;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class UserLogin {
    final static String USERNAME_ADMIN = "usernameAdmin";
    final static String PASSWORD_ADMIN = "passwordAdmin";
    final static String USERNAME = "username";
    final static String PASSWORD = "password";
    final static String FIRSTNAME = "firstname";
    final static String LASTNAME = "lastname";
    final static String EMAIL = "email";
    final static String STATUS = "status";
    final static String STATUS_PEDAGOG = "pedagog";
    final static String STATUS_ADMIN = "admin";
    final static String PLAYGROUNDSIDS = "playgroundsIDs";
    final static String WEBSITE = "website";
    final static String PHONENUMBER = "phoneNumber";
    final static String IMAGEPATH = "http://localhost:8088/rest/user";


    private static Brugeradmin ba;

    public static User verifyLogin(Context ctx) {
        JSONObject jsonObject = new JSONObject(ctx.body());
        String username = jsonObject.getString(USERNAME);
        String password = jsonObject.getString(PASSWORD);
        try {
            ba = (Brugeradmin) Naming.lookup(Brugeradmin.URL);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
        Bruger bruger;
        try {
            bruger = ba.hentBruger(username, password);
            if (bruger != null) {
                return findUserInDB(bruger);
            }
            //todo njl lav bedre
        } catch (Exception e) {
            if (username.equalsIgnoreCase("root")) {
                User root = null;
                try {
                    root = Controller.getInstance().getUser(username);
                } catch (DALException e1) {
                    e1.printStackTrace();
                }
                if (root.getPassword().equalsIgnoreCase(password)) {
                    return root;
                }
            }
        }
        ctx.status(401).result("Unauthorized");
        return null;
    }

    // Metoden opretter brugeren i databasen, hvis han ikke allerede findes.
    public static User findUserInDB(Bruger bruger) {
        User user = null;
        try {
            user = Controller.getInstance().getUser(bruger.brugernavn);
        } catch (DALException e) {
            e.printStackTrace();
        }

        if (user == null) {
            System.out.println("Bruger findes ikke i databasen. \nBruger oprettes i databasen");
            user = new User.Builder(bruger.brugernavn)
                    .setFirstname(bruger.fornavn)
                    .setLastname(bruger.efternavn)
                    .email(bruger.email)
                    .password(bruger.adgangskode)
                    .status(STATUS_PEDAGOG)
                    .setWebsite(bruger.ekstraFelter.get("webside").toString())
                    .setImagePath(String.format(IMAGEPATH + "/%s/profile-picture", bruger.brugernavn))
                    .build();
            Controller.getInstance().createUser(user);
        }
        //Brugeren har ikke selv logget ind før og skal derfor ikke oprettes i DB men opdateres
        if (!user.isLoggedIn()) {
            user.setFirstname(bruger.fornavn);
            user.setLastname(bruger.efternavn);
            user.setEmail(bruger.email);
            user.setPassword(bruger.adgangskode);
            user.setStatus(user.getStatus());
            user.setWebsite(bruger.ekstraFelter.get("webside").toString());
            user.setLoggedIn(true);
            user.setImagePath(String.format(IMAGEPATH + "/%s/profile-picture", bruger.brugernavn));
            Controller.getInstance().updateUser(user);
        }
        return user;
    }

    public static InputStream getProfilePicture(String username) {
        String path = String.format("src/main/resources/images/profile_pictures/%s.png", username);
        BufferedImage buffImage = null;
        File imageFile = new File(path);
        try {
            buffImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Hvis ikke han har et profil billede får han random_user
        if (buffImage == null) {
            path = "src/main/resources/images/profile_pictures/random_user.png";
            imageFile = new File(path);
            try {
                buffImage = ImageIO.read(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(buffImage, "png", os);
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        return is;
    }
}