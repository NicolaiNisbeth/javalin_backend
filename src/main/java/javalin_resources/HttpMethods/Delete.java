package javalin_resources.HttpMethods;

import database.DALException;
import database.collections.User;
import database.dao.Controller;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.json.JSONObject;

import java.util.List;

public class Delete implements Tag {

    public static class DeletePlayground {

        public static Handler deleteOnePlaygroundDelete = ctx -> {
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

        public static Handler deleteParticipantFromPlaygroundEventDelete = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            if (Controller.getInstance().removeUserFromPlaygroundEvent(jsonObject.getString(EVENT_ID), jsonObject.getString(USER_ID)))
                ctx.status(200).result("Removed user from event");
            else
                ctx.status(404).result("Couldn't remove user from event");
        };


        public static List<User> deleteUser(Context ctx) {
            JSONObject jsonObject = null, deleteUserModel = null;
            jsonObject = new JSONObject(ctx.body());
            deleteUserModel = jsonObject.getJSONObject("deleteUserModel");
            String usernameAdmin = deleteUserModel.getString(USERNAME_ADMIN);
            String passwordAdmin = deleteUserModel.getString(PASSWORD_ADMIN);
            String username = deleteUserModel.getString(USERNAME);
            // todo slet ham fra legeplader også
            //  JSONArray adminRightsOfNewUser = jsonObject.getJSONArray("userAdminRights");

            User admin = null;
            try {
                admin = Controller.getInstance().getUser(usernameAdmin);
            } catch (DALException e) {
                e.printStackTrace();
            }
            if (!admin.getPassword().equalsIgnoreCase(passwordAdmin)) {
                ctx.status(401).result("Unauthorized - Forkert kodeord...");
            } else {
                Controller.getInstance().deleteUser(username);
            }
            return Controller.getInstance().getUsers();
        }

    }


    public static class DeletePedagogue {

        public static Handler deletePedagogueFromPlaygroundDelete = ctx -> {
            if (Controller.getInstance().removePedagogueFromPlayground(ctx.pathParam(PLAYGROUND_NAME), ctx.pathParam(USER_NAME)))
                ctx.status(200).result("Pedagogue is removed from the playground");
            else
                ctx.status(404).result("Couldn't remove pedagogue from playground");
        };

    }

    public static class DeleteEvent {

        public static Handler deleteEventFromPlaygroundDelete = ctx -> {
            if (Controller.getInstance().removePlaygroundEvent(ctx.pathParam(EVENT_ID))) {
                ctx.status(200).result("Event has been removed from the playground");
            } else {
                ctx.status(404).result("Couldn't remove event from playground");
            }
        };

    }

    public static class DeleteMessage {

        public static Handler deletePlaygroundMessageDelete = ctx -> {
            if (Controller.getInstance().removePlaygroundMessage(ctx.pathParam(PLAYGROUND_MESSAGE_ID)))
                ctx.status(200).result("Message deleted successfully");
            else
                ctx.status(404).result("Couldn't delete message");
        };

    }
}
