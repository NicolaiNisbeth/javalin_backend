package javalinjwt.examples;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.Javalin;
import javalinjwt.JWTProvider;
import javalinjwt.JavalinJWT;

import java.util.Optional;

import static io.javalin.apibuilder.ApiBuilder.before;

public class BasicExample {
    public static void main(String[] args) {
        // create the provider
        JWTProvider provider = ProviderExample.createHMAC512();

        // create the app
        Javalin app = Javalin.create()
             //   .port(4000)
                .start(4000);

        app.before(ctx -> { System.out.println("Javalin Server fik " + ctx.method() + " på " + ctx.url() + " med query " + ctx.queryParamMap() + " og form " + ctx.formParamMap()); });


        app.get("/generate",  context -> {
            // a mock user as an examples
            MockUser mockUser = new MockUser("Mocky McMockface", "admin");

            // generate a token for the user
            String token = provider.generateToken(mockUser);

            // send the JWT response
            context.json(new JWTResponse(token).jwt);
        });

        app.get("/validate", context -> {
            Optional<DecodedJWT> decodedJWT = JavalinJWT.getTokenFromHeader(context)
                    .flatMap(provider::validateToken);

            if (!decodedJWT.isPresent()) {
                context.status(401).result("Missing or invalid token");
            }
            else {
                context.result("Hi " + decodedJWT.get().getClaim("name").asString());
            }
        });
    }
}
