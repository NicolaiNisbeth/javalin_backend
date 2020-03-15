package Database;

public class DALException extends Exception {

    /**
     * Class for throwing an exception, while running the program.
     *
     * @author Christian Budtz
     */

    public DALException(String msg, Throwable e) {
        super(msg, e);
    }
    public DALException(String msg) {
        super(msg);
    }
}
