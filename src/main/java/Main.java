import database.dao.Controller;
import io.javalin.Javalin;
import resources.GalgelegResource;
import resources.UserAdminResource;
import resources.UserLoginResource;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static Javalin app;

    public static void main(String[] args) throws Exception {
        buildDirectories();
        start();
    }

    private static void buildDirectories() {
        System.out.println("Server: Starting directories inquiry");
        File homeFolder = new File(System.getProperty("user.home"));
        Path pathProfileImages = Paths.get(homeFolder.toPath().toString() + "/server_resource/profile_images");
        File serverResProfileImages = new File(pathProfileImages.toString());
        Path pathPlaygrounds = Paths.get(homeFolder.toPath().toString() + "/server_resource/playgrounds");
        File serverResPlaygrounds = new File(pathPlaygrounds.toString());

        if (serverResProfileImages.exists()) {
            System.out.println("Server: Directories exists from path: " + homeFolder.toString());
        } else {
            boolean dirCreated = serverResProfileImages.mkdirs();
            boolean dir2Created = serverResPlaygrounds.mkdir();
            if (dirCreated && dir2Created) {
                System.out.println("Server: Directories is build at path: " + homeFolder.toString());
            }
        }
    }

    public static void stop() {
        app.stop();
        app = null;
    }

    public static void start() throws Exception {
        if (app != null) return;
/* .addSinglePageRoot("/resouces", "/webapp/index.html")*/
        app = Javalin.create(config -> {
            config.enableCorsForAllOrigins()
                   ;
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
                ctx.json(UserLoginResource.verifyLogin(ctx)).contentType("json"));

        app.get("/rest/user/:username/profile-picture", ctx ->
                ctx.result(UserAdminResource.getProfilePicture(ctx.pathParam("username"))).contentType("image/png"));


        app.post("rest/create_user", ctx ->
                ctx.json(UserAdminResource.createUser(ctx)).contentType("json"));
        app.get("rest/user_list", ctx ->
                ctx.json(Controller.getInstance().getUsers()).contentType("json"));
        app.post("rest/remove_user", ctx ->
                ctx.json(UserAdminResource.deleteUser(ctx.body(), ctx)).contentType("json"));
        app.put("rest/update_user", ctx ->
                ctx.json(UserAdminResource.updateUser(ctx)).contentType("json"));
    }
}
