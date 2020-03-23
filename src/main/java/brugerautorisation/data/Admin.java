package brugerautorisation.data;

import database.collections.Event;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Admin extends Bruger implements Serializable {
    private static final long serialVersionUID = 12233;
    private String id;
    private String firstname;
    private String lastname;
    private String status;
    private String imagepath;
    private String email;
    private String password;
    private String userName;
    private String[] phonenumbers;
    private Set<Event> events = new HashSet<>();    // many-to-many, One-Way-Embedding (an event has few Users, but User has many events)
    private String playgroundID;
    Set<String> connectedPlaygroundIDs;


}
