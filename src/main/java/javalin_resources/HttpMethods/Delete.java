package javalin_resources.HttpMethods;

import database.dao.Controller;
import io.javalin.http.Handler;
import org.json.JSONObject;

public class Delete {

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


    private static final String PLAYGROUND_ID = "id";
    private static final String PLAYGROUND_NAME = "name";
    private static final String PLAYGROUND_STREET_NAME = "streetname";
    private static final String PLAYGROUND_STREET_NUMBER = "streetnumber";
    private static final String PLAYGROUND_ZIPCODE = "zipcode";
    private static final String PLAYGROUND_PEDAGOGUES = "pedagogues";
    private static final String PLAYGROUND_COMMUNE = "commune";
    private static final String PLAYGROUND_EVENTS = "events";
    private static final String PLAYGROUND_SOCCERFIELD = "soccerfield";
    private static final String PLAYGROUND_TOILETS = "toilets";
    private static final String PLAYGROUND_IMAGEPATH = "imagepath";
    private static final String PLAYGROUND_MESSAGE_ID = "messageid";
    private static final String PLAYGROUND_MESSAGES = "messages";


    public static class DeletePlayground {

        public static Handler removeOnePlaygroundDelete = ctx -> {
            String playgroundname = "";
            playgroundname = ctx.pathParam(PLAYGROUND_NAME);

            if (playgroundname != "") {
                Controller.getInstance().deletePlayground(playgroundname);
                ctx.status(200);
                System.out.println("Deleted playground with name " + playgroundname);
            } else {
                ctx.status(404).result("Couldn't delete playground or it doesn't exist");
                System.out.println("Found no playground");
            }
        };

    }

    public static class DeleteUser {

        public static Handler removeUserFromPlaygroundEventDelete = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            if (Controller.getInstance().removeUserFromPlaygroundEvent(jsonObject.getString(EVENT_ID), jsonObject.getString(USER_ID)))
                ctx.status(200).result("Removed user from event");
            else
                ctx.status(404).result("Couldn't remove user from event");
        };

    }

    public static class DeletePedagogue {

        public static Handler removePedagogueFromPlaygroundDelete = ctx -> {
            if (Controller.getInstance().removePedagogueFromPlayground(ctx.pathParam(PLAYGROUND_NAME), ctx.pathParam(USER_NAME)))
                ctx.status(200).result("Pedagogue is removed from the playground");
            else
                ctx.status(404).result("Couldn't remove pedagogue from playground");
        };

    }

    public static class DeleteEvent {

        public static Handler removeEventFromPlaygroundDelete = ctx -> {
            if (Controller.getInstance().removePlaygroundEvent(ctx.pathParam(EVENT_ID))) {
                ctx.status(200).result("Event has been removed from the playground");
            } else {
                ctx.status(404).result("Couldn't remove event from playground");
            }
        };

    }



}
