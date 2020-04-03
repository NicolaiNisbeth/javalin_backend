import database.dao.Controller;
import io.javalin.Javalin;
import javalin_resources.*;
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

        app.get("rest/galgeleg/highscore", ctx ->
                ctx.json(GalgelegResource.getHighscoreListe()).contentType("json"));
        app.post("rest/galgeleg/:username", ctx ->
                ctx.result(GalgelegResource.startGame(ctx.pathParam("username"))).contentType("json"));
        app.get("rest/galgeleg/:username/:guess", ctx ->
                ctx.result(GalgelegResource.makeGuess(ctx.pathParam("username"), ctx.pathParam("guess"))).contentType("json"));

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
            get(Path.Playground.PLAYGROUND_ALL, ctx -> ctx.json(Controller.getInstance().getPlaygrounds()).contentType("json"));
            get(Path.Playground.PLAYGROUND_ONE, PlaygroundResource.OnePlaygroundGet);
            //Works
            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, PlaygroundResource.OnePlaygroundOneEmployeeHandlerGet);
            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ALL, PlaygroundResource.OnePlaygroundAllEmployeeHandlerGet);
            // Works
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS, EventRessource.OneEventParticipantsHandlerGet);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, EventRessource.OneEventOneParticipantHandlerGet);
            //works
            get(Path.Playground.PLAYGROUNDS_ONE_ALL_EVENTS, EventRessource.PlayGroundAllEventsHandlerGet);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT, EventRessource.OneEventHandlerGet);
            //works
            get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, MessageRessource.AllMessageHandlerGet);
            get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, MessageRessource.OneMessageHandlerGet);


            //POST

            //works
            post(Path.Playground.PLAYGROUND_ALL, PlaygroundResource.CreatePlaygroundHandlerPost);
            //work
            post(Path.Playground.PLAYGROUNDS_ONE_ALL_EVENTS, PlaygroundResource.addPlaygroundEventPost);
            //works
            post(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, MessageRessource.PlaygroundMessageInsertPost);


            //PUT

            //works
            put(Path.Playground.PLAYGROUND_ONE, PlaygroundResource.UpdatePlaygroundHandlerPut);
            //works
            put(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, MessageRessource.PlaygroundMessageUpdatePut);
            //works
            put(Path.Playground.PLAYGROUNDS_ONE_EVENT, PlaygroundResource.updateEventToPlaygroundPut);


            //DELETE

            //works
            delete(Path.Playground.PLAYGROUND_ONE, PlaygroundResource.DeleteOnePlaygroundDelete);
            //works
            delete(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, PlaygroundResource.removePedagogueFromPlaygroundDelete);
            //works
            delete(Path.Playground.PLAYGROUNDS_ONE_EVENT, PlaygroundResource.removeEventFromPlaygroundDelete);
        });
    }
}
