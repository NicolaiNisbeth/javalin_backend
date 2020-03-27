import database.dao.Controller;
import database.utils.Path;
import io.javalin.Javalin;
import io.javalin.http.Context;
import resources.GalgelegResource;
import resources.PlaygroundResource;
import resources.UserAdminResource;
import resources.UserLogin;

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
         before(UserLogin.confirmlogin);
         get(Path.Playground.PLAYGROUND_ALL, PlaygroundResource.AllPlaygroundsHandlerGet);
         get(Path.Playground.PLAYGROUND_ONE, PlaygroundResource.OnePlaygroundHandlerGet);

        });
    }

    public static void usermethods() throws Exception {
        /*
        User getUser(User activeUser, String userID) throws DALException;

        void createUser(User activeUser, User userToBeCreated) throws DALException;
        void updateUser(User activeUser, User updatedUser) throws DALException;
        void deleteUser(User activeUser, String userID) throws DALException;
        */


        // username og password er den bruger der selv er logget på.
        app.get("users/:id/:username/:password", ctx -> {
            User user = UserLogin.verificerLogin(ctx.body(),ctx);
            if (user != null) {
            ctx.json(Controller.getController().getUser(user,ctx.pathParam("id"))).contentType("json");
            ctx.status(200);
            }
            else
                // 401 is status code for unauthorized .
                ctx.status(401);
        });

        //app.post("users/:id/:username", ctx -> ctx.json(Controller.getController().createUser(ctx.pathParam())))

        //app.routes(() -> { crud("users/:id", Controller.getController().get); });

        //app.post("users/:id", () -> { })
    }
}
