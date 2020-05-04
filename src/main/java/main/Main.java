package main;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.reprezen.kaizen.oasparser.model3.Info;
import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.prometheus.client.exporter.HTTPServer;
import javalin_resources.HttpMethods.JWTHandler;
import javalin_resources.Util.Path;
import javalin_resources.http_methods.*;
import javalinjwt.JavalinJWT;
import monitor.QueuedThreadPoolCollector;
import monitor.StatisticsHandlerCollector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import static io.javalin.apibuilder.ApiBuilder.*;

import java.io.*;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.Optional;

public class Main {
    public static Javalin app;
    private static final int port = 8080;



    public static void main(String[] args) throws Exception {
        String hostName = InetAddress.getLocalHost().getHostName();
        String hostAddress = hostName.equals("aws-ec2-javalin-hoster") ? "18.185.121.182" : "localhost";
        System.out.println("Starting server from " + hostAddress);
        System.out.println(String.format("Listening on %s", InetAddress.getLocalHost().getHostAddress()));

        buildDirectories();
        start();
    }


    public static void start() throws Exception {

        StatisticsHandler statisticsHandler = new StatisticsHandler();
        QueuedThreadPool queuedThreadPool = new QueuedThreadPool(200, 8, 60_000);
        initializePrometheus(statisticsHandler, queuedThreadPool);

        if (app != null) return;
        app = Javalin.create(config -> config.enableCorsForAllOrigins()
                .registerPlugin(getConfiguredOpenApiPlugin())
                .addSinglePageRoot("", "/webapp/index.html")
                .addStaticFiles("webapp")
                .server(() -> {
                    Server server = new Server(queuedThreadPool);
                    server.setHandler(statisticsHandler);
                    return server;
                })).start(port);

        System.out.println("Check out Swagger UI docs at http://localhost:8080/rest");
        System.out.println("Check out OpenAPI docs at http://localhost:8080/rest-docs");

        app.before(ctx -> System.out.println(
                String.format("Javalin Server fik %s på %s med query %s og form %s",
                        ctx.method(), ctx.url(), ctx.queryParamMap(), ctx.formParamMap()))
        );

        app.routes(() -> {

            // REST endpoints
            app.routes(() -> {

                app.before("/*", ctx -> {
                    String source = "Authfilter";

                    Optional<DecodedJWT> decodedJWT = JavalinJWT.getTokenFromHeader(ctx).flatMap(JWTHandler.provider::validateToken);
                    System.out.println(source);

                    if (!decodedJWT.isPresent()) {
                        System.out.println(source+": No/or altered token");

                        //Redirection to a responsemessage, providing with informaion on how to post a login request.

                    } else {
                        System.out.println("Token is present");
                    }

                });

/** USERS **/
                get(Path.User.USERS_ALL, User.getAllUsers);
                get(Path.User.USERS_ALL_EMPLOYEES, User.getAllEmployees);
                get(Path.User.USERS_ONE_PROFILE_PICTURE, User.getUserPicture);

                put(Path.User.USERS_CRUD, User.updateUser);
                put(Path.User.USERS_RESET_PASSWORD, User.resetPassword);

                post(Path.User.USERS_LOGIN, User.userLogin);
                post(Path.User.USERS_CRUD, User.createUser);

                delete(Path.User.USERS_CRUD, User.deleteUser);


                /** PLAYGROUNDS **/
                get(Path.Playground.PLAYGROUNDS_ONE, Playground.readOnePlayground);
                get(Path.Playground.PLAYGROUNDS_ALL, Playground.readAllPlaygrounds);
                get(Path.Playground.PLAYGROUNDS_ONE_PEDAGOGUE_ONE, Playground.readOnePlaygroundOneEmployee);
                get(Path.Playground.PLAYGROUNDS_ONE_PEDAGOGUE_ALL, Playground.readOnePlaygroundAllEmployee);
                get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Event.readOneEvent);
                get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Event.readOneEventOneParticipant);
                get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Event.readOneEventParticipants);
                get(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Event.readOnePlayGroundAllEvents);
                get(Path.Playground.PLAYGROUNDS_ONE_MESSAGE_ONE, Message.readOneMessage);
                get(Path.Playground.PLAYGROUNDS_ONE_MESSAGE_ALL, Message.readAllMessages);

                put(Path.Playground.PLAYGROUNDS_ONE, Playground.updatePlayground);
                put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Event.updateEventToPlayground);
                put(Path.Playground.PLAYGROUNDS_ONE_MESSAGE_ONE, Message.updatePlaygroundMessage);

                post(Path.Playground.PLAYGROUNDS_ALL, Playground.createPlayground);
                post(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Event.createPlaygroundEvent);
                post(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Event.createUserToPlaygroundEvent);
                post(Path.Playground.PLAYGROUNDS_ONE_MESSAGE_ALL, Message.createPlaygroundMessage);

                delete(Path.Playground.PLAYGROUNDS_ONE, Playground.deleteOnePlayground);
                delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Event.deleteEventFromPlayground);
                delete(Path.Playground.PLAYGROUNDS_ONE_MESSAGE_ONE, Message.deletePlaygroundMessage);
                delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Event.deleteUserFromPlaygroundEvent);

                /** MESSAGES **/

                get(Path.Message.MESSAGE_IMAGE_ONE, Message.getMessageImage);

                /** EVENTS **/

                //TODO: Tag stilling til disse
                //put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Put.PutUser.updateUserToPlaygroundEventPut);
                //delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Delete.DeleteUser.deleteParticipantFromPlaygroundEventDelete);
                //delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Delete.User.deleteParticipantFromPlaygroundE
            });
        });
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

    private static OpenApiPlugin getConfiguredOpenApiPlugin() {
        Info info = new Info().version("1.0").title("Københavns Legepladser API").description(
                "The REST API is a student project made to make the public playgrounds of " +
                        "Copenhagen Municipality more accessible." +
                        "The API's endpoints is visible in the list below" +
                        "This documentation is a draft.");

        OpenApiOptions options = new OpenApiOptions(info)
                .activateAnnotationScanningFor("kbh-legepladser-api")
                // endpoint for OpenAPI json
                .path("/rest-docs")
                // endpoint for swagger-ui
                .swagger(new SwaggerOptions("/rest"))
                .defaultDocumentation(doc -> {
                });
        return new OpenApiPlugin(options);
    }

    private static void buildDirectories() {
        File homeFolder = new File(System.getProperty("user.home"));
        java.nio.file.Path pathProfileImages = Paths.get(homeFolder.toPath().toString() + "/server_resource/profile_images");
        File serverResProfileImages = new File(pathProfileImages.toString());
        java.nio.file.Path pathMessageImages = Paths.get(homeFolder.toPath().toString() + "/server_resource/message_images");
        File serverResMessageImages = new File(pathMessageImages.toString());
        java.nio.file.Path pathPlaygrounds = Paths.get(homeFolder.toPath().toString() + "/server_resource/playgrounds");
        File serverResPlaygrounds = new File(pathPlaygrounds.toString());

        if (serverResProfileImages.exists() && serverResMessageImages.exists() && serverResPlaygrounds.exists()) {
            System.out.println(String.format("Server: Using resource directories from path: %s\\server_resource\\", homeFolder.toString()));
        } else {
            boolean userDirCreated = serverResProfileImages.mkdirs();
            boolean playgroundDirCreated = serverResPlaygrounds.mkdir();
            boolean messageDirCreated = serverResMessageImages.mkdirs();

            if (userDirCreated || messageDirCreated || playgroundDirCreated) {
                System.out.println(String.format("Server: Resource directories is build at path: %s\\server_resource", homeFolder.toString()));
            }
        }
    }

    private static String unpackToken(io.javalin.http.Context ctx, String infoToRetrive){
        Optional<DecodedJWT> decodedJWT = JavalinJWT.getTokenFromHeader(ctx)
                .flatMap(JWTHandler.provider::validateToken);

        return decodedJWT.get().getClaim(infoToRetrive).asString();

    }
}
