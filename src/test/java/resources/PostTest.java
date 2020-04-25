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
            if (user.getUsername().substring(0,3).equalsIgnoreCase("abc")) {
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
        Context ctx = mock(Context.class); // "mock-maker-inline" must be enabled

        JsonModels.UserModel userModel = new JsonModels.UserModel();
        userModel.usernameAdmin = "root";
        userModel.passwordAdmin = "root";
        userModel.username = "abc";
        userModel.password = "abc";
        userModel.firstname = "Hans";
        userModel.lastname = "Bertil";
        userModel.email = "";
        userModel.status = "pædagog";
        userModel.imagePath = "";
        userModel.phonenumbers = new String[2];
        userModel.website = "";
        userModel.playgroundsIDs = new String[2];
        Gson gson = new Gson();
        String json = gson.toJson(userModel);

        // Normal oprettelse af bruger
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();

        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(201);
        verify(ctx).result("User created.");

        // Fejlagtig oprettelse af den samme bruger
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(411);
        verify(ctx).result("Unauthorized - User already exists");

        // Fejlagtig oprettelse af anden bruger med forkert admin password
     /*   ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        userModel.usernameAdmin = "rot";
        userModel.username += "test";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(411);
        verify(ctx).result("Unauthorized - Wrong admin username");*/
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
