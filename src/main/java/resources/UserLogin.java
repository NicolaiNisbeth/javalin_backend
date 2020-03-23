package resources;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import database.DALException;
import database.dao.Controller;
import org.json.JSONObject;

import io.javalin.http.Context;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class UserLogin {
    private static Brugeradmin ba;

    public static void isUserInDB(String userName){
        try {
            Controller.getController().getUserWithUserName(userName);
            System.out.println("han er admin");
        } catch (DALException e) {
            e.printStackTrace();
        }
    }

    public static Bruger verificerLogin(String request, Context ctx) {
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
        System.out.println(user.brugernavn);
       isUserInDB(user.brugernavn.toString());
        return user;
    }
}
