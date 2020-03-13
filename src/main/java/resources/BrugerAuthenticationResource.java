package resources;/*
package resources;


import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import org.json.JSONObject;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@Path("brugerLogin")
public class BrugerAuthenticationResource {
    private Brugeradmin ba;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response verificerLogin(String request){

        try {
            JSONObject jsonObject = new JSONObject(request);
            String brugernavn = jsonObject.getString("brugernavn");
            String adgangskode = jsonObject.getString("adgangskode");
            ba = (Brugeradmin) Naming.lookup(Brugeradmin.URL);
            Bruger bruger = ba.hentBruger(brugernavn, adgangskode);
            return Response.ok().entity(bruger).build();
        }
        catch (IllegalArgumentException e){
            return Response.status(404).entity("brugernavn eller adgangskode er forkert").build();
        }
        catch (NotBoundException | MalformedURLException | RemoteException e) {
            return Response.status(404).build();
        }
    }
}*/
