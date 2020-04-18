import database.dao.Controller;
import io.javalin.Javalin;
import javalin_resources.HttpMethods.*;
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
            get(Path.Employee.EMPLOYEE_ALL, Get.GetUser.getAllUsers);
            get(Path.Employee.EMPLOYEE_ONE_PROFILE_PICTURE, Get.GetUser.getUserPicture);

            /**
             * POST
             **/

            //POST PLAYGROUNDS
            //WORKS
            post(Path.Playground.PLAYGROUND_ALL, Post.PostPlayground.createPlaygroundPost);
            post(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Post.PostEvent.createPlaygroundEventPost);
            post(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, Post.PostMessage.createPlaygroundMessagePost);

            //TODO: Implement this
            post(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ALL, Post.PostUser.createUserToPlaygroundPost);
            post(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Post.PostUser.createParticipantsToPlaygroundEventPost);


            //POST EMPLOYEES
            post(Path.Employee.LOGIN, Post.PostUser.userLogin);
            post(Path.Employee.CREATE, Post.PostUser.createUser);
            post("/rest/employee/imagetest", context -> {
                Shared.saveProfilePicture2(context);
            });


            /**
             * PUT
             **/
            //PUT PLAYGROUNDS
            put(Path.Playground.PLAYGROUND_ONE, Put.PutPlayground.updatePlaygroundPut);
            put(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Put.PutMessage.updatePlaygroundMessagePut);
            put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Put.PutEvent.updateEventToPlaygroundPut);

            //TODO: Test this
            put(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Put.PutPedagogue.updatePedagogueToPlayGroundPut);
            put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Put.PutUser.updateUserToPlaygroundEventPut);

            //PUT EMLOYEES
            put(Path.Employee.UPDATE, Put.PutUser.updateUser);
            put(Path.Employee.RESET_PASSWORD, Put.PutUser.resetPassword);

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
            delete(Path.Employee.DELETE, Delete.DeleteUser.deleteUser);
        });
    }
}
