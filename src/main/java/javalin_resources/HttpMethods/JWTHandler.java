package javalin_resources.HttpMethods;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import database.dto.UserDTO;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;

public class JWTHandler {


    /*
    In order to use any functionality available here, you need first to create a JWT provider (for lack of a better word).
    A provider is a somewhat convient way of working with JWT which wraps a generator and a verifier.
    You need a generator which implements the functional interfaceJWTGeneratr, and a verifier which is the normal Auth0 JWTVerifier.
    Additionally, both of them require an Auth0 Algorithm to work on. For the sake of example, we are going to use HMAC256.
     */

    //1.
   static Algorithm algorithm = Algorithm.HMAC256("very_secret");

    //2.
    /*
    For generating token
     */
    static JWTGenerator<UserDTO> generator = (user, alg) -> {
        JWTCreator.Builder token = JWT.create()
                .withClaim("name", user.getUsername())
                .withClaim("password", user.getPassword());
        return token.sign(alg);
    };

    //3.
    /*
    For verifeing JWT
     */
    public static JWTVerifier verifier = JWT.require(algorithm).build();

    //4.
    /*
    The wrapper object is created
     */
    public static JWTProvider provider = new JWTProvider(algorithm, generator, verifier);

}
