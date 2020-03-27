package database.utils;

public class Path {

    public static class Playground {
        // All playgrounds.
        public static final String PLAYGROUND_ALL = "/playgrounds/all";
        // A playground with the given name.
        public static final String PLAYGROUND_ONE = "/playground/:name";
        // All employees on a playground
        public static final String PLAYGROUND_ONE_EMPLOYEES = "/playground/:name/employees";
        // All playgrounds with a given zipcode.
        public static final String PLAYGROUNDS_WITHZIPCODE = "/playground/:zipcode";
        // All playgrounds nearby.
        public static final String PLAYGROUNDS_NEARBY = "/playground/:nearby";
        // All events on a playground
        public static final String PLAYGROUNDS_ONE_EVENTS = "/playground/:name/events";
        // All events on a given month on a given playground.
        public static final String PLAYGROUND_ONE_EVENTS_MONTH = "/playground/:name/events/:month";
        // All events on all playgrounds.
        public static final String PLAYGROUND_ALL_EVENTS = "/playground/events";
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
