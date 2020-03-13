package brugerautorisation.data;



import server.rmi.IGalgelegRMI;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;

public class Bruger implements Serializable {
	// Vigtigt: Sæt versionsnummer så objekt kan læses selvom klassen er ændret!
	private static final long serialVersionUID = 12345; // bare et eller andet nr.

	public String brugernavn; // studienummer
	public String email = "hvad@ved.jeg.dk";
	public long sidstAktiv;
  	public String campusnetId; // campusnet database-ID
  	public String studeretning = "ukendt";
  	public String fornavn = "test";
  	public String efternavn = "testesen";
	public String adgangskode;
  	public HashMap<String, Object> ekstraFelter = new HashMap<String, Object>();
	public String toString()
	{
		return brugernavn+"/"+adgangskode;
	}

	public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException {
		System.out.println("Forbinder til " + IGalgelegRMI.URL);
		IGalgelegRMI galgen = (IGalgelegRMI) Naming.lookup(IGalgelegRMI.URL);
		System.out.println("Forbundet til serveren");
	}
}