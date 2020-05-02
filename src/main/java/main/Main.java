package main;

import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.ReDocOptions;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.prometheus.client.exporter.HTTPServer;
import io.swagger.v3.oas.models.info.Info;
import javalin_resources.HttpMethods.*;
import javalin_resources.Util.Path;
import monitor.QueuedThreadPoolCollector;
import monitor.StatisticsHandlerCollector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import static io.javalin.apibuilder.ApiBuilder.*;

import java.io.*;
import java.net.InetAddress;
import java.nio.file.Paths;

public class Main {
    public static Javalin app;
    private static final int port = 8080;
    private static String hostAddress;

    public static void main(String[] args) throws Exception {
        String hostName = InetAddress.getLocalHost().getHostName();
        hostAddress = hostName.equals("aws-ec2-javalin-hoster") ? "18.185.121.182" : "localhost";
        System.out.println("Starting server from " + hostAddress);
        System.out.println(String.format("Listening on %s:%s", InetAddress.getLocalHost().getHostAddress(), port));

        buildDirectories();
        start();
    }

    public static void start() throws Exception {

        StatisticsHandler statisticsHandler = new StatisticsHandler();
        QueuedThreadPool queuedThreadPool = new QueuedThreadPool(200, 8, 60_000);
        initializePrometheus(statisticsHandler, queuedThreadPool);

        if (app != null) return;
        app = Javalin.create(config -> config.enableCorsForAllOrigins()
                .addSinglePageRoot("", "/webapp/index.html")
                .addStaticFiles("webapp")
                .server(() -> {
                    Server server = new Server(queuedThreadPool);
                    server.setHandler(statisticsHandler);
                    return server;
                })).start(port);

        app.before(ctx -> System.out.println(
                String.format("Javalin Server fik %s på %s med query %s og form %s",
                        ctx.method(), ctx.url(), ctx.queryParamMap(), ctx.formParamMap()))
        );

        app.exception(Exception.class, (e, ctx) -> e.printStackTrace());


        // REST endpoints
        app.routes(() -> {
            /** GET **/
            get("/rest", ctx -> {
                InputStream targetStream = Get.class.getResourceAsStream("/docs/swagger.json");
                ctx.result(targetStream).contentType("json");

            });
            get(Path.Employee.EMPLOYEE_ALL, Get.User.getAllUsers);
            get(Path.Employee.EMPLOYEE_ONE_PROFILE_PICTURE, Get.User.getUserPicture);
            get(Path.Playground.PLAYGROUND_ONE, Get.Playground.readOnePlayground);
            get(Path.Playground.PLAYGROUND_ALL, Get.Playground.readAllPlaygrounds);
            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Get.Playground.readOnePlaygroundOneEmployee);
            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ALL, Get.Playground.readOnePlaygroundAllEmployee);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Get.Event.readOneEvent);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Get.Event.readOneEventOneParticipant);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Get.Event.readOneEventParticipants);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Get.Event.readOnePlayGroundAllEvents);
            get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Get.Message.readOneMessage);
            get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, Get.Message.readAllMessages);

            /** POST **/
            post(Path.Employee.LOGIN, Post.User.userLogin);
            post(Path.Employee.CREATE, Post.User.createUser);
            post(Path.Playground.PLAYGROUND_ALL, Post.Playground.createPlayground);
            post(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Post.Event.createPlaygroundEvent);
            post(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Post.Event.createUserToPlaygroundEvent);
            post(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, Post.Message.createPlaygroundMessage);
            //TODO: Implement this
            //skal foregå under create/update user post(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ALL, Post.PostUser.createUserToPlaygroundPost);
            post(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Post.User.createParticipantsToPlaygroundEvent);
            post("/rest/employee/imagetest", context -> Shared.saveProfilePicture2(context));

            /** PUT **/
            put(Path.Employee.UPDATE, Put.User.updateUser);
            put(Path.Employee.RESET_PASSWORD, Put.User.resetPassword);
            put(Path.Playground.PLAYGROUND_ONE, Put.Playground.updatePlayground);
            put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Put.Event.updateEventToPlayground);
            put(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Put.Message.updatePlaygroundMessage);
            //TODO: Test this
            put(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Put.Pedagogue.updatePedagogueToPlayGround);
            // put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Put.PutUser.updateUserToPlaygroundEventPut);

            /** DELETE **/
            delete(Path.Employee.DELETE, Delete.User.deleteUser);
            delete(Path.Playground.PLAYGROUND_ONE, Delete.Playground.deleteOnePlayground);
            delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Delete.Event.deleteEventFromPlayground);
            delete(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Delete.Message.deletePlaygroundMessage);
            delete(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Delete.Pedagogue.deletePedagogueFromPlayground);
            //TODO: Test this
            //delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Delete.DeleteUser.deleteParticipantFromPlaygroundEventDelete);
            delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Delete.Event.remoteUserFromPlaygroundEvent);
            delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Delete.User.deleteParticipantFromPlaygroundEvent);
        });
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

    private static void initializePrometheus(StatisticsHandler statisticsHandler, QueuedThreadPool queuedThreadPool) throws IOException {
        StatisticsHandlerCollector.initialize(statisticsHandler); // collector is included in source code
        QueuedThreadPoolCollector.initialize(queuedThreadPool); // collector is included in source code
        HTTPServer prometheusServer = new HTTPServer(7080);
    }

    public static String getHostAddress() {
        return hostAddress;
    }
}
