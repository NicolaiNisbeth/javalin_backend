package data;

import java.io.Serializable;
import java.util.ArrayList;

public class GameStatus implements Serializable {
    //Disse to linjer kopieret fra "Bruger". Forstår ikke helt hvorfor det skal med.
    // Vigtigt: Sæt versionsnummer så objekt kan læses selvom klassen er ændret!
    public static final long serialVersionUID = 12345; // bare et eller andet nr.

    public ArrayList<String> brugteBogstaver = new ArrayList<String>();
    public String synligtOrd;
    public int antalForkerteBogstaver;
    public boolean sidsteBogstavVarKorrekt;
    public boolean spilletErVundet;
    public boolean spilletErTabt;
    public boolean spilletErSlut;
    public String ordet;

    public GameStatus(ArrayList<String> brugteBogstaver, String synligtOrd, int antalForkerteBogstaver, boolean sidsteBogstavVarKorrekt, boolean spilletErVundet, boolean spilletErTabt, boolean spilletErSlut, String ordet) {
        this.brugteBogstaver = brugteBogstaver;
        this.synligtOrd = synligtOrd;
        this.antalForkerteBogstaver = antalForkerteBogstaver;
        this.sidsteBogstavVarKorrekt = sidsteBogstavVarKorrekt;
        this.spilletErVundet = spilletErVundet;
        this.spilletErTabt = spilletErTabt;
        this.spilletErSlut = spilletErSlut;
        this.ordet = ordet;
    }
}
