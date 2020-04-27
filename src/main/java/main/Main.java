package main;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import database.collections.User;
import database.dao.Controller;
import io.javalin.Javalin;
import io.prometheus.client.exporter.HTTPServer;
import javalin_resources.HttpMethods.*;
import io.javalin.core.security.Role;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import javalin_resources.HttpMethods.Delete;
import javalin_resources.HttpMethods.Get;
import javalin_resources.HttpMethods.Post;
import javalin_resources.HttpMethods.Put;
import javalin_resources.Util.JWebToken;
import javalin_resources.Util.Path;
import monitor.QueuedThreadPoolCollector;
import monitor.StatisticsHandlerCollector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import javalinjwt.JWTAccessManager;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;
import javalinjwt.JavalinJWT;
import javalinjwt.examples.JWTResponse;
import org.json.JSONObject;


import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.core.security.SecurityUtil.roles;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    public static Javalin app;
    public static HashMap<String, Role> rolesmapping;
    public static HashSet<Role> anyone;
    public static HashSet<Role> pedagogues;
    public static HashSet<Role> admin;

    static Algorithm algorithm = Algorithm.HMAC256("Bennys_polser"); //ja tak venner let's go.
    static JWTProvider provider;
    static JWTVerifier verifier;


    public static void main(String[] args) throws Exception {
        buildDirectories();

        rolesmapping = new HashMap<>();
        rolesmapping.put("anyone", User.roles.ANYONE);
        rolesmapping.put("pedagogue", User.roles.PEDAGOGUE);
        rolesmapping.put("admin", User.roles.ADMIN);

        anyone = new HashSet<>();
        anyone.add(User.roles.ANYONE);

        pedagogues = new HashSet<>();
        pedagogues.add(User.roles.PEDAGOGUE);

        admin = new HashSet<>();
        admin.add(User.roles.ADMIN);

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

    private static void initializePrometheus(StatisticsHandler statisticsHandler, QueuedThreadPool queuedThreadPool) throws IOException {
        StatisticsHandlerCollector.initialize(statisticsHandler); // collector is included in source code
        QueuedThreadPoolCollector.initialize(queuedThreadPool); // collector is included in source code
        HTTPServer prometheusServer = new HTTPServer(7080);
    }

    public static void start() throws Exception {

        StatisticsHandler statisticsHandler = new StatisticsHandler();
        QueuedThreadPool queuedThreadPool = new QueuedThreadPool(200, 8, 60_000);
        initializePrometheus(statisticsHandler, queuedThreadPool);

        if (app != null) return;

        JWTGenerator<User> generator = (user, alg) -> {
            JWTCreator.Builder token = JWT.create().withClaim("Username", user.getUsername()).withClaim("Role", user.getRole());
            return token.sign(alg);
        };


        verifier = JWT.require(algorithm).build();
        provider = new JWTProvider(algorithm, generator, verifier);
        JWTAccessManager accessManager = new JWTAccessManager("Role", rolesmapping, User.roles.ANYONE);

        app = Javalin.create(config -> {
            config.enableCorsForAllOrigins()
                    .accessManager(accessManager)
                    .addSinglePageRoot("", "/webapp/index.html")
                    .server(() -> {
                        Server server = new Server(queuedThreadPool);
                        server.setHandler(statisticsHandler);
                        return server;
                    });
        }).start(8088);


        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
        });
        app.config.addStaticFiles("webapp");



        // REST endpoints
        app.routes(() -> {

            /**
             * BEFORE
             */

            before(ctx -> { System.out.println("Javalin Server fik " + ctx.method() + " på " + ctx.url() + " med query " + ctx.queryParamMap() + " og form " + ctx.formParamMap()); });

            before(JavalinJWT.createHeaderDecodeHandler(provider)); // This takes care of validating the JWT. It then adds it to the ctx for future use.

            get("/test",Get.GetPlayground.readAllPlaygroundsGet, roles(User.roles.ANYONE, User.roles.PEDAGOGUE, User.roles.ADMIN));
            get("/test1",Get.GetPlayground.readAllPlaygroundsGet, roles(User.roles.PEDAGOGUE, User.roles.ADMIN));
            get("/test2", Get.GetPlayground.readAllPlaygroundsGet, roles(User.roles.ADMIN));
            get("/validate", validateJWTHandler, anyone);
            get("/generate", generateJWTHandler, anyone);
            /**
             * GET
             **/
            //GET PLAYGROUNDS
            get(Path.Playground.PLAYGROUND_ALL, Get.GetPlayground.readAllPlaygroundsGet, anyone);
            get(Path.Playground.PLAYGROUND_ONE, Get.GetPlayground.readOnePlaygroundGet, anyone);
            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Get.GetPlayground.readOnePlaygroundOneEmployeeGet, anyone);
            get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ALL, Get.GetPlayground.readOnePlaygroundAllEmployeeGet, anyone);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Get.GetEvent.readOneEventParticipantsGet, anyone);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Get.GetEvent.readOneEventOneParticipantGet, anyone);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Get.GetEvent.readOnePlayGroundAllEventsGet, anyone);
            get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Get.GetEvent.readOneEventGet, anyone);
            get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, Get.GetMessage.readAllMessagesGet, anyone);
            get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Get.GetMessage.readOneMessageGet, anyone);

            //GET EMPLOYEES
            get(Path.Employee.EMPLOYEE_ALL, Get.GetUser.getAllUsers, anyone);
            get(Path.Employee.EMPLOYEE_ONE_PROFILE_PICTURE, Get.GetUser.getUserPicture, anyone);

            /**
             * POST
             **/

            //POST PLAYGROUNDS
            //WORKS
            post(Path.Playground.PLAYGROUND_ALL, Post.PostPlayground.createPlaygroundPost, anyone);
            post(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Post.PostEvent.createPlaygroundEventPost, anyone);
            post(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Post.PostEvent.createUserToPlaygroundEventPost, anyone);
            post(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, Post.PostMessage.createPlaygroundMessagePost, anyone);

            //TODO: Implement this
            post(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Post.PostUser.createUserToPlaygroundPost, anyone);
            post(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Post.PostUser.createParticipantsToPlaygroundEventPost, anyone);


            //POST EMPLOYEES
            post(Path.Employee.LOGIN, Post.PostUser.userLogin, anyone);
            post(Path.Employee.CREATE, Post.PostUser.createUser, anyone);
            post("/rest/employee/imagetest", context -> { Shared.saveProfilePicture2(context); });


            /**
             * PUT
             **/
            //PUT PLAYGROUNDS
            put(Path.Playground.PLAYGROUND_ONE, Put.PutPlayground.updatePlaygroundPut, anyone);
            put(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Put.PutMessage.updatePlaygroundMessagePut, anyone);
            put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Put.PutEvent.updateEventToPlaygroundPut, anyone);

            //TODO: Test this
            put(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Put.PutPedagogue.updatePedagogueToPlayGroundPut, anyone);
            put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Put.PutUser.updateUser, anyone);

            //PUT EMLOYEES
            put(Path.Employee.UPDATE, Put.PutUser.updateUser, anyone);
            put(Path.Employee.RESET_PASSWORD, Put.PutUser.resetPassword, anyone);

            /**
             * DELETE
             **/
            //DELETE PLAYGROUNDS
            delete(Path.Playground.PLAYGROUND_ONE, Delete.DeletePlayground.deleteOnePlaygroundDelete, anyone);
            delete(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Delete.DeletePedagogue.deletePedagogueFromPlaygroundDelete, anyone);
            delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Delete.DeleteEvent.deleteEventFromPlaygroundDelete, anyone);
            delete(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Delete.DeleteMessage.deletePlaygroundMessageDelete, anyone);

            delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Delete.DeleteEvent.remoteUserFromPlaygroundEventPost, anyone);

            //TODO: Test this
            delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Delete.DeleteUser.deleteParticipantFromPlaygroundEventDelete, anyone);

            //DELETE EMPLOYEES
            delete(Path.Employee.DELETE, Delete.DeleteUser.deleteUser, anyone);
        });
    }



    public static Handler generateJWTHandler = ctx -> {
        //JSONObject jsonObject = new JSONObject(ctx.body());
       User user = null;
      // if (jsonObject.has(Get.USER_NAME)) {
      //      user = Controller.getInstance().getUser(jsonObject.getString(Get.USER_NAME));
      //  }

        if (user == null) {
            user = new User.Builder("AnonymousUser").build();
            user.setRoleSet(admin);
            user.setRole("ADMIN");
        }
        String token = provider.generateToken(user);
        ctx.json(new JWTResponse(token));
    };

    public static Handler validateJWTHandler = context -> {
        DecodedJWT decodedJWT = JavalinJWT.getDecodedFromContext(context);
        context.result("Hi " + decodedJWT.getClaim("username").asString());
    };

}


