package resources;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import database.DALException;
import database.collections.User;
import database.dao.Controller;
import org.json.JSONObject;

import io.javalin.http.Context;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class UserLogin {
    private static Brugeradmin ba;

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

            try {
                Controller.getController().updateUser(user);
            } catch (DALException e1) {
                e1.printStackTrace();
            }
        }
        return user;
    }


    public static User verificerLogin(String request, Context ctx) {
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

 /*   public static User verificerLogin(String request, Context ctx) {
        JSONObject jsonObject = new JSONObject(request);
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        try {
            ba = (Brugeradmin) Naming.lookup(Brugeradmin.URL);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
        Bruger user = null;
        try {
            user = ba.hentBruger(username, password);

            if (user != null) {
                //todo kald til controller
            }

        } catch (Exception e) {
            ctx.status(401).result("Unauthorized");
        }
        return findUserInDB(user);
    }*/

    //todo få sat nogle ordentlige status koder på
    public static String createUser(String request, Context ctx) {

        JSONObject jsonObject = new JSONObject(request);
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        String usernameOfNewUser = jsonObject.getString("usernameOfNewUser");
        String statusOfNewUser = jsonObject.getString("statusOfNewUser");

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


    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
        Bruger user;
        ba = (Brugeradmin) Naming.lookup(Brugeradmin.URL);
        try {
            user = ba.hentBruger("s185020", "njl_nykode");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

