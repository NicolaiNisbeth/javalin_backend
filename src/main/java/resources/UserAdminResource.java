package resources;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import com.mongodb.WriteResult;
import database.DALException;
import database.collections.User;
import database.dao.Controller;
import org.json.JSONArray;
import org.json.JSONObject;

import io.javalin.http.Context;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public class UserAdminResource {
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
    final static String IMAGEPATH = "imagePath";
    final static String PHONENUMBER = "phoneNumber";

    private static Brugeradmin ba;

    public static User verifyLogin(String request, Context ctx) {
        JSONObject jsonObject = new JSONObject(request);
        String username = jsonObject.getString(USERNAME);
        String password = jsonObject.getString(PASSWORD);
        try {
            ba = (Brugeradmin) Naming.lookup(Brugeradmin.URL);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
        Bruger bruger = null;
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
            Controller.getInstance().updateUser(user);
        }
        return user;
    }

    //todo ryd op
    //todo få sat nogle ordentlige status koder på
    //bruges af admins til at give brugere rettigheder - INDEN de selv er logget på første gang
    public static List<User> createUser(String request, Context ctx) {
        JSONObject jsonObject = new JSONObject(request);
        String usernameAdmin = jsonObject.getString(USERNAME_ADMIN);
        String passwordAdmin = jsonObject.getString(PASSWORD_ADMIN);
        String username = jsonObject.getString(USERNAME);
        String password = jsonObject.getString(PASSWORD);
        String firstName = jsonObject.getString(FIRSTNAME);
        String lastName = jsonObject.getString(FIRSTNAME);
        String email = jsonObject.getString(EMAIL);
        String status = jsonObject.getString(STATUS);
        JSONArray playgroundIDs = jsonObject.getJSONArray(PLAYGROUNDSIDS);
        String phoneNumber = jsonObject.getString(PHONENUMBER);
        String imagePath = jsonObject.getString(IMAGEPATH);
        String website = jsonObject.getString(WEBSITE);

        User admin = null;
        User newUser = null;

        try {
            admin = Controller.getInstance().getUser(usernameAdmin);
        } catch (DALException e) {
            e.printStackTrace();
        }
        if (!admin.getPassword().equalsIgnoreCase(passwordAdmin)) {
            ctx.status(401).result("Unauthorized - password er ikke korrekt");
            return Controller.getInstance().getUsers();
        } else {
            try {
                admin = Controller.getInstance().getUser(usernameAdmin);
            } catch (DALException e) {
                e.printStackTrace();
            }
            newUser = new User.Builder(username)
                    .status(status)
                    .build();

            //newUser.setPassword(password);
            newUser.setFirstname(firstName);
            newUser.setLastname(lastName);
            newUser.setStatus(status);
            newUser.setEmail(email);
            newUser.setWebsite(website);
            newUser.setImagepath(imagePath);
            String[] phoneNumbers = new String[1];
            phoneNumbers[0] = phoneNumber;
            newUser.setPhonenumbers(phoneNumbers);

            for (Object id : playgroundIDs) {
                newUser.getPlaygroundsIDs().add(id.toString());
            }

            WriteResult ws = Controller.getInstance().createUser(newUser);
            if (ws.wasAcknowledged()) {
                ctx.status(401).result("User was created");
            } else {
                ctx.status(401).result("User was not created");
                return Controller.getInstance().getUsers();
            }
        }

        return Controller.getInstance().getUsers();
    }

    //todo få sat nogle ordentlige status koder på
    //bruges af admins til at give brugere rettigheder - EFTER de selv er logget på første gang
    public static List<User> updateUser(String request, Context ctx) {
        JSONObject jsonObject = new JSONObject(request);
        String usernameAdmin = jsonObject.getString(USERNAME_ADMIN);
        String passwordAdmin = jsonObject.getString(PASSWORD_ADMIN);
        String username = jsonObject.getString(USERNAME);
        String password = jsonObject.getString(PASSWORD);
        String firstName = jsonObject.getString(FIRSTNAME);
        String lastName = jsonObject.getString(LASTNAME);
        String email = jsonObject.getString(EMAIL);
        String status = jsonObject.getString(STATUS);
        JSONArray playgroundIDs = jsonObject.getJSONArray(PLAYGROUNDSIDS);
        String phoneNumber = jsonObject.getString(PHONENUMBER);
        String imagePath = jsonObject.getString(IMAGEPATH);
        String website = jsonObject.getString(WEBSITE);

        User admin = null;
        User userToUpdate = null;

        try {
            admin = Controller.getInstance().getUser(usernameAdmin);
        } catch (DALException e) {
            e.printStackTrace();
        }
        if (!admin.getPassword().equalsIgnoreCase(passwordAdmin)) {
            ctx.status(401).result("Unauthorized - password er ikke korrekt");
            return Controller.getInstance().getUsers();
        } else {
            try {
                admin = Controller.getInstance().getUser(usernameAdmin);
                userToUpdate = Controller.getInstance().getUser(username);
            } catch (DALException e) {
                e.printStackTrace();
            }

            //userToUpdate.setPassword(password);
            userToUpdate.setFirstname(firstName);
            userToUpdate.setLastname(lastName);
            userToUpdate.setStatus(status);
            userToUpdate.setEmail(email);
            userToUpdate.setWebsite(website);
            userToUpdate.setImagepath(imagePath);
            String[] phoneNumbers = new String[1];
            phoneNumbers[0] = phoneNumber;
            userToUpdate.setPhonenumbers(phoneNumbers);
            userToUpdate.getPlaygroundsIDs().removeAll(userToUpdate.getPlaygroundsIDs());
            for (Object id : playgroundIDs) {
                userToUpdate.getPlaygroundsIDs().add(id.toString());
            }

            if (Controller.getInstance().updateUser(userToUpdate)) {
                ctx.status(401).result("User was updated");
            } else {
                ctx.status(401).result("User was not updated");
                return Controller.getInstance().getUsers();
            }
        }
        return Controller.getInstance().getUsers();
    }

    //lavet backup
    public static List<User> deleteUser(String body, Context ctx) {
        JSONObject jsonObject = new JSONObject(body);
        String usernameAdmin = jsonObject.getString(USERNAME_ADMIN);
        String passwordAdmin = jsonObject.getString(PASSWORD_ADMIN);
        String username = jsonObject.getString(USERNAME);
        // todo slet ham fra legeplader også
        //  JSONArray adminRightsOfNewUser = jsonObject.getJSONArray("userAdminRights");

        User admin = null;
        User userToDelete = null;

        try {
            admin = Controller.getInstance().getUser(usernameAdmin);
        } catch (DALException e) {
            e.printStackTrace();
        }
        if (!admin.getPassword().equalsIgnoreCase(passwordAdmin)) {
            ctx.status(401).result("Unauthorized - password er ikke korrekt");
        } else {
            //  usName = Controller.getInstance().getUser(usernameToBeDeleted).getUsername();
            Controller.getInstance().deleteUser(username);
        }
        return Controller.getInstance().getUsers();
    }
}
