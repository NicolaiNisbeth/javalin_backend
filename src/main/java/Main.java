import database.dao.Controller;
import io.javalin.Javalin;
import javalin_resources.*;
import javalin_resources.HttpMethods.Delete;
import javalin_resources.HttpMethods.Get;
import javalin_resources.HttpMethods.Post;
import javalin_resources.HttpMethods.Put;
import javalin_resources.Util.Path;

import static io.javalin.apibuilder.ApiBuilder.*;


public class Main {
    public static Javalin app;


    public static void main(String[] args) throws Exception {
        start();

    }

    public static void stop() {
        app.stop();
        app = null;
    }

    public static void start() throws Exception {
        if (app != null) return;

        app = Javalin.create(config -> {
            config.enableCorsForAllOrigins();
        }).start(8090);


        app.before(ctx -> {
            System.out.println("Javalin Server fik " + ctx.method() + " på " + ctx.url() + " med query " + ctx.queryParamMap() + " og form " + ctx.formParamMap());
        });
        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
        });
        app.config.addStaticFiles("webapp");

        // REST endpoints
        app.get("/rest/hej", ctx -> ctx.result("Hejsa, godt at møde dig!"));
        app.get("/rest/hej/:fornavn", ctx -> ctx.result("Hej " + ctx.queryParam("fornavn") + ", godt at møde dig!"));

        //NJL - er i brug
        app.get("rest/playground_list", ctx ->
                ctx.json(Controller.getInstance().getPlaygrounds()).contentType("json"));
        app.post("rest/user_login", ctx ->
                ctx.json(UserAdminResource.verifyLogin(ctx.body(), ctx)).contentType("json"));
        app.post("rest/create_user", ctx ->
                ctx.json(UserAdminResource.createUser(ctx.body(), ctx)).contentType("json"));
        app.put("rest/update_user", ctx ->
                ctx.json(UserAdminResource.updateUser(ctx.body(), ctx)).contentType("json"));
        app.get("rest/user_list", ctx ->
                ctx.json(Controller.getInstance().getUsers()).contentType("json"));
        app.post("rest/remove_user", ctx ->
                ctx.json(UserAdminResource.deleteUser(ctx.body(), ctx)).contentType("json"));

        app.routes(() -> {


            //GET

            //Works
            get(Path.Playground.PLAYGROUND_ALL, Get.GetPlayground.allPlaygroundsGet);
            get(Path.Playground.PLAYGROUND_ONE, Get.GetPlayground.onePlaygroundGet);
            //Works
            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Get.GetPlayground.onePlaygroundOneEmployeeGet);
            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ALL, Get.GetPlayground.onePlaygroundAllEmployeeGet);
            // Works
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS, Get.GetEvent.oneEventParticipantsGet);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Get.GetEvent.oneEventOneParticipantGet);
            //works
            get(Path.Playground.PLAYGROUNDS_ONE_ALL_EVENTS, Get.GetEvent.playGroundAllEventsGet);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT, Get.GetEvent.oneEventGet);
            //works
            get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, Get.GetMessage.AllMessagesGet);
            get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Get.GetMessage.OneMessageGet);


            //POST

            //works
            post(Path.Playground.PLAYGROUND_ALL, Post.PostPlayground.createPlaygroundPost);
            //work
            post(Path.Playground.PLAYGROUNDS_ONE_ALL_EVENTS, Post.PostEvent.addPlaygroundEventPost);
            //works
            post(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, MessageRessource.PlaygroundMessageInsertPost);


            //PUT

            //works
            put(Path.Playground.PLAYGROUND_ONE, Put.PutPlayground.updatePlaygroundPut);
            //works
            put(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, MessageRessource.PlaygroundMessageUpdatePut);
            //works
            put(Path.Playground.PLAYGROUNDS_ONE_EVENT, Put.PutEvent.updateEventToPlaygroundPut);


            //DELETE

            //works
            delete(Path.Playground.PLAYGROUND_ONE, Delete.DeletePlayground.removeOnePlaygroundDelete);
            //works
            delete(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Delete.DeletePedagogue.removePedagogueFromPlaygroundDelete);
            //works
            delete(Path.Playground.PLAYGROUNDS_ONE_EVENT, Delete.DeleteEvent.removeEventFromPlaygroundDelete);
            //works
            delete(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, MessageRessource.removePlaygroundMessageHandlerDelete);
        });
    }
}
