package resources;


import Database.DTOs.PlaygroundDTODum;
import Database.DatabaseManagement;
import server.rmi.IGalgelegRMI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

//@Path("/galgeleg")
public class PlaygroundResource {
    public static ArrayList<PlaygroundDTODum> getPlaygoundsList() {

        ArrayList<PlaygroundDTODum> playgrounds = null;
        try {
            playgrounds = (ArrayList<PlaygroundDTODum>) DatabaseManagement.getPlaygrounds();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playgrounds;
    }
}
