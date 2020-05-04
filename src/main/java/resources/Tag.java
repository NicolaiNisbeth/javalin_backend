package resources;

public interface Tag {

    String EVENT_ID = "id";
    String EVENT_NAME = "name";
    String EVENT_DESCRIPTION = "description";
    String EVENT_YEAR = "year";
    String EVENT_MONTH = "month";
    String EVENT_DAY = "day";
    String EVENT_HOUR_START = "hourstart";
    String EVENT_HOUR_END = "hourend";
    String EVENT_MINUTE_START = "minutestart";
    String EVENT_MINUTE_END = "minuteend";
    String EVENT_ASSIGNED_USERS = "assignedusers";
    String EVENT_IMAGEPATH = "imagepath";
    String EVENT_PARTICIPANTS = "participants";
    String EVENT_DETAILS = "details";

    String USERS = "users";
    String USER_NAME = "username";

    String PLAYGROUND_ID = "id";
    String PLAYGROUND_NAMES = "playgroundNames";
    String PLAYGROUND_STREET_NAME = "streetname";
    String PLAYGROUND_STREET_NUMBER = "streetnumber";
    String PLAYGROUND_ZIPCODE = "zipcode";
    String PLAYGROUND_PEDAGOGUES = "pedagogues";
    String PLAYGROUND_COMMUNE = "commune";
    String PLAYGROUND_EVENTS = "events";
    String PLAYGROUND_HASSOCCERFIELD = "hassoccerfield";
    String PLAYGROUND_TOILET_POSSIBILITIES = "ToiletPossibilities";
    String PLAYGROUND_TOILETS = "toilets";
    String PLAYGROUND_IMAGEPATH = "imagepath";
    String PLAYGROUND_MESSAGE_ID = "messageid";
    String PLAYGROUND_MESSAGES = "messages";

    String MESSAGE_ID = "messageID";
    String MESSAGE_CATEGORY = "category";
    String MESSAGE_STRING = "messageString";
    String MESSAGE_WRITTENBY_ID = "writtenByID";
    String MESSAGE_HASIMAGE = "hasImage";

    String USERNAME_ADMIN = "usernameAdmin";
    String PASSWORD_ADMIN = "passwordAdmin";
    String USERNAME = "username";
    String PASSWORD = "password";
    String FIRSTNAME = "firstname";
    String LASTNAME = "lastname";
    String EMAIL = "email";
    String STATUS = "status";
    String STATUS_PEDAGOG = "pædagog";
    String PLAYGROUNDSNAMES = "playgroundsNames";
    String WEBSITE = "website";
    String PHONENUMBERS = "phoneNumbers";
    String IMAGEPATH = String.format("http://18.185.121.182:8080/rest/users");
}
