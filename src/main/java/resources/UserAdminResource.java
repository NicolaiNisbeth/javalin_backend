package resources;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
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
import java.util.ArrayList;
import java.util.List;

public class UserAdminResource {
    private static Brugeradmin ba;

    public static User verifyLogin(String request, Context ctx) {
        JSONObject jsonObject = new JSONObject(request);
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
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

        } catch (Exception e) {
            ctx.status(401).result("Unauthorized");
        }
        return null;
    }

    // Metoden opretter brugeren i databasen, hvis han ikke allerede findes.
    public static User findUserInDB(Bruger bruger) {
        User user = null;

        try {
            user = Controller.getController().getUserWithUserName(bruger.brugernavn);
        } catch (DALException e) {
            System.out.println("Bruger findes ikke i databasen. \nBruger oprettes i databasen");
            user = new User.Builder(bruger.brugernavn)
                    .setFirstname(bruger.fornavn)
                    .setLastname(bruger.efternavn)
                    .email(bruger.email)
                    .password(bruger.adgangskode)
                    .status("pedagogue")
                    .setWebsite(bruger.ekstraFelter.get("webside").toString())
                    .build();
            try {
                Controller.getController().createUser(user);
            } catch (DALException e1) {
                e1.printStackTrace();
            }
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

            try {
                Controller.getController().updateUser(user);
            } catch (DALException e1) {
                e1.printStackTrace();
            }
        }
        return user;
    }

    //todo få sat nogle ordentlige status koder på
    //bruges af admins til at give brugere rettigheder - INDEN de selv er logget på første gang
    public static String createUser(String request, Context ctx) {
        JSONObject jsonObject = new JSONObject(request);
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        String usernameOfNewUser = jsonObject.getString("usernameOfNewUser");
        String statusOfNewUser = jsonObject.getString("statusOfNewUser");
        JSONArray adminRightsOfNewUser = jsonObject.getJSONArray("userAdminRights");

        //List<Object> adminRightsOfNewUser = jsonObject.getJSONArray("userAdminRights").toList();

        User admin = null;
        User newUser = null;

        try {
            admin = Controller.getController().getUserWithUserName(username);
            if (!admin.getPassword().equalsIgnoreCase(password)) {
                ctx.status(401).result("Unauthorized - password er ikke korrekt");
                return "ikke oprettet";
            } else {
                admin = Controller.getController().getUserWithUserName(username);
                newUser = new User.Builder(usernameOfNewUser)
                        .status(statusOfNewUser)
                        .build();
                for (Object id : adminRightsOfNewUser) {
                    newUser.getPlaygroundsIDs().add(id.toString());
                }
                if (Controller.getController().createUser(admin, newUser)) {
                    ctx.status(401).result("User was created");
                } else {
                    ctx.status(401).result("User was not created");
                    return "ikke oprettet";
                }
            }
        } catch (DALException e) {
            e.printStackTrace();
            ctx.status(401).result("Unauthorized");
            return "ikke oprettet";
        }
        return "oprettet";
    }

    //todo få sat nogle ordentlige status koder på
    //bruges af admins til at give brugere rettigheder - EFTER de selv er logget på første gang
    public static String updateUser(String request, Context ctx) {
        JSONObject jsonObject = new JSONObject(request);
        String adminUsername = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        String usernameOfNewUser = jsonObject.getString("usernameOfNewUser");
        String statusOfNewUser = jsonObject.getString("statusOfNewUser");
        JSONArray adminRightsOfNewUser = jsonObject.getJSONArray("userAdminRights");

        User admin = null;
        User userToUpdate = null;

        try {
            admin = Controller.getController().getUserWithUserName(adminUsername);
            if (!admin.getPassword().equalsIgnoreCase(password)) {
                ctx.status(401).result("Unauthorized - password er ikke korrekt");
                return "ikke updated";
            } else {
                admin = Controller.getController().getUserWithUserName(adminUsername);
                userToUpdate = new User.Builder(usernameOfNewUser)
                        .status(statusOfNewUser)
                        .build();
                for (Object id : adminRightsOfNewUser) {
                    userToUpdate.getPlaygroundsIDs().add(id.toString());
                }
                if (Controller.getController().updateUser(admin, userToUpdate)) {
                    ctx.status(401).result("User was updated");
                } else {
                    ctx.status(401).result("User was not updated");
                    return "ikke updated";
                }
            }
        } catch (DALException e) {
            e.printStackTrace();
            ctx.status(401).result("Unauthorized");
            return "ikke updated";
        }
        return "updated";
    }

    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {

    }
}

