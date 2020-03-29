import database.dao.Controller;
import javalin_resources.Util.Path;
import io.javalin.Javalin;
import javalin_resources.*;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.post;

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
        }).start(8088);


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
                ctx.result(UserAdminResource.createUser(ctx.body(), ctx)));
        app.put("rest/update_user", ctx ->
                ctx.result(UserAdminResource.createUser(ctx.body(), ctx)));
        app.get("rest/user_list", ctx ->
                ctx.json(Controller.getInstance().getUsers()).contentType("json"));
        app.post("rest/remove_user", ctx ->
                ctx.result(UserAdminResource.deleteUser(ctx.body(), ctx)));

        app.routes(() -> {

            get(Path.Playground.PLAYGROUND_ALL, PlaygroundResource.AllPlaygroundsHandlerGet);
            get(Path.Playground.PLAYGROUND_ONE, PlaygroundResource.OnePlaygroundHandlerGet);

            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ALL, PlaygroundResource.OnePlaygroundAllEmployeeHandlerGet);
            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, PlaygroundResource.OnePlaygroundOneEmployeeHandlerGet);

            get(Path.Playground.PLAYGROUNDS_ONE_ALL_EVENTS, EventRessource.PlayGroundAllEventsHandlerGet);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT, EventRessource.OneEventHandlerGet);

        });
    }
}
