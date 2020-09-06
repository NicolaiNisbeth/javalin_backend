import io.javalin.Javalin;
import io.javalin.core.security.Role;
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

import static io.javalin.apibuilder.ApiBuilder.*;

public class Main {
  private static final int port = 8080;
  private static Javalin app;

  public static void main(String[] args) throws Exception {
    String hostName = InetAddress.getLocalHost().getHostName();
    String hostAddress = hostName.equals("aws-ec2-javalin-hoster") ? "18.185.121.182" : "localhost";
    System.out.println("Starting server from " + hostAddress);
    System.out.printf("Listening on %s%n", InetAddress.getLocalHost().getHostAddress());
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

    before(ctx -> System.out.printf("Javalin Server fik %s på %s med query %s og form %s%n",
            ctx.method(),
            ctx.url(),
            ctx.queryParamMap(),
            ctx.formParamMap())
    );

    // endpoints
    app.routes(() -> {

      // users
      path("/rest/users", () -> {
        post(User.createUser);
        put(User.updateUser);
        delete(User.deleteUser);
        post(":login", User.login);
        get(":employees", User.getEmployees);
      });

      // playgrounds
      path("/rest/playgrounds", () -> {
        get(Playground.getPlaygrounds);
        post(Playground.createPlayground);

        // playground
        path(":name", () -> {
          get(Playground.getPlayground);
          put(Playground.updatePlayground);
          delete(Playground.deletePlayground);

          path(":pedagogues", () -> {
            get(Playground.getPedagogues);
            get(":username", Playground.getPedagogue);
          });

          // events
          path(":events", () -> {
            get(Event.getEvents);

            path(":id", () -> {
              post(Event.createEvent);
              get(Event.getEvent);
              delete(Event.deleteEvent);

              path(":participants", () -> {
                get(Event.getParticipants);

                path(":username", () -> {
                  get(Event.getUserInEvent);
                  post(Event.registerUser);
                  delete(Event.unregister);
                });
              });
            });
          });

          // messages
          path("messages", () -> {
            get(Message.getMessages);

            path(":id", () -> {
              post(Message.createMessage);
              get(Message.getMessage);
              put(Message.updateMessage);
              delete(Message.deleteMessage);
            });
          });
        });
      });
    });
  }

  public static void stop() {
    app.stop();
    app = null;
  }

  private static void initializePrometheus(StatisticsHandler statisticsHandler, QueuedThreadPool queuedThreadPool) throws IOException {
    StatisticsHandlerCollector.initialize(statisticsHandler);
    QueuedThreadPoolCollector.initialize(queuedThreadPool);
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
      System.out.printf("Server: Using resource directories from path: %s\\server_resource\\%n", homeFolder.toString());
    } else {
      boolean userDirCreated = serverResProfileImages.mkdirs();
      boolean playgroundDirCreated = serverResPlaygrounds.mkdir();
      boolean messageDirCreated = serverResMessageImages.mkdirs();

      if (userDirCreated || messageDirCreated || playgroundDirCreated) {
        System.out.printf("Server: Resource directories is build at path: %s\\server_resource%n", homeFolder.toString());
      }
    }
  }

  enum Roles implements Role {
    ANYONE,
    PEDAGOGUE,
    ADMIN
  }
}
