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
        public static final String PLAYGROUND_ALL = "/playgrounds";
        // A playground with the given name.
        public static final String PLAYGROUND_ONE = "/playgrounds/:name";
        // All employees on a playground
        public static final String PLAYGROUND_ONE_PEDAGOGUE_ALL = "/playgrounds/:name/pedagogues";
        // One employees on a playground
        public static final String PLAYGROUND_ONE_PEDAGOGUE_ONE = "/playgrounds/:name/pedagogues/:username";
        // All events on all playgrounds.
        public static final String PLAYGROUND_ALL_EVENTS = "/playgrounds/events";
        // All events on a playground
        public static final String PLAYGROUNDS_ONE_EVENTS_ALL = "/playgrounds/:name/events";
        // One events on a playground
        public static final String PLAYGROUNDS_ONE_EVENT_ONE = "/playgrounds/:name/events/:id";
        // One event paricipant
        public static final String PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL = "/playgrounds/:name/events/:id/participants";
        // One event paricipant
        public static final String PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE = "/playgrounds/:name/events/:id/participants/:username";
        // All events on a given month on a given playground.
        public static final String PLAYGROUND_ONE_EVENTS_MONTH = "/playground/:name/events/:month";
        // Playground message
        public static final String PLAYGROUND_ONE_MESSAGE_ONE = "/playgrounds/:name/messages/:id";
        // Playground message all
        public static final String PLAYGROUND_ONE_MESSAGE_ALL = "/playgrounds/:name/messages";
        // All playgrounds with a given zipcode.
        public static final String PLAYGROUNDS_WITHZIPCODE = "/playground/:zipcode";
        // All playgrounds nearby.
        public static final String PLAYGROUNDS_NEARBY = "/playground/:nearby";
    }

    public static class Employee {
        // ALl employees
        public static final String EMPLOYEE_ALL = "/employee";
        // Employee based on ID
        public static final String EMPLOYEE_ONE = "/employee/:id";
        // All employees working within a zipcode.
        public static final String EMPLOYEE_ALL_ZIPCODE = "/employee/:zipcode";
    }

    public static class User {
        public static final String LOGIN = "/login";
        public static final String HOMESCREEN = "/:id";
        public static final String FAVORITES = "/:id/favorites";
        public static final String USER_POSTS = "/id/posts";
    }
}
