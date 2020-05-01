package javalin_resources.HttpMethods;

import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import database.dto.EventDTO;
import database.dto.MessageDTO;
import database.dto.PlaygroundDTO;
import database.dto.UserDTO;
import database.Controller;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.ContentType;
import org.eclipse.jetty.http.HttpStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

public class Get implements Tag {

    public static class Event {

        public static Handler readOneEvent = ctx -> {
            EventDTO event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
            if (event != null) {
                ctx.json(event).contentType("json");
                ctx.status(200);
            } else
                ctx.status(404).result("Couldn't find an event");
        };

        public static Handler readOneEventParticipants = ctx -> {
            EventDTO event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
            if (event != null) {
                ctx.json(event.getParticipants()).contentType("json");
                ctx.status(200);
            } else
                ctx.status(404).result("Couldn't find any participants for this event");
        };

        public static Handler readOneEventOneParticipant = ctx -> {
            EventDTO event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
            for (UserDTO user : event.getAssignedUsers())
                if (user.getUsername().equals(ctx.pathParam(USER_NAME))) {
                    ctx.json(user).contentType("json");
                    ctx.status(200);
                    return;
                } else
                    ctx.status(404).result("Couldn't find the participant for this event");
        };

        public static Handler readOnePlayGroundAllEvents = ctx -> {
            List<EventDTO> events = Controller.getInstance().getEventsInPlayground(ctx.pathParam(PLAYGROUND_NAME));
            if (events != null) {
                ctx.json(events).contentType("json");
                ctx.status(200);
            } else {
                ctx.status(404).result("Couldn't find any events for this playground");
            }
        };
    }

    public static class Playground {

        public static Handler readAllPlaygrounds = ctx -> {
            try {
                List<PlaygroundDTO> playgrounds = Controller.getInstance().getPlaygrounds();
                ctx.status(HttpStatus.OK_200);
                ctx.result("Ok - playgrounds were fetched successfully");
                ctx.json(playgrounds);
                ctx.contentType(ContentType.JSON);
            } catch (NoSuchElementException e){
                ctx.status(HttpStatus.NOT_FOUND_404);
                ctx.result("Not found - no playgrounds in database");
                ctx.contentType(ContentType.JSON);
            } catch (Exception e){
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                ctx.result("Internal error - failed to fetch playgrounds in database");
                ctx.contentType(ContentType.JSON);
            }
        };

        public static Handler readOnePlayground = ctx -> {
            ctx.json(Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME))).contentType("json");

        };

        public static Handler readOnePlaygroundAllEmployee = ctx -> {
            ctx.json(Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME)).getAssignedPedagogue()).contentType("json");
        };

        public static Handler readOnePlaygroundOneEmployee = ctx -> {
            ctx.json(Controller.getInstance().getUser(ctx.pathParam(USER_NAME))).contentType("json");
        };
    }

    public static class Message {

        public static Handler readOneMessage = ctx -> {
            MessageDTO message = Controller.getInstance().getMessage(ctx.pathParam((MESSAGE_ID)));
            if (message != null) {
                ctx.json(message).contentType("json");
                ctx.status(200);
            } else
                ctx.status(404).result("Failed to retrieve message");
        };

        public static Handler readAllMessages = ctx -> {
            List<MessageDTO> messages = Controller.getInstance().getMessagesInPlayground(ctx.pathParam(PLAYGROUND_NAME));
            if (messages != null) {
                ctx.json(messages).contentType("json");
                ctx.status(200);
            } else
                ctx.status(404).result("Failed to retrieve any messages");
        };
    }

    public static class User {

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
