package resources;/*
package resources;

import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;


public class ClientTest {

    public static void main(String[] args) {
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);
        WebTarget target = client.target(getBaseURI());
        System.out.println(target.path("hello/Nicolai").request().accept(MediaType.TEXT_PLAIN).get(String.class));
        System.out.println(target.path("hello/no").request().accept(MediaType.TEXT_PLAIN).get(String.class));
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8080/rest").build();
    }
}

*/
