package javalin_resources.Util;

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
        // All events on all playgrounds.
        public static final String PLAYGROUNDS_ALL_EVENTS = "/rest/playgrounds/events";
        // All events on a playground
        public static final String PLAYGROUNDS_ONE_EVENTS_ALL = "/rest/playgrounds/:name/events";
        // One events on a playground
        public static final String PLAYGROUNDS_ONE_EVENT_ONE = "/rest/playgrounds/:name/events/:id";
        // One event paricipant
        public static final String PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL = "/rest/playgrounds/:name/events/:id/participants";
        // One event paricipant
        public static final String PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE = "/rest/playgrounds/:name/events/:id/participants/:username";
        // All events on a given month on a given playground.
        public static final String PLAYGROUND_ONE_EVENTS_MONTH = "/rest/playgrounds/:name/events/:month";
        // Playground message
        public static final String PLAYGROUNDS_ONE_MESSAGE_ONE = "/rest/playgrounds/:name/messages/:id";
        // Playground message all
        public static final String PLAYGROUNDS_ONE_MESSAGE_ALL = "/rest/playgrounds/:name/messages";
        // All playgrounds with a given zipcode.
        public static final String PLAYGROUNDS_WITHZIPCODE = "/rest/playgrounds/:zipcode";
        // All playgrounds nearby.
        public static final String PLAYGROUNDS_NEARBY = "/rest/playgrounds/:nearby";
    }

    public static class User {
        // ALl users
        public static final String USERS_ALL = "/rest/users";
        public static final String USERS_ALL_EMPLOYEES = "/rest/users/all-employees";
        // User based on ID
        public static final String USERS_ONE = "/rest/users/:id";
        // All employees working within a zipcode.
        public static final String USERS_ALL_ZIPCODE = "/rest/users/:zipcode";
        // Get employee's profile picture
        public static final String USERS_ONE_PROFILE_PICTURE = "/rest/users/:username/profile-picture";

        public static final String USERS_LOGIN = "rest/users/login";
        public static final String USERS_DELETE = "rest/users/delete";
        public static final String USERS_UPDATE = "rest/users/update";
        public static final String USERS_CREATE = "rest/users/create";
        public static final String USERS_RESET_PASSWORD = "rest/users/reset-setPassword";

    }
}
