package resources;

import com.google.gson.Gson;
import database.DALException;
import database.collections.User;
import database.dao.*;
import io.javalin.http.Context;
import io.javalin.plugin.json.JavalinJson;
import javalin_resources.HttpMethods.Post;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import main.Main;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.VerificationCollector;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.extractProperty;
import static org.mockito.Mockito.*;

class PostTest {
    private static Context ctx;
    static Controller beta;
    private Main app = new Main(); // inject any dependencies you might have
    private String usersJson = JavalinJson.toJson(Controller.getInstance().getUsers());

    @BeforeAll
    static void setUp() throws Exception {
        //main.Main.start();
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        beta = Controller.getInstance();
    }

    @AfterAll
    static void printAll() throws DALException {
        UserDAO userDAO = new UserDAO();
        System.out.println("Test-userlist after test: ");
        for (User user : userDAO.getUserList()) {
            if (user.getUsername().length() < 1 || user.getUsername().substring(0,3).equalsIgnoreCase("abc") ) {
                System.out.println(user);
                System.out.println("Deleting test user: " + user.getUsername());
                userDAO.deleteUser(user.getUsername());
            }
        }
    }

    /**
     * POST USER TESTS
     */
    @Test
    void createUser() throws Exception {
        JsonModels.UserModel userModel = new JsonModels.UserModel();
        userModel.usernameAdmin = "root";
        userModel.passwordAdmin = "root";
        userModel.username = "abc";
        userModel.password = "abc";
        userModel.firstname = "Hans";
        userModel.lastname = "Bertil";
        userModel.email = "kål";
        userModel.status = "pædagog";
        userModel.imagePath = "";
        userModel.phonenumbers = new String[2];
        userModel.website = "";
        userModel.playgroundsIDs = new String[2];
        Gson gson = new Gson();
        String json = gson.toJson(userModel);

        Context ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);

        // Normal oprettelse af bruger
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(201);
        verify(ctx).result("User created.");

        /**
         * User edge cases
         */
        // Forsøg på oprettelse af user der allerede er i db
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);

        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(401);
        verify(ctx).result("Unauthorized - User already exists");

        // Forsøg på oprettelse af user uden username
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);

        userModel.username = "";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(400);
        verify(ctx).result("Bad Request - Error in user data");

        /**
         * Admin edge cases
         */
        // Forkert admin status
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);
        userModel.usernameAdmin = "abc";
        userModel.passwordAdmin = "abc";
        userModel.username = "abctest";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(401);
        verify(ctx).result("Unauthorized - Wrong admin status");
        // Forkert admin brugernavn
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);

        userModel.usernameAdmin = "rot";
        userModel.username += "test";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(401);
        verify(ctx).result("Unauthorized - Wrong admin username");

        // Fejlagtig oprettelse af bruger med forkert admin password
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);

        userModel.usernameAdmin = "root";
        userModel.passwordAdmin = "rot";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(401);
        verify(ctx).result("Unauthorized - Wrong admin password");


    }

    @Test
    void userLoginRoot() throws Exception {

        JsonModels.LoginModel loginmodel = new JsonModels.LoginModel();
        loginmodel.username = "root";
        loginmodel.password = "root";

        Gson gson = new Gson();
        String json = gson.toJson(loginmodel);

        User loginUser = new User.Builder(loginmodel.username)
                .status("admin")
                .password(loginmodel.password)
                .setFirstname("Københavns")
                .setLastname("Kommune")
                .build();

        //when(ctx.body()).thenReturn(json);

        //when(ctx.json(User.class).contentType("json")).thenCallRealMethod();
        //doCallRealMethod().when(ctx).json(User.class).contentType("json");
        // doCallRealMethod().when(ctx).json(loginUser).contentType("json");

        // when(ctx.json(loginmodel)).thenReturn(ctx.json(loginmodel));
        //when(ctx.json(loginmodel)).thenAnswer(new ReturnFirstArg<Integer>());
        //when(object.method(7)).thenAnswer(new ReturnFirstArg<Integer>());

        Post.PostUser.userLogin.handle(ctx);
    }

    @Test
    void userLogin() throws Exception {

        JsonModels.LoginModel loginmodel = new JsonModels.LoginModel();
        loginmodel.username = "abc";
        loginmodel.password = "abc";

        Gson gson = new Gson();
        String json = gson.toJson(loginmodel);

        when(ctx.body()).thenReturn(json);
        Post.PostUser.userLogin.handle(ctx);
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
