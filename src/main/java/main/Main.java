package main;

import io.javalin.Javalin;
import io.prometheus.client.exporter.HTTPServer;
import javalin_resources.Util.Path;
import javalin_resources.collections.*;
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
  private static String hostAddress;

  public static void main(String[] args) throws Exception {
    String hostName = InetAddress.getLocalHost().getHostName();
    hostAddress = hostName.equals("aws-ec2-javalin-hoster") ? "18.185.121.182" : "localhost";
    System.out.println("Starting server from " + hostAddress);
    System.out.println(String.format("Listening on %s", InetAddress.getLocalHost().getHostAddress()));

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
    app = Javalin.create(config -> config.enableCorsForAllOrigins()
      .addSinglePageRoot("", "/webapp/index.html")
      .server(() -> {
        Server server = new Server(queuedThreadPool);
        server.setHandler(statisticsHandler);
        return server;
      })).start(8080);

    app.before(ctx -> System.out.println(
      String.format("Javalin Server fik %s på %s med query %s og form %s",
        ctx.method(), ctx.url(), ctx.queryParamMap(), ctx.formParamMap()))
    );

    app.exception(Exception.class, (e, ctx) -> e.printStackTrace());
    app.config.addStaticFiles("webapp");

    // REST endpoints
    app.routes(() -> {
      /** GET **/
      get(Path.Employee.EMPLOYEE_ALL, User.getAllUsers);
      get(Path.Employee.EMPLOYEE_ONE_PROFILE_PICTURE, User.getUserPicture);
      get(Path.Playground.PLAYGROUND_ONE, Playground.readOnePlayground);
      get(Path.Playground.PLAYGROUND_ALL, Playground.readAllPlaygrounds);
      get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Playground.readOnePlaygroundOneEmployee);
      get(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ALL, Playground.readOnePlaygroundAllEmployee);
      get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Event.readOneEvent);
      get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Event.readOneEventOneParticipant);
      get(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Event.readOneEventParticipants);
      get(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Event.readOnePlayGroundAllEvents);
      get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Message.readOneMessage);
      get(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, Message.readAllMessages);

      /** POST **/
      post(Path.Employee.LOGIN, User.userLogin);
      post(Path.Employee.CREATE, User.createUser);
      post(Path.Playground.PLAYGROUND_ALL, Playground.createPlayground);
      post(Path.Playground.PLAYGROUNDS_ONE_EVENTS_ALL, Event.createPlaygroundEvent);
      post(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Event.createUserToPlaygroundEvent);
      post(Path.Playground.PLAYGROUND_ONE_MESSAGE_ALL, Message.createPlaygroundMessage);
      //TODO: Implement this
      //skal foregå under create/update user post(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ALL, Post.PostUser.createUserToPlaygroundPost);
      post("/rest/employee/imagetest", context -> Shared.saveProfilePicture2(context));

      /** PUT **/
      put(Path.Employee.UPDATE, User.updateUser);
      put(Path.Employee.RESET_PASSWORD, User.resetPassword);
      put(Path.Playground.PLAYGROUND_ONE, Playground.updatePlayground);
      put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Event.updateEventToPlayground);
      put(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Message.updatePlaygroundMessage);
      //TODO: Test this
      put(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Pedagogue.updatePedagogueToPlayGround);
      // put(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Put.PutUser.updateUserToPlaygroundEventPut);

      /** DELETE **/
      delete(Path.Employee.DELETE, User.deleteUser);
      delete(Path.Playground.PLAYGROUND_ONE, Playground.deleteOnePlayground);
      delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE, Event.deleteEventFromPlayground);
      delete(Path.Playground.PLAYGROUND_ONE_MESSAGE_ONE, Message.deletePlaygroundMessage);
      delete(Path.Playground.PLAYGROUND_ONE_PEDAGOGUE_ONE, Pedagogue.deletePedagogueFromPlayground);
      //TODO: Test this
      //delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Delete.DeleteUser.deleteParticipantFromPlaygroundEventDelete);
      delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANT_ONE, Event.removeUserFromPlaygroundEvent);
      //delete(Path.Playground.PLAYGROUNDS_ONE_EVENT_ONE_PARTICIPANTS_ALL, Delete.User.deleteParticipantFromPlaygroundEvent);
    });
  }

  public static String getHostAddress() {
    return hostAddress;
  }
}
