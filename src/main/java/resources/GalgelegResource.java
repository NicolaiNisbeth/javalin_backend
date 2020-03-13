package resources;


import server.rmi.IGalgelegRMI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

//@Path("/galgeleg")
public class GalgelegResource {
    private static IGalgelegRMI server;

    /*@POST
    @Path("/{brugernavn}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)*/
    public static String startGame(String username) {
        String spilStatus = null;
        try {
            System.out.println("Forbinder til " + IGalgelegRMI.URL);
            server = (IGalgelegRMI) Naming.lookup(IGalgelegRMI.URL);
            System.out.println("Forbundet til serveren");
            spilStatus = server.startSpil(username);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
        return spilStatus;
    }
 /*   @GET
    @Path("/{brugernavn}/{bogstav}")
    @Consumes(MediaType.APPLICATION_JSON)
    */

    public static String makeGuess(String username, String guess) {
        String spilStatus = null;
        try {
            spilStatus = server.gaetBogstav(guess.charAt(0), username);
        } catch (IOException | IllegalArgumentException e) {
        }
        return spilStatus;
    }

    public static ArrayList<String> getHighscoreListe() {
        ArrayList<String> highscore = null;
        try {
            if (server == null) connectToServer();
            highscore = (ArrayList<String>) server.getHighscoreList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return highscore;
    }

    private static void connectToServer() {
        try {
            server = (IGalgelegRMI) Naming.lookup(IGalgelegRMI.URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
