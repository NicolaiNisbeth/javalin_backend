import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import database.collections.User;
import io.javalin.Javalin;
import io.javalin.core.security.Role;
import io.javalin.http.Handler;
import javalinjwt.JWTAccessManager;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;
import javalinjwt.JavalinJWT;
import javalinjwt.examples.JWTResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class deleteThis {

    public static Javalin app;
    static Algorithm algorithm = Algorithm.HMAC256("very_secret");
    static JWTProvider provider;
    static JWTVerifier verifier;

    public static void main (String [] args) {

    }




    public static void start() throws Exception {
        if (app != null) return;
        Map<String, Role> rolesMapping = new HashMap<>() ;
        rolesMapping.put("user", User.roles.ANYONE);
        JWTAccessManager accessManager = new JWTAccessManager("role", rolesMapping, User.roles.ANYONE);
        app = Javalin.create(config -> {
            config.enableCorsForAllOrigins()
                    .addSinglePageRoot("", "/webapp/index.html");

            config.accessManager(accessManager);

        }).start(8088);


        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
        });
        app.config.addStaticFiles("webapp");

        JWTGenerator<MockUser> generator = (user, alg) -> {
            JWTCreator.Builder token = JWT.create()
                    .withClaim("name", user.name)
                    .withClaim("level", user.role);
            return token.sign(alg);
        };

        verifier = JWT.require(algorithm).build();
        provider = new JWTProvider(algorithm, generator, verifier);
        // REST endpoints
        app.routes(() -> {
            app.before(JavalinJWT.createHeaderDecodeHandler(provider));

        });
    }



    public static Handler generateHandler = context -> {
        MockUser mockUser = new MockUser("Mocky McMockface", "user");
        String token = provider.generateToken(mockUser);
        context.json(new JWTResponse(token));
    };

    Handler validateHandler = context -> {
        DecodedJWT decodedJWT = JavalinJWT.getDecodedFromContext(context);
        context.result("Hi " + decodedJWT.getClaim("name").asString());
    };


    static class MockUser {
        String name;
        String role;

        MockUser(String name, String role) {
            this.name = name;
            this.role = role;
        }
    }
}
