package javalin_resources.HttpMethods;

import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import database.dto.UserDTO;
import database.Controller;
import database.exceptions.NoModificationException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.ContentType;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;

import java.util.NoSuchElementException;

public class Delete implements Tag {

    public static class Playground {

        public static Handler deleteOnePlayground = ctx -> {
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

    public static class User {

        public static Handler deleteParticipantFromPlaygroundEvent = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            Controller.getInstance().removeUserFromEvent(jsonObject.getString(EVENT_ID), jsonObject.getString(USER_ID));
            // TODO: remove true and catch exception and set corresponding status code

            if (true)
                ctx.status(200).result("delete participant from playground event");
            else
                ctx.status(404).result("Couldn't remove user from event");
        };

        public static Handler deleteUser = ctx -> {
            JSONObject jsonObject, deleteUserModel;
            jsonObject = new JSONObject(ctx.body());
            deleteUserModel = jsonObject.getJSONObject("deleteUserModel");
            String usernameAdmin = deleteUserModel.getString(USERNAME_ADMIN);
            String passwordAdmin = deleteUserModel.getString(PASSWORD_ADMIN);
            String username = deleteUserModel.getString(USERNAME);
            // todo slet ham fra legeplader også
            // todo Nisbeth?? JSONArray adminRightsOfNewUser = jsonObject.getJSONArray("userAdminRights");

            boolean adminAuthorized = Shared.checkAdminCredentials(usernameAdmin, passwordAdmin, ctx);
            if (!adminAuthorized) {
                return;
            }

            Controller.getInstance().deleteUser(username);
            ctx.status(200);
            ctx.json("OK - User deleted");
            ctx.contentType("json");
        };
    }

    public static class Pedagogue {

        public static Handler deletePedagogueFromPlayground = ctx -> {
            Controller.getInstance().removePedagogueFromPlayground(ctx.pathParam(PLAYGROUND_NAME), ctx.pathParam(USER_NAME));
            // TODO: remove true and catch exception and set corresponding status code
            if (true)
                ctx.status(200).result("Pedagogue is removed from the playground");
            else
                ctx.status(404).result("Couldn't remove pedagogue from playground");
        };

    }

    public static class Event {

        public static Handler deleteEventFromPlayground = ctx -> {
            Controller.getInstance().deletePlaygroundEvent(ctx.pathParam(EVENT_ID));
            // TODO: remove true and catch exception and set corresponding status code

            if (true) {
                ctx.status(200).result("Event has been removed from the playground");
            } else {
                ctx.status(404).result("Couldn't remove event from playground");
            }
        };

        public static Handler removeUserFromPlaygroundEvent = ctx -> {
            String id = ctx.pathParam("id");
            String username = ctx.pathParam("username");
            if (id.isEmpty() || username.isEmpty()){
                ctx.status(HttpStatus.BAD_REQUEST_400);
                ctx.result("Bad request - No event id or username in path param");
                ctx.contentType(ContentType.JSON);
                return;
            }

            try {
                Controller.getInstance().removeUserFromEvent(id, username);
                ctx.status(HttpStatus.OK_200);
                ctx.result("OK - user was removed from event successfully");
                ctx.json(new UserDTO.Builder(username));
                ctx.contentType(ContentType.JSON);
            } catch (NoSuchElementException e){
                ctx.status(HttpStatus.NOT_FOUND_404);
                ctx.result(String.format("Not found - event %s or user %s is not in database", id, username));
                ctx.contentType(ContentType.JSON);
            } catch (NoModificationException | MongoException e){
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                ctx.result("Internal error - failed to remove user from event");
                ctx.contentType(ContentType.JSON);
            }
        };
    }

    public static class Message {

        public static Handler deletePlaygroundMessage = ctx -> {
            Controller.getInstance().deletePlaygroundMessage(ctx.pathParam(PLAYGROUND_MESSAGE_ID));
            // TODO: remove true and catch exception and set corresponding status code


            if (true)
                ctx.status(200).result("Message deleted successfully");
            else
                ctx.status(404).result("Couldn't delete message");
        };

    }
}
