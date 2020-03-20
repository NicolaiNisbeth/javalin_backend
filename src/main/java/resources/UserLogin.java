package resources;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import org.json.JSONObject;

import io.javalin.http.Context;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class UserLogin {
    private static Brugeradmin ba;

    // @Path("brugerLogin")

    public static Bruger verificerLogin(String request, Context ctx) {

        JSONObject jsonObject = new JSONObject(request);
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        try {
            ba = (Brugeradmin) Naming.lookup(Brugeradmin.URL);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
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

        return user;
    }
}

   /* private static void bruger(Context ctx) throws Exception {
        String brugernavn = ctx.pathParam("brugernavn");    // del af path  /bruger/s123456
        String adgangskode = ctx.queryParam("adgangskode"); // del af query  ?adgangskode=kode1xyz
        Brugeradmin ba = (Brugeradmin) Naming.lookup("server.rmi://javabog.dk/brugeradmin");
        if (adgangskode == null) {
            Bruger bruger = ba.hentBrugerOffentligt(brugernavn);
            ctx.json(bruger);
        } else try {
            Bruger bruger = ba.hentBruger(brugernavn, adgangskode);
            ctx.json(bruger);
        } catch (Exception e) {
            ctx.status(401).result("Unauthorized");
        }*/


/*    private static void sendGlemtAdgangskodeEmail(Context ctx) throws Exception {
        Brugeradmin ba = (Brugeradmin) Naming.lookup("server.rmi://javabog.dk/brugeradmin");
        String brugernavn = ctx.formParam("brugernavn");
        String følgetekst = ctx.formParam("foelgetekst");
        if (brugernavn == null) brugernavn = ctx.queryParam("brugernavn");
        ba.sendGlemtAdgangskodeEmail(brugernavn, følgetekst);
        ctx.result("Der blev sendt en mail til " + brugernavn + " med teksten " + følgetekst);
    }*/

