package brugerautorisation.transport.rmi;

import brugerautorisation.data.Bruger;

public interface Brugeradmin extends java.rmi.Remote {
    String  DOMAIN = "javabog.dk";
    String  PATH = "brugeradmin";
    String  URL  = String.format("rmi://%s/%s", DOMAIN, PATH);

    /**
     * Henter alle en brugers data
     * @return et Bruger-objekt med alle data
     */
    Bruger hentBruger(String brugernavn, String adgangskode) throws java.rmi.RemoteException;

}
