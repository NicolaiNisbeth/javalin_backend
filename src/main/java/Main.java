import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper;
import database.collections.User;
import database.dao.Controller;
import io.javalin.Javalin;
import io.javalin.core.security.Role;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import javalin_resources.HttpMethods.Delete;
import javalin_resources.HttpMethods.Get;
import javalin_resources.HttpMethods.Post;
import javalin_resources.HttpMethods.Put;
import javalin_resources.Util.JWebToken;
import javalin_resources.Util.Path;
import javalinjwt.JWTAccessManager;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;
import javalinjwt.JavalinJWT;
import javalinjwt.examples.JWTResponse;
import org.json.JSONObject;


import static io.javalin.apibuilder.ApiBuilder.*;

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
        rolesmapping.put("user", User.roles.ANYONE);
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



    public static void start() throws Exception {
        if (app != null) return;

        JWTGenerator<deleteThis.MockUser> generator = (user, alg) -> {
            JWTCreator.Builder token = JWT.create().withClaim("name", user.name).withClaim("level", user.role);
            return token.sign(alg);
        };


        verifier = JWT.require(algorithm).build();
        provider = new JWTProvider(algorithm, generator, verifier);
        JWTAccessManager accessManager = new JWTAccessManager("role", rolesmapping, User.roles.ANYONE);

        app = Javalin.create(config -> {
            config.enableCorsForAllOrigins().addSinglePageRoot("", "/webapp/index.html");
            config.accessManager(accessManager);
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

            get("/validate", validateJWTHandler, anyone);
            get("/generate", generateJWTHandler, anyone);
            /**
             * GET
             **/
            //GET PLAYGROUNDS
            get(Path.Playground.PLAYGROUND_ALL, Get.GetPlayground.readAllPlaygroundsGet, anyone);
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



    public static Handler generateJWTHandler = ctx -> {
        deleteThis.MockUser mockUser = new deleteThis.MockUser("Mocky McMockface", "user");
    //    JSONObject jsonObject = new JSONObject(ctx.body());
    //    User user = null;
    //    if (jsonObject.has(Get.USER_NAME)) {
    //        user = Controller.getInstance().getUser(jsonObject.getString(Get.USER_NAME));
    //    }
        String token = provider.generateToken(mockUser);
        ctx.json(new JWTResponse(token));
    //    if (user != null) {

      //  } else {
       //     ctx.status(400).result("not found");
      //  }
    };

    public static Handler validateJWTHandler = context -> {
        DecodedJWT decodedJWT = JavalinJWT.getDecodedFromContext(context);
        context.result("Hi " + decodedJWT.getClaim("name").asString());
    };
}
