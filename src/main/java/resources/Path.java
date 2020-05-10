package resources;

public class Path {
  /**
   * Hello friends
   * The strings is named after first the object it looks for, and then how muc. For example look at the playground_all
   * and the playground one. If it looks for more things it will say {object}_{how many?}_{another object}_{how many}
   * and so fourth. If you have any questions then contact me.
   * Br, Gustav.
   * <p>
   * Ressources can be found here. https://docs.google.com/spreadsheets/d/1YTCKT_WVPJq8Thh6pwso8qQqgjPSwprOZ46bQqMWGQs/edit#gid=0
   */
  public static class Playground {
    // All playgrounds.
    public static final String PLAYGROUNDS_ALL = "/rest/playgrounds";
    // A playground with the given name.
    public static final String PLAYGROUNDS_ONE = "/rest/playgrounds/:name";
    // All employees on a playground
    public static final String PLAYGROUNDS_ONE_PEDAGOGUE_ALL = "/rest/playgrounds/:name/pedagogues";
    // One employees on a playground
    public static final String PLAYGROUNDS_ONE_PEDAGOGUE_ONE = "/rest/playgrounds/:name/pedagogues/:username";
    // All events on a playground
    public static final String PLAYGROUNDS_ONE_EVENTS_ALL = "/rest/playgrounds/:playgroundNames/events";
    // One events on a playground
    public static final String PLAYGROUNDS_ONE_EVENT_ONE = "/rest/playgrounds/:name/events/:id";
    // One event paricipant
    public static final String PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL = "/rest/playgrounds/:name/events/:id/participants";
    // One event paricipant
    public static final String PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE = "/rest/playgrounds/:name/events/:id/participants/:username";
    // Playground message
    public static final String PLAYGROUNDS_ONE_MESSAGE_ONE = "/rest/playgrounds/:name/messages/:id";
    // Playground message all
    public static final String PLAYGROUNDS_ONE_MESSAGE_ALL = "/rest/playgrounds/:name/messages";

    public static final String PLAYGROUNDS_ONE_PROFILE_PICTURE = "/rest/playgrounds/:name/picture";
  }

  public static class User {
    // ALl users
    public static final String USERS_ALL = "/rest/users";
    // All users that are employees - not clients
    public static final String USERS_ALL_EMPLOYEES = "/rest/users/all-employees";
    public static final String USERS_CRUD = "/rest/users/:username";

    // Get employee's profile picture
    public static final String USERS_ONE_PROFILE_PICTURE = "/rest/users/:username/profile-picture";

    public static final String USERS_LOGIN = "/rest/users/login";
    public static final String USERS_RESET_PASSWORD = "/rest/users/reset-setPassword";

  }

  public static class Message {
    public static final String MESSAGE_IMAGE_ONE = "/rest/messages/:id/image";
  }

}
