package javalin_resources.HttpMethods;

import database.dao.Controller;
import io.javalin.http.Handler;
import org.json.JSONObject;

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

        public static Handler deleteUserFromPlaygroundEventDelete = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            if (Controller.getInstance().removeUserFromPlaygroundEvent(jsonObject.getString(EVENT_ID), jsonObject.getString(USER_ID)))
                ctx.status(200).result("Removed user from event");
            else
                ctx.status(404).result("Couldn't remove user from event");
        };

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

        public static Handler removePlaygroundMessageHandlerDelete = ctx -> {
            if (Controller.getInstance().removePlaygroundMessage(ctx.pathParam(PLAYGROUND_MESSAGE_ID)))
                ctx.status(200);
            else
                ctx.status(404);
        };

    }



}
