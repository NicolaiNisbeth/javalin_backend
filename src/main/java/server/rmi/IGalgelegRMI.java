package server.rmi;

import brugerautorisation.data.Bruger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface IGalgelegRMI extends java.rmi.Remote {
    int PORT = 1099;
    String PATH = "galgeleg";
    String DOMAIN = "ec2-13-48-132-112.eu-north-1.compute.amazonaws.com";
    String URL = String.format("rmi://%s:%d/%s", DOMAIN, PORT, PATH);

    String gaetBogstav(char bogstav, String brugernavn) throws RemoteException, IOException;

    boolean erSpilletSlut(String brugernavn) throws RemoteException;

    String getStatus(String brugernavn) throws RemoteException, IOException;

    ArrayList<String> getStatus2(String brugernavn) throws RemoteException;

    StringBuilder galgeART() throws RemoteException, IOException;

    Bruger login(String brugernavn, String adgangskode) throws RemoteException, MalformedURLException, NotBoundException;

    String startSpil(String brugernavn) throws RemoteException;

    List<String> getHighscoreList() throws RemoteException;
}
