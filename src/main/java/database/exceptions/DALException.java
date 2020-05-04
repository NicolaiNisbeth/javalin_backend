package database.exceptions;

public class DALException extends Exception {
    /*
    Fra Budtz:
    //Til Java serialisering...
        private static final long serialVersionUID = 7355418246336739229L;
     */


    public DALException(String msg) {
        super(msg);
    }

    public DALException(String msg, Throwable e) {
        super(msg, e);
    }

}
