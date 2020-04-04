package javalin_resources.HttpMethods;

import database.collections.Event;
import database.collections.Message;
import database.collections.User;
import database.dao.Controller;
import io.javalin.http.Handler;

import java.util.List;

public class Get implements Tag {

    public static class GetEvent {

        public static Handler readOneEventGet = ctx -> {
            Event event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
            if (event != null) {
                ctx.json(event).contentType("json");
                ctx.status(200);
            } else
                ctx.status(404).result("Couldn't find an event");
        };

        public static Handler readOneEventParticipantsGet = ctx -> {
            Event event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
            if (event != null) {
                ctx.json(event.getParticipants()).contentType("json");
                ctx.status(200);
            } else
                ctx.status(404).result("Couldn't find any participants for this event");
        };

        public static Handler readOneEventOneParticipantGet = ctx -> {
            Event event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
            for (User user : event.getAssignedUsers())
                if (user.getUsername().equals(ctx.pathParam(USER_NAME))) {
                    ctx.json(user).contentType("json");
                    ctx.status(200);
                    return;
                } else
                    ctx.status(404).result("Couldn't find the participant for this event");
        };

        public static Handler readOnePlayGroundAllEventsGet = ctx -> {
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

        public static Handler readAllPlaygroundsGet = ctx -> {
            ctx.json(Controller.getInstance().getPlaygrounds()).contentType("json");
        };

        public static Handler readOnePlaygroundGet = ctx -> {
            ctx.json(Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME))).contentType("json");

        };

        public static Handler readOnePlaygroundAllEmployeeGet = ctx -> {
            ctx.json(Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME)).getAssignedPedagogue()).contentType("json");
        };

        public static Handler readOnePlaygroundOneEmployeeGet = ctx -> {
            ctx.json(Controller.getInstance().getUser(ctx.pathParam(USER_NAME))).contentType("json");
        };

    }

    public static class GetMessage {

        public static Handler readOneMessageGet = ctx -> {
            Message message = Controller.getInstance().getMessage(ctx.pathParam((MESSAGE_ID)));
            if (message != null) {
                ctx.json(message).contentType("json");
                ctx.status(200);
            } else
                ctx.status(404).result("Failed to retrieve message");
        };

        public static Handler readAllMessagesGet = ctx -> {
            List<Message> messages = Controller.getInstance().getPlaygroundMessages(ctx.pathParam(PLAYGROUND_NAME));
            if (messages != null) {
                ctx.json(messages).contentType("json");
                ctx.status(200);
            } else
                ctx.status(404).result("Failed to retrieve any messages");
        };
    }

}