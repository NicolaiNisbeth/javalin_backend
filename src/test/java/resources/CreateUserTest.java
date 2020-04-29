package resources;

import com.google.gson.Gson;
import com.mongodb.WriteResult;
import database.DALException;
import database.collections.Playground;
import database.collections.User;
import database.dao.*;
import io.javalin.http.Context;
import javalin_resources.HttpMethods.Post;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.awt.*;

import static org.mockito.Mockito.*;

class CreateUserTest {
    private static Context ctx;
    private JsonModels.UserModel userModel = new JsonModels.UserModel();
    private static Gson gson;
    private static String json;
    private static Playground playground;

    @BeforeAll
    static void setUp() {
        //"mock-maker-inline" must be enabled
        ctx = mock(Context.class);
    }

    @BeforeEach
    void setUpUser() {
        userModel = new JsonModels.UserModel();
        userModel.usernameAdmin = "root";
        userModel.passwordAdmin = "root";
        userModel.username = "abc";
        userModel.password = "abc";
        userModel.firstname = "Hans";
        userModel.lastname = "Bertil";
        userModel.email = "kål";
        userModel.status = "pædagog";
        userModel.imagePath = "";
        userModel.phoneNumbers = new String[2];
        userModel.phoneNumbers[0] = "12345678";
        userModel.phoneNumbers[1] = "887654321";
        userModel.website = "";
        userModel.playgroundsIDs = new String[1];
        gson = new Gson();
        json = gson.toJson(userModel);

        try {
            Controller.getInstance().getUser("root");
        } catch (DALException e) {
            User root = new User.Builder("root")
                    .status("admin")
                    .setPassword("root")
                    .setFirstname("Københavns")
                    .setLastname("Kommune")
                    .build();
            Controller.getInstance().createUser(root);
        }

        playground = new Playground.Builder("KålPladsen")
                .setCommune("København Ø")
                .setZipCode(2100)
                .setStreetName("Vognmandsmarken")
                .setStreetNumber(69)
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .build();
        Controller.getInstance().createPlayground(playground);
    }

    @AfterEach
    void cleanUp() {
        try {
            Controller.getInstance().deletePlayground("KålPladsen");
        } catch (Exception e) {
        }
        try {
            Controller.getInstance().deleteUser("abc");
        } catch (Exception e) {

        }

    }

    @Test
    void createUser() throws Exception {
        // Normal oprettelse af bruger
        userModel.playgroundsIDs[0] = playground.getName();
        Context ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);

        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(201);
        verify(ctx).json("Created - User created");

        User user = Controller.getInstance().getUser("abc");
        Assertions.assertEquals(1, user.getPlaygroundsIDs().size());
        System.out.println("ids " + user.getPlaygroundsIDs());
        Playground playground1 = Controller.getInstance().getPlayground("KålPladsen");
        Assertions.assertEquals(1, playground1.getAssignedPedagogue().size());
    }

    @Test
    void deleteUser() throws Exception {
        Controller.getInstance().createPlayground(playground);
        userModel.playgroundsIDs[0] = playground.getName();

        Context ctx = mock(Context.class);
        ctx.result("");
        ctx.status(0);

        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(201);
        verify(ctx).json("Created - User created");

        User user = Controller.getInstance().getUser("abc");
        Assertions.assertEquals(1, user.getPlaygroundsIDs().size());

        playground = Controller.getInstance().getPlayground("KålPladsen");
        Assertions.assertEquals(1, playground.getAssignedPedagogue().size());
        Controller.getInstance().deleteUser(user.getUsername());
        playground = Controller.getInstance().getPlayground("KålPladsen");
        Assertions.assertEquals(0, playground.getAssignedPedagogue().size());
    }
    /**
     * User edge cases
     */
    @Test
    void dublicateUserInDB() throws Exception {
        User abcUser = new User.Builder("abc")
                .setPassword("abc")
                .status("pædagog")
                .build();
        Controller.getInstance().createUser(abcUser);

        // Forsøg på oprettelse af user der allerede er i db
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);

        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(401);
        verify(ctx).result("Unauthorized - User already exists");
    }

    @Test
    void userWithNoUsername() throws Exception {
        // Forsøg på oprettelse af user uden username
        ctx = mock(Context.class);
        ctx.result("");
        ctx.status(0);

        userModel.username = "";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(400);
        verify(ctx).result("Bad Request - Error in user data");
    }

    @Test
    void userWithNoPassword() throws Exception {
        // Forsøg på oprettelse af user uden setPassword
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);

        userModel.password = "";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(400);
        verify(ctx).result("Bad Request - Error in user data");
    }

    /**
     * Admin edge cases
     */
    @Test
    void wrongAdminStatus() throws Exception {
        // Forkert admin status
        User abcUser = new User.Builder("abc-wrong-adm-stat")
                .setPassword("abc")
                .status("pædagog")
                .build();
        Controller.getInstance().createUser(abcUser);
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);

        userModel.usernameAdmin = "abc-wrong-adm-stat";
        userModel.passwordAdmin = "abc-wrong-adm-stat";
        userModel.username = "abc";
        userModel.password = "abc";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(401);
        verify(ctx).result("Unauthorized - Wrong admin status");

        Controller.getInstance().deleteUser("abc-wrong-adm-stat");
    }

    @Test
    void wrongAdminUsername() throws Exception {
        // Forkert admin brugernavn
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);
        userModel.usernameAdmin = "rot";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(401);
        verify(ctx).result("Unauthorized - Wrong admin username");
    }

    @Test
    void wrongAdminPassword() throws Exception {
        //Forkert admin setPassword
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);
        userModel.passwordAdmin = "rot";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(401);
        verify(ctx).result("Unauthorized - Wrong admin setPassword");
    }
}

/*
end point test
@Test
    public void GET_to_fetch_users_returns_list_of_users() throws Exception {
        Main.start();
        HttpResponse<String> response = Unirest.get("http://localhost:8080/rest/employee/all").asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(usersJson);
        Main.stop();
    }
*/
