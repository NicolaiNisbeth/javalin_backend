import database.dao.Controller;
import io.javalin.Javalin;
import javalin_resources.*;
import javalin_resources.HttpMethods.Delete;
import javalin_resources.HttpMethods.Get;
import javalin_resources.HttpMethods.Post;
import javalin_resources.HttpMethods.Put;
import javalin_resources.Util.Path;

import static io.javalin.apibuilder.ApiBuilder.*;

import java.io.*;
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

        java.nio.file.Path pathProfileImages = Paths.get(homeFolder.toPath().toString() + "/server_resource/profile_images");
        File serverResProfileImages = new File(pathProfileImages.toString());
        java.nio.file.Path pathPlaygrounds = Paths.get(homeFolder.toPath().toString() + "/server_resource/playgrounds");
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
        app = Javalin.create(config -> {
            config.enableCorsForAllOrigins()
                    .addSinglePageRoot("", "/webapp/index.html")
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

        //NJL - er i brug
        app.get("rest/playground_list", ctx ->
                ctx.json(Controller.getInstance().getPlaygrounds()).contentType("json"));
/*        app.post("rest/user_login", ctx ->
                ctx.json(UserLogin.verifyLogin(ctx)).contentType("json"));*/

        app.get("/rest/employee/:username/profile-picture", ctx ->
                ctx.result(UserAdminResource.getProfilePicture(ctx.pathParam("username"))).contentType("image/png"));
        app.post("rest/employee/create", ctx ->
                ctx.json(UserAdminResource.createUser(ctx)).contentType("json"));
        app.put("rest/employee/update", ctx ->
                ctx.json(UserAdminResource.updateUser(ctx)).contentType("json"));
        app.get("rest/employee/all", ctx ->
                ctx.json(Controller.getInstance().getUsers()).contentType("json"));
        app.post("rest/employee/delete", ctx ->
                ctx.json(UserAdminResource.deleteUser(ctx)).contentType("json"));

        app.routes(() -> {


            /**
             * GET
             **/

            //Works
            get(Path.Playground.PLAYGROUND_ALL, Get.GetPlayground.readAllPlaygroundsGet);
            get(Path.Playground.PLAYGROUND_ONE, Get.GetPlayground.readOnePlaygroundGet);
            //Works
            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Get.GetPlayground.readOnePlaygroundOneEmployeeGet);
            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ALL, Get.GetPlayground.readOnePlaygroundAllEmployeeGet);
            // Works
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Get.GetEvent.readOneEventParticipantsGet);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Get.GetEvent.readOneEventOneParticipantGet);
            //works
            get(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Get.GetEvent.readOnePlayGroundAllEventsGet);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Get.GetEvent.readOneEventGet);
            //works
            get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, Get.GetMessage.readAllMessagesGet);
            get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Get.GetMessage.readOneMessageGet);


            /**
             * POST
             **/
            //works
            post(Path.Playground.PLAYGROUND_ALL, Post.PostPlayground.createPlaygroundPost);
            //work
            post(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Post.PostEvent.createPlaygroundEventPost);
            //works
            post(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, Post.PostMessage.createPlaygroundMessagePost);

            //TODO: Implement this
            post(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ALL, Post.PostPedagogue.createPedagogueToPlaygroundPost);
            post(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Post.PostUser.createParticipantsToPlaygroundEventPost);

            // User
            post(Path.Employee.LOGIN, Post.PostUser.createUserLoginPost);


            /**
             * PUT
             **/
            //works
            put(Path.Playground.PLAYGROUND_ONE, Put.PutPlayground.updatePlaygroundPut);
            //works
            put(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Put.PutMessage.updatePlaygroundMessagePut);
            //works
            put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Put.PutEvent.updateEventToPlaygroundPut);

            //TODO: Test this
            put(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Put.PutPedagogue.updatePedagogueToPlayGroundPut);
            put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, ctx -> ctx.json(Put.PutUser.updateUserToPlaygroundEventPut).contentType("json"));

            /**
             * DELETE
             **/
            //works
            delete(Path.Playground.PLAYGROUND_ONE, Delete.DeletePlayground.deleteOnePlaygroundDelete);
            //works
            delete(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Delete.DeletePedagogue.deletePedagogueFromPlaygroundDelete);
            //works
            delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Delete.DeleteEvent.deleteEventFromPlaygroundDelete);
            //works
            delete(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Delete.DeleteMessage.deletePlaygroundMessageDelete);

            //TODO: Test this
            delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Delete.DeleteUser.deleteParticipantFromPlaygroundEventDelete);


            delete("rest/employee/delete", ctx ->
                    ctx.json(UserAdminResource.deleteUser(ctx)).contentType("json"));
        });
    }
}

