package rest;

import io.javalin.Javalin;
import io.javalin.core.security.Role;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.prometheus.client.exporter.HTTPServer;
import io.swagger.v3.oas.models.info.Info;
import javalinjwt.JWTAccessManager;
import javalinjwt.JavalinJWT;
import monitoring.QueuedThreadPoolCollector;
import monitoring.StatisticsHandlerCollector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import resources.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Main {
  private static final int port = 8080;
  public static Javalin app;

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
    // Den her enkrypterer vores webtokens.
    JWTHandler.provider = JWTHandler.createHMAC512();

    // Acces manager

    Map<String, Role> rolesMapping = new HashMap<String, Role>() {{
      put("pædagog", Roles.PEDAGOGUE);
      put("admin", Roles.ADMIN);
    }};

    // Acces manageren tager imod 1. hvilken attribut hos bruger objektet bestemmer hans "status" (pædagog, admin, lig)
    // Andet argument er hvad for nogle roller bruger vi?
    // Det tredje er hvad er "default rollen". Altså i tilfældet af at vores bruger ikke er logget ind.
    JWTAccessManager accessManager = new JWTAccessManager("status", rolesMapping, Roles.ANYONE);


    if (app != null) return;
    app = Javalin.create(config -> config.enableCorsForAllOrigins()
      .registerPlugin(getConfiguredOpenApiPlugin())
      .addSinglePageRoot("", "/webapp/index.html")
      .addStaticFiles("webapp")
      .accessManager(accessManager)
      .server(() -> {
        Server server = new Server(queuedThreadPool);
        server.setHandler(statisticsHandler);
        return server;
      })).start(port);

    System.out.println("Check out Swagger UI docs at http://localhost:8080/rest");
    System.out.println("Check out OpenAPI docs at http://localhost:8080/rest-docs");

    // REST endpoints
    app.routes(() -> {

      before(ctx -> System.out.println(
        String.format("Javalin Server fik %s på %s med query %s og form %s",
          ctx.method(), ctx.url(), ctx.queryParamMap(), ctx.formParamMap()))
      );

      before(JavalinJWT.createHeaderDecodeHandler(JWTHandler.provider));

      /** USERS **/
      get(Path.User.USERS_ALL, User.getAllUsers, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      get(Path.User.USERS_ALL_EMPLOYEES, User.getAllEmployees, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      get(Path.User.USERS_ONE_PROFILE_PICTURE, User.getUserPicture, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));

      put(Path.User.USERS_CRUD, User.updateUser, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.ADMIN, Roles.PEDAGOGUE)));
      put(Path.User.USERS_RESET_PASSWORD, User.resetPassword, new HashSet<>(Arrays.asList(Roles.ADMIN)));

      post(Path.User.USERS_LOGIN, User.userLogin, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      post(Path.User.USERS_CRUD, User.createUser, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.ADMIN))); // used for signup

      delete(Path.User.USERS_CRUD, User.deleteUser, new HashSet<>(Arrays.asList(Roles.ADMIN)));


      /** PLAYGROUNDS **/
      get(Path.Playground.PLAYGROUNDS_ONE, Playground.readOnePlayground, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      get(Path.Playground.PLAYGROUNDS_ALL, Playground.readAllPlaygrounds, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      get(Path.Playground.PLAYGROUNDS_ONE_PEDAGOGUE_ONE, Playground.readOnePlaygroundOneEmployee, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      get(Path.Playground.PLAYGROUNDS_ONE_PEDAGOGUE_ALL, Playground.readOnePlaygroundAllEmployee, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Event.readOneEvent, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Event.readOneEventOneParticipant, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Event.readOneEventParticipants, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      get(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Event.readOnePlayGroundAllEvents, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      get(Path.Playground.PLAYGROUNDS_ONE_MESSAGE_ONE, Message.readOneMessage, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      get(Path.Playground.PLAYGROUNDS_ONE_MESSAGE_ALL, Message.readAllMessages, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));

      get(Path.Playground.PLAYGROUNDS_ONE_PROFILE_PICTURE, Playground.getPicture, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));


      put(Path.Playground.PLAYGROUNDS_ONE, Playground.updatePlayground, new HashSet<>(Arrays.asList(Roles.PEDAGOGUE, Roles.ADMIN)));
      put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Event.updateEventToPlayground, new HashSet<>(Arrays.asList(Roles.PEDAGOGUE, Roles.ADMIN)));
      put(Path.Playground.PLAYGROUNDS_ONE_MESSAGE_ONE, Message.updatePlaygroundMessage, new HashSet<>(Arrays.asList(Roles.PEDAGOGUE, Roles.ADMIN)));

      post(Path.Playground.PLAYGROUNDS_ALL, Playground.createPlayground, new HashSet<>(Arrays.asList(Roles.ADMIN)));
      post(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Event.createPlaygroundEvent, new HashSet<>(Arrays.asList(Roles.PEDAGOGUE, Roles.ADMIN)));
      post(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Event.createUserToPlaygroundEvent, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      post(Path.Playground.PLAYGROUNDS_ONE_MESSAGE_ALL, Message.createPlaygroundMessage, new HashSet<>(Arrays.asList(Roles.PEDAGOGUE, Roles.ADMIN)));

      delete(Path.Playground.PLAYGROUNDS_ONE, Playground.deleteOnePlayground, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));
      delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Event.deleteEventFromPlayground, new HashSet<>(Arrays.asList(Roles.PEDAGOGUE, Roles.ADMIN)));
      delete(Path.Playground.PLAYGROUNDS_ONE_MESSAGE_ONE, Message.deletePlaygroundMessage, new HashSet<>(Arrays.asList(Roles.PEDAGOGUE, Roles.ADMIN)));
      delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Event.deleteUserFromPlaygroundEvent, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));

      /** MESSAGES **/

      get(Path.Message.MESSAGE_IMAGE_ONE, Message.getMessageImage, new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)));

      /** EVENTS **/
    });
  }

  public static void stop() {
    app.stop();
    app = null;
  }

  private static void initializePrometheus(StatisticsHandler statisticsHandler, QueuedThreadPool queuedThreadPool) throws IOException {
    StatisticsHandlerCollector.initialize(statisticsHandler);
    QueuedThreadPoolCollector.initialize(queuedThreadPool);
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
      .path("/rest-docs") // endpoint for OpenAPI json
      .swagger(new SwaggerOptions("/rest")) // endpoint for swagger-ui
      .roles(new HashSet<>(Arrays.asList(Roles.ANYONE, Roles.PEDAGOGUE, Roles.ADMIN)))
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

  // This enum get's used by the acces manager in the start method
  enum Roles implements Role {
    ANYONE,
    PEDAGOGUE,
    ADMIN
  }
}
