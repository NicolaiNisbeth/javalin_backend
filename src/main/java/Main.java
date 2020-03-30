import database.dao.Controller;
import io.javalin.Javalin;
import resources.GalgelegResource;
import resources.UserAdminResource;

import java.util.HashMap;
import java.util.HashSet;

public class Main {
    public static Javalin app;
    private static HashMap<String, String> profileImages;

    public static void main(String[] args) throws Exception {
        profileImages = new HashMap<>();
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
                ctx.json(UserAdminResource.createUser(ctx.body(), ctx)).contentType("json"));
        //app.post("rest/update_user", ctx ->
         //       ctx.json(UserAdminResource.updateUser2(ctx.body(), ctx)).contentType("json"));
        app.post("rest/update_user", ctx -> UserAdminResource.updateUser2(ctx.body(), ctx));
        app.get("rest/user_list", ctx ->
                ctx.json(Controller.getInstance().getUsers()).contentType("json"));
        app.post("rest/remove_user", ctx ->
                ctx.json(UserAdminResource.deleteUser(ctx.body(), ctx)).contentType("json"));



        app.get("rest/user_list/images", ctx ->
                ctx.json(getPictures()).contentType("json"));

        app.get("rest/set_image", ctx ->
                ctx.json(getPictures()).contentType("json"));

    }

    private static Object getPictures() {
       return profileImages.put("s185020", "");
    }
}
