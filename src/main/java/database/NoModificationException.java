package database;

public class NoModificationException extends Exception {

    public NoModificationException() {

    }
    public NoModificationException(String msg) {
        super(msg);
    }

    public NoModificationException(String msg, Throwable e) {
        super(msg, e);
    }
}
