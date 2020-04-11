import database.dao.Controller;
import io.javalin.Javalin;
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
                    .addSinglePageRoot("", "/webapp/index.html");
        }).start(8088);

        app.before(ctx -> {
            System.out.println("Javalin Server fik " + ctx.method() + " på " + ctx.url() + " med query " + ctx.queryParamMap() + " og form " + ctx.formParamMap());
        });
        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
        });
        app.config.addStaticFiles("webapp");

        // REST endpoints
        app.routes(() -> {

            /**
             * GET
             **/

            //GET PLAYGROUNDS
            get(Path.Playground.PLAYGROUND_ALL, Get.GetPlayground.readAllPlaygroundsGet);
            get(Path.Playground.PLAYGROUND_ONE, Get.GetPlayground.readOnePlaygroundGet);
            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Get.GetPlayground.readOnePlaygroundOneEmployeeGet);
            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ALL, Get.GetPlayground.readOnePlaygroundAllEmployeeGet);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Get.GetEvent.readOneEventParticipantsGet);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Get.GetEvent.readOneEventOneParticipantGet);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Get.GetEvent.readOnePlayGroundAllEventsGet);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Get.GetEvent.readOneEventGet);
            get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, Get.GetMessage.readAllMessagesGet);
            get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Get.GetMessage.readOneMessageGet);

            //GET EMPLOYEES
            // NJL
            get(Path.Employee.EMPLOYEE_ALL, ctx ->
                    ctx.json(Controller.getInstance().getUsers()).contentType("json"));
            get(Path.Employee.EMPLOYEE_ONE_PROFILE_PICTURE, ctx ->
                    ctx.result(Get.GetUser.getProfilePicture(ctx.pathParam("username"))).contentType("image/png"));

            /**
             * POST
             **/

            //POST PLAYGROUNDS
            //WORKS
            post(Path.Playground.PLAYGROUND_ALL, Post.PostPlayground.createPlaygroundPost);
            post(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Post.PostEvent.createPlaygroundEventPost);
            post(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, Post.PostMessage.createPlaygroundMessagePost);

            //TODO: Implement this
            //post(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ALL, Post.PostPedagogue.createPedagogueToPlaygroundPost);
            post(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Post.PostUser.createParticipantsToPlaygroundEventPost);

            // NJL
            //POST EMPLOYEES
            post(Path.Employee.LOGIN, ctx -> ctx.json(Post.PostUser.userLogin(ctx)).contentType("json"));
            post(Path.Employee.CREATE, ctx -> ctx.json(Post.PostUser.createUser(ctx)).contentType("json"));

            /**
             * PUT
             **/
            //PUT PLAYGROUNDS
            put(Path.Playground.PLAYGROUND_ONE, Put.PutPlayground.updatePlaygroundPut);
            //put(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Put.PutMessage.updatePlaygroundMessagePut);
            put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Put.PutEvent.updateEventToPlaygroundPut);

            //TODO: Test this
            put(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Put.PutPedagogue.updatePedagogueToPlayGroundPut);
            put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, ctx -> ctx.json(Put.PutUser.updateUserToPlaygroundEventPut).contentType("json"));

            //PUT EMLOYEES
            // NJL
            put(Path.Employee.UPDATE, ctx -> ctx.json(Put.PutUser.updateUser(ctx)).contentType("json"));
            put(Path.Employee.RESET_PASSWORD, ctx -> ctx.json(Put.PutUser.resetPassword(ctx)).contentType("json"));

            /**
             * DELETE
             **/
            //DELETE PLAYGROUNDS
            delete(Path.Playground.PLAYGROUND_ONE, Delete.DeletePlayground.deleteOnePlaygroundDelete);
            delete(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Delete.DeletePedagogue.deletePedagogueFromPlaygroundDelete);
            delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Delete.DeleteEvent.deleteEventFromPlaygroundDelete);
            delete(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Delete.DeleteMessage.deletePlaygroundMessageDelete);

            //TODO: Test this
            delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Delete.DeleteUser.deleteParticipantFromPlaygroundEventDelete);

            //DELETE EMPLOYEES
            // NJL
            delete(Path.Employee.DELETE, ctx -> ctx.json(Delete.DeleteUser.deleteUser(ctx)).contentType("json"));
        });
    }
}
