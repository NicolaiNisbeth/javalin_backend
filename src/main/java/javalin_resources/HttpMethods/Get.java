package javalin_resources.HttpMethods;

import database.collections.Event;
import database.collections.Message;
import database.collections.User;
import database.dao.Controller;
import io.javalin.http.Handler;
import javalin_resources.Util.ViewUtil;

import java.util.List;
import java.util.Map;

public class Get {

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

    private static final String MESSAGE_ID = "id";
    private static final String MESSAGE_CATEGORY = "category";
    private static final String MESSAGE_ICON = "icon";
    private static final String MESSAGE_STRING = "messageString";
    private static final String MESSAGE_OUTDATED = "outDated";
    private static final String MESSAGE_WRITTENBY_ID = "writtenByID";

    private static final String USER_ID = "id";
    private static final String USER = "user";
    private static final String USERS = "users";
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
    private static final String PLAYGROUND_TOILET_POSSIBILITIES = "ToiletPossibilities";
    private static final String PLAYGROUND_IMAGEPATH = "imagepath";
    private static final String PLAYGROUND_MESSAGE_ID = "messageid";
    private static final String PLAYGROUND_MESSAGES = "messages";



    public static class GetEvent {
        public static Handler oneEventGet = ctx -> {
            Event event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
            if (event != null) {
                ctx.json(event).contentType("json");
                ctx.status(200);
            } else
                ctx.status(404).result("Couldn't find an event");
        };
        public static Handler oneEventParticipantsGet = ctx -> {
            Event event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
            if (event != null) {
                ctx.json(event.getParticipants()).contentType("json");
                ctx.status(200);
            } else
                ctx.status(404).result("Couldn't find any participants for this event");
        };
        public static Handler oneEventOneParticipantGet = ctx -> {
            Event event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
            for (User user : event.getAssignedUsers())
                if (user.getUsername().equals(ctx.pathParam(USER_NAME))) {
                    ctx.json(user).contentType("json");
                    ctx.status(200);
                    return;
                } else
                    ctx.status(404).result("Couldn't find the participant for this event");
        };
        public static Handler playGroundAllEventsGet = ctx -> {
            List<Event> events = Controller.getInstance().getPlaygroundEvents(ctx.pathParam(PLAYGROUND_NAME));
            if (events != null) {
                ctx.json(events).contentType("json");
                ctx.status(200);
            } else {
                ctx.status(404).result("Couldn't find any events for this playground");
            }
        };
    }

    public static class GetPlayground {
        public static Handler allPlaygroundsGet = ctx -> {
            ctx.json(Controller.getInstance().getPlaygrounds()).contentType("json");
        };
        public static Handler onePlaygroundGet = ctx -> {
            ctx.json(Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME))).contentType("json");

        };
        public static Handler onePlaygroundAllEmployeeGet = ctx -> {
            ctx.json(Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME)).getAssignedPedagogue()).contentType("json");
        };
        public static Handler onePlaygroundOneEmployeeGet = ctx -> {
            ctx.json(Controller.getInstance().getUser(ctx.pathParam(USER_NAME))).contentType("json");
        };




    }

    public static class GetMessage {

        public static Handler OneMessageGet = ctx -> {
            Message message = Controller.getInstance().getMessage(ctx.pathParam((MESSAGE_ID)));
            if (message != null) {
                ctx.json(message).contentType("json");
                ctx.status(200);
            } else
                ctx.status(404).result("Failed to retrieve message");
        };

        public static Handler AllMessagesGet = ctx -> {
            List<Message> messages = Controller.getInstance().getPlaygroundMessages(ctx.pathParam(PLAYGROUND_NAME));
            if (messages != null) {
                ctx.json(messages).contentType("json");
                ctx.status(200);
            } else
                ctx.status(404).result("Failed to retrieve any messages");
        };

        public static Handler PlayGroundMessagesHandlerGet = ctx -> {
            Map<String, Object> model = ViewUtil.baseModel(ctx);
            List<Message> messages = Controller.getInstance().getPlaygroundMessages(ctx.pathParam("playground"));
            if (messages != null) {
                model.put("messages", messages);
                ctx.status(200);
            } else {
                ctx.status(404);
            }
        };

    }

}
