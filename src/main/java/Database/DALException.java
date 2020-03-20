package Database;

public class DALException extends Exception {
    /*
    Fra Budtz:
    //Til Java serialisering...
        private static final long serialVersionUID = 7355418246336739229L;
     */

    /*
    Også this, tror ikke vi bruger.
    public DALException(String message, Throwable e){
    }
    */

    public DALException(String message){
        super(message);
    }

}
