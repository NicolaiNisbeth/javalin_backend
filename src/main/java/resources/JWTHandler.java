package resources;

import brugerautorisation.data.Bruger;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.dao.UserDAO;
import database.dto.UserDTO;
import javalinjwt.JWTGenerator;
import javalinjwt.JWTProvider;

public class JWTHandler {

    // Den her er static så den kan bruges i flere klasser.
    public static JWTProvider provider;
    /*
    In order to use any functionality available here, you need first to create a JWT provider (for lack of a better word).
    A provider is a somewhat convient way of working with JWT which wraps a generator and a verifier.
    You need a generator which implements the functional interfaceJWTGeneratr, and a verifier which is the normal Auth0 JWTVerifier.
    Additionally, both of them require an Auth0 Algorithm to work on. For the sake of example, we are going to use HMAC256.
     */

    public static JWTProvider createHMAC512() {
        JWTGenerator<UserDTO> generator = (user, alg) -> {
            JWTCreator.Builder token = JWT.create()
                    .withClaim("name", user.getUsername())
                    .withClaim("status", user.getStatus());
            return token.sign(alg);
        };

        Algorithm algorithm = Algorithm.HMAC512("very_secret");
        JWTVerifier verifier = JWT.require(algorithm).build();

        return new JWTProvider(algorithm, generator, verifier);
    }
}
