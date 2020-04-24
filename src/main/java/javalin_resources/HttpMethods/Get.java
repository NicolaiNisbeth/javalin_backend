package javalin_resources.HttpMethods;

import database.collections.Event;
import database.collections.Message;
import database.collections.User;
import database.dao.Controller;
import io.javalin.http.Handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        public static Handler readAllEventsGet = ctx -> {
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
            ctx.json(String.valueOf(ctx.json(Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME))).contentType("json")));
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

    public static class GetUser {

        public static Handler getUserPicture = ctx -> {
            File homeFolder = new File(System.getProperty("user.home"));
            Path path = Paths.get(String.format(homeFolder.toPath() +
                    "/server_resource/profile_images/%s.png", ctx.pathParam("username")));

            File initialFile = new File(path.toString());
            InputStream targetStream = null;
            try {
                targetStream = new FileInputStream(initialFile);
         /*   BufferedImage in = ImageIO.read(initialFile);
            UserAdminResource.printImage(in);*/

            } catch (IOException e) {
                System.out.println("Server: User have no profile picture...");
            }

            if (targetStream != null) {
                ctx.result(targetStream).contentType("image/png");
            } else {
                System.out.println("Server: Returning random user picture...");
                targetStream = Get.class.getResourceAsStream("/images/profile_pictures/random_user.png");
                ctx.result(targetStream).contentType("image/png");
            }
        };

        public static Handler getAllUsers = ctx -> {
            ctx.json(Controller.getInstance().getUsers()).contentType("json");
        };
    }
}
