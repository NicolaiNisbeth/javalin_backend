package javalin_resources;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import database.DALException;
import database.collections.User;
import database.dao.Controller;
import io.javalin.http.Handler;
import org.json.JSONObject;

import io.javalin.http.Context;
import javalin_resources.Util.ViewUtil;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Map;

public class UserLogin {
    private static Brugeradmin ba;

    public static User isUserInDB(Bruger bruger) {
        User user = null;
        try {
            user = Controller.getInstance().getUser(bruger.brugernavn);
        } catch (DALException e) {
            e.printStackTrace();
        }

        if (user == null){
            System.out.println("Bruger findes ikke i databasen. \nBruger oprettes i databasen");
            user = new User.Builder(bruger.brugernavn)
                    .setFirstname(bruger.fornavn)
                    .setLastname(bruger.efternavn)
                    .email(bruger.email)
                    .password(bruger.adgangskode)
                    .status("pedagogue")
                    .build();

            Controller.getInstance().createUser(user);
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

    // The origin of the request (request.pathInfo()) is saved in the session so
    // the user can be redirected back after login
    public static Handler confirmlogin = ctx -> {
        if (!ctx.path().startsWith("/books"))
            return;
        if (ctx.sessionAttribute("currentUser") == null) {
            ctx.sessionAttribute("loginRedirect", ctx.path());
            ctx.redirect("/");
            ctx.status(401);
        }
    };

    public static Handler handleLoginPost = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        User user = verificerLogin(ctx.body(), ctx);
        if (user != null) {
            model.put("authenticationFailed", true);
        } else {
            ctx.sessionAttribute("currentUser", user.getId());
            ctx.sessionAttribute("currentUserStatus", user.getStatus());
            model.put("authenticationSucceeded", true);
            model.put("currentUser", user.getId());
        }
    };
}
