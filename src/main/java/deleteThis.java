import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import database.collections.User;
import io.javalin.Javalin;
import io.javalin.core.security.Role;
import io.javalin.http.Handler;
import io.prometheus.client.exporter.HTTPServer;
import javalin_resources.HttpMethods.*;
import javalin_resources.Util.Path;
import javalinjwt.JWTAccessManager;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;
import javalinjwt.JavalinJWT;
import javalinjwt.examples.JWTResponse;
import monitor.QueuedThreadPoolCollector;
import monitor.StatisticsHandlerCollector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.core.security.SecurityUtil.roles;

public class deleteThis {

    public static Javalin app;
    public static HashMap<String, Role> rolesMapping;

    static Algorithm algorithm = Algorithm.HMAC256("very_secret"); //ja tak venner let's go.

    static JWTGenerator<MockUser> generator = (user, alg) -> {
        JWTCreator.Builder token = JWT.create()
                .withClaim("name", user.name)
                .withClaim("level", user.level);
        return token.sign(alg);
    };


    static JWTVerifier verifier = JWT.require(algorithm).build();
    static JWTProvider provider = new JWTProvider(algorithm, generator, verifier);


    public static void main(String[] args) throws Exception {
        rolesMapping = new HashMap<>();
        rolesMapping.put("user", Roles.user);
        rolesMapping.put("admin", Roles.admin);
        rolesMapping.put("anyone", Roles.anyone);
        start();
    }

    public static void start() throws Exception {


        if (app != null) return;


        app = Javalin.create(config -> {
            config.enableCorsForAllOrigins()
                    .addSinglePageRoot("", "/webapp/index.html");
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


            get("/test", testhandler);
            get("/test1", testhandler);
            get("/test2", testhandler);

            app.get("/generate",  generateJWTHandler);
            app.get("/validate", validateJWTHandler);
            app.get("/adminslounge", validateJWTHandler);

    });
    }
    public static Handler generateJWTHandler = ctx -> {
            MockUser mockUser = new MockUser("Mocky McMockface", "admin");
            String token = provider.generateToken(mockUser);
            ctx.json(new JWTResponse(token));
    };

    public static Handler validateJWTHandler = context -> {
        Optional<DecodedJWT> decodedJWT = JavalinJWT.getTokenFromHeader(context)
                .flatMap(provider::validateToken);

        if (!decodedJWT.isPresent()) {
            context.status(401).result("Missing or invalid token");
        }
        else {
            context.result("Hi " + decodedJWT.get().getClaim("name").asString());
        }
    };

    public static Handler testhandler = ctx -> {
        ctx.result("hello");
    };
}

class MockUser {
    String name;
    String level;

    MockUser(String name, String level) {
        this.name = name;
        this.level = level;
    }
}
enum Roles implements Role {
    anyone,
    user,
    admin
}


