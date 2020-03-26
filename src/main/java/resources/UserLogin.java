package resources;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import database.DALException;
import database.collections.User;
import database.dao.Controller;
import org.json.JSONObject;

import io.javalin.http.Context;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class UserLogin {
    private static Brugeradmin ba;

    // Metoden opretter brugeren i databasen, hvis han ikke allerede findes.
    public static User isUserInDB(Bruger bruger) {
        User user = null;
        try {
            user = Controller.getController().getUserWithUserName(bruger.brugernavn);

        } catch (DALException e) {
            System.out.println("Bruger findes ikke i databasen. \nBruger oprettes i databasen");

            System.out.println(bruger.ekstraFelter.get("webside").toString());

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
        Bruger user = null;
        try {
            user = ba.hentBruger(username, password);

            if (user != null) {
                //todo kald til controller
            }

        } catch (Exception e) {
            ctx.status(401).result("Unauthorized");
        }
        return isUserInDB(user);
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

