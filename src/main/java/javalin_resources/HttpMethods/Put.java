package javalin_resources.HttpMethods;

import database.collections.*;
import database.dao.Controller;
import io.javalin.http.Handler;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class Put {

    private static final String EVENT_ID = "id";
    private static final String EVENT_NAME = "name";
    private static final String EVENT_DESCRIPTION = "description";
    private static final String EVENT_YEAR = "year";
    private static final String EVENT_MONTH = "month";
    private static final String EVENT_DAY = "day";
    private static final String EVENT_HOUR = "hour";
    private static final String EVENT_HOUR_START = "hourstart";
    private static final String EVENT_HOUR_END = "hourend";
    private static final String EVENT_MINUTE_START = "minutestart";
    private static final String EVENT_MINUTE_END = "minuteend";
    private static final String EVENT_ASSIGNED_USERS = "assignedusers";
    private static final String EVENT_IMAGEPATH = "imagepath";
    private static final String EVENT_PARTICIPANTS = "participants";
    private static final String EVENT_DETAILS = "details";

    private static final String USER = "user";
    private static final String USER_ID = "id";
    private static final String USER_NAME = "username";

    private static final String PEDAGOGUE = "pedagogue";


    private static final String PLAYGROUND_ID = "id";
    private static final String PLAYGROUND_NAME = "name";
    private static final String PLAYGROUND_STREET_NAME = "streetname";
    private static final String PLAYGROUND_STREET_NUMBER = "streetnumber";
    private static final String PLAYGROUND_ZIPCODE = "zipcode";
    private static final String PLAYGROUND_PEDAGOGUES = "pedagogues";
    private static final String PLAYGROUND_COMMUNE = "commune";
    private static final String PLAYGROUND_EVENTS = "events";
    private static final String PLAYGROUND_HASSOCCERFIELD = "hassoccerfield";
    private static final String PLAYGROUND_TOILETS = "toilets";
    private static final String PLAYGROUND_IMAGEPATH = "imagepath";
    private static final String PLAYGROUND_MESSAGE_ID = "messageid";
    private static final String PLAYGROUND_MESSAGES = "messages";




    public static class PutEvent {

        public static Handler updateEventToPlaygroundPut = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            Event event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
            Playground playground = Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME));

            if (playground != null) {

                if (jsonObject.has(EVENT_ID)) {
                    event.setId(jsonObject.getString(EVENT_ID));
                }
                if (jsonObject.has(EVENT_NAME)) {
                    event.setName(jsonObject.getString(EVENT_NAME));
                }
                if (jsonObject.has(EVENT_IMAGEPATH)) {
                    event.setImagepath(jsonObject.getString(EVENT_IMAGEPATH));
                }
                if (jsonObject.has(EVENT_PARTICIPANTS)) {
                    event.setParticipants(jsonObject.getInt(EVENT_PARTICIPANTS));
                }
                if (jsonObject.has(EVENT_DESCRIPTION)) {
                    event.setDescription(jsonObject.getString(EVENT_DESCRIPTION));
                }
                if (jsonObject.has(EVENT_DETAILS)) {
                    //TODO: Change this
                    event.setDetails(null);
                }
                if (jsonObject.has(EVENT_ASSIGNED_USERS)) {
                    Set<User> assignedUsers = new HashSet<>();
                    for (int i = 0; i < jsonObject.getJSONArray(EVENT_ASSIGNED_USERS).length(); i++) {
                        String assignedUserId = jsonObject.getJSONArray(EVENT_ASSIGNED_USERS).getJSONObject(i).getString(EVENT_ASSIGNED_USERS);
                        assignedUsers.add(Controller.getInstance().getUser(assignedUserId));
                    }
                    event.setAssignedUsers(assignedUsers);
                }
                if (jsonObject.has(PLAYGROUND_NAME)) {
                    event.setPlayground(PLAYGROUND_NAME);
                }

                if (Controller.getInstance().updatePlaygroundEvent(event)) {
                    ctx.status(200).result("Event is updated");
                }
            } else {
                ctx.status(404).result("Couldn't update event");
            }
        };
    }

    public static class PutPlayground {

        public static Handler updatePlaygroundPut = ctx -> {
            Playground playground = Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME));
            JSONObject jsonObject = new JSONObject(ctx.body());
            if (playground != null) {
                if (jsonObject.has(PLAYGROUND_STREET_NAME))
                    playground.setStreetName(jsonObject.getString(PLAYGROUND_STREET_NAME));

                if (jsonObject.has(PLAYGROUND_PEDAGOGUES)) {
                    Set<User> pedagoges = new HashSet<>();

                    for (int i = 0; i < jsonObject.getJSONArray(PLAYGROUND_PEDAGOGUES).length(); i++) {
                        String username = jsonObject.getJSONArray(PLAYGROUND_PEDAGOGUES).getString(i);
                        pedagoges.add(Controller.getInstance().getUser(username));
                    }
                    playground.setAssignedPedagogue(pedagoges);
                }

                if (jsonObject.has(PLAYGROUND_COMMUNE))
                    playground.setCommune(jsonObject.getString(PLAYGROUND_COMMUNE));

                if (jsonObject.has(PLAYGROUND_EVENTS)) {
                    Set<Event> eventSet = new HashSet<>();
                    for (int i = 0; i < jsonObject.getJSONArray(PLAYGROUND_EVENTS).length(); i++) {
                        String eventid = jsonObject.getJSONArray(PLAYGROUND_EVENTS).getJSONObject(i).getString(PLAYGROUND_EVENTS);
                        eventSet.add(Controller.getInstance().getEvent(eventid));
                    }
                    playground.setEvents(eventSet);
                }
                if (jsonObject.has(PLAYGROUND_HASSOCCERFIELD))
                    playground.setHasSoccerField(jsonObject.getBoolean(PLAYGROUND_HASSOCCERFIELD));

                if (jsonObject.has(PLAYGROUND_ID))
                    playground.setId(jsonObject.getString(PLAYGROUND_ID));

                if (jsonObject.has(PLAYGROUND_IMAGEPATH))
                    playground.setImagePath(jsonObject.getString(PLAYGROUND_IMAGEPATH));

                if (jsonObject.has(PLAYGROUND_MESSAGES)) {
                    Set<Message> messagesSet = new HashSet<>();
                    for (int i = 0; i < jsonObject.getJSONArray(PLAYGROUND_MESSAGE_ID).length(); i++) {
                        String messageid = jsonObject.getJSONArray(PLAYGROUND_MESSAGE_ID).getJSONObject(i).getString(PLAYGROUND_MESSAGE_ID);
                        messagesSet.add(Controller.getInstance().getMessage(messageid));
                    }
                    playground.setMessages(messagesSet);
                }

                if (jsonObject.has(PLAYGROUND_STREET_NUMBER))
                    playground.setStreetNumber(jsonObject.getInt(PLAYGROUND_STREET_NUMBER));

                if (jsonObject.has(PLAYGROUND_TOILETS))
                    playground.setToiletPossibilities(jsonObject.getBoolean(PLAYGROUND_TOILETS));

                if (jsonObject.has(PLAYGROUND_ZIPCODE))
                    playground.setZipCode(jsonObject.getInt(PLAYGROUND_ZIPCODE));

                if (Controller.getInstance().updatePlayground(playground)) {
                    ctx.status(200).result("Playground updated");
                    //Test
                    System.out.println("update playground with name " + playground.getName());
                }
            } else {
                ctx.status(404).result("Playground didn't update");
            }
        };

    }

    public static class PutPedagogue {

        public static Handler updatePedagogueToPlayGroundPut = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            Playground playground = Controller.getInstance().getPlayground(jsonObject.getString(PLAYGROUND_NAME));
            User user = Controller.getInstance().getUser(jsonObject.getString(PEDAGOGUE));
            playground.getAssignedPedagogue().add(user);
            Controller.getInstance().updatePlayground(playground);
            if (jsonObject.getString(PEDAGOGUE) != null && jsonObject.getString(PLAYGROUND_NAME) != null) {
                ctx.status(200).result("Updated Successfull");
            } else {
                ctx.status(404).result("Failed to update");
            }
        };



    }

    public static class PutUser {

        public static Handler updateUserToPlaygroundEventPut = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            Event event = Controller.getInstance().getEvent(jsonObject.getString(EVENT_ID));
            User user = Controller.getInstance().getUser(jsonObject.getString(USER_ID));
            event.getAssignedUsers().add(user);
            Controller.getInstance().updatePlaygroundEvent(event);
            if (jsonObject.getString(EVENT_ID) != null && jsonObject.getString(USER_ID) != null) {
                ctx.status(200).result("Update successfull");
            } else {
                ctx.status(404).result("Failed to update");
            }
        };

    }


}
