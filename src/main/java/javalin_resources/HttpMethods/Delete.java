package javalin_resources.HttpMethods;

import database.DataSource;
import database.collections.User;
import database.dao.Controller;
import io.javalin.http.Handler;
import org.json.JSONObject;

public class Delete implements Tag {

    public static class DeletePlayground {

        public static Handler deleteOnePlaygroundDelete = ctx -> {
            String playgroundname = "";
            playgroundname = ctx.pathParam(PLAYGROUND_NAME);

            if (playgroundname != "") {
                Controller.getInstance(DataSource.getTestDB()).deletePlayground(playgroundname);
                ctx.status(200);
                System.out.println("Deleted playground with name " + playgroundname);
            } else {
                ctx.status(404).result("Couldn't delete playground or it doesn't exist");
                System.out.println("Found no playground");
            }
        };

    }

    public static class DeleteUser {

        public static Handler deleteParticipantFromPlaygroundEventDelete = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            if (Controller.getInstance(DataSource.getTestDB()).removeUserFromPlaygroundEvent(jsonObject.getString(EVENT_ID), jsonObject.getString(USER_ID)))
                ctx.status(200).result("Removed user from event");
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

            Controller.getInstance(DataSource.getTestDB()).deleteUser(username);
            ctx.status(222);
            ctx.result("User deleted.");
            ctx.json("User deleted").contentType("json");
        };
    }

    public static class DeletePedagogue {

        public static Handler deletePedagogueFromPlaygroundDelete = ctx -> {
            if (Controller.getInstance(DataSource.getTestDB()).removePedagogueFromPlayground(ctx.pathParam(PLAYGROUND_NAME), ctx.pathParam(USER_NAME)))
                ctx.status(200).result("Pedagogue is removed from the playground");
            else
                ctx.status(404).result("Couldn't remove pedagogue from playground");
        };

    }

    public static class DeleteEvent {

        public static Handler deleteEventFromPlaygroundDelete = ctx -> {
            Controller.getInstance(DataSource.getTestDB()).removePlaygroundEvent(ctx.pathParam(EVENT_ID));
            if (true) {
                ctx.status(200).result("Event has been removed from the playground");
            } else {
                ctx.status(404).result("Couldn't remove event from playground");
            }
        };

        public static Handler remoteUserFromPlaygroundEventPost = ctx -> {
            String id = ctx.pathParam("id");
            String username = ctx.pathParam("username");
            Boolean successful = Controller.getInstance(DataSource.getTestDB()).removeUserFromPlaygroundEvent(id, username);
            if (successful) {
                ctx.status(200).result("Removal was successful");
                ctx.json(new User.Builder(username));
            } else {
                ctx.status(404).result("Failed to remove");
                ctx.json(new User.Builder(username));
            }
        };
    }

    public static class DeleteMessage {

        public static Handler deletePlaygroundMessageDelete = ctx -> {
            Controller.getInstance(DataSource.getTestDB()).removePlaygroundMessage(ctx.pathParam(PLAYGROUND_MESSAGE_ID));
            if (true)
                ctx.status(200).result("Message deleted successfully");
            else
                ctx.status(404).result("Couldn't delete message");
        };

    }
}
