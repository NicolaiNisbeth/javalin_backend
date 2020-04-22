package resources;

import com.google.gson.Gson;
import database.dao.*;
import io.javalin.http.Context;
import javalin_resources.HttpMethods.Post;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class PostTest {
    private static Context ctx;
    static Controller beta;
    static IEventDAO eventDAO;
    static IMessageDAO messageDAO;
    static IPlaygroundDAO playgroundDAO;
    static IUserDAO userDAO;

    @BeforeAll
    static void setUp() {
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        //controller = new Controller();
        beta = Controller.getInstance();

    }


    @Test
    public void POST_to_create_users_gives_201_for_valid_username() throws Exception {
        when(ctx.queryParam("username")).thenReturn("Roland");
        Post.PostUser.createUser.handle(ctx);

        // UserController.create(ctx); // the handler we're testing
        verify(ctx).status(201);
    }
/*

    @Test(expected = BadRequestResponse.class)
    public void POST_to_create_users_throws_for_invalid_username() {
        when(ctx.queryParam("username")).thenReturn(null);
        UserController.create(ctx); // the handler we're testing
    }
*/


    /**
     * POST USER
     */
    @Test
    void createUser() throws Exception {

        UserModel userModel = new UserModel();
        userModel.usernameAdmin = "root";
        userModel.passwordAdmin = "root";
        userModel.username = "abc";
        userModel.password = "abc";
        userModel.firstname = "Hans";
        userModel.lastname = "Bertil";
        userModel.email = "";
        userModel.status = "pædagog";
        userModel.imagePath = "";
        userModel.phoneNumber = "";
        userModel.website = "";
        userModel.playgroundsIDs = new String[2];
        Gson gson = new Gson();
        String json = gson.toJson(userModel);

        when(ctx.formParam("UserModel")).thenReturn(json);

        Post.PostUser.createUser.handle(ctx);

        //when(ctx.queryParam("username")).thenReturn("Roland");
        // UserController.create(ctx); // the handler we're testing
        // verify(ctx).status(201);
    }


    @Test
    void userLogin() throws Exception {

        LoginModel loginmodel = new LoginModel();
        loginmodel.username = "abc";
        loginmodel.password = "abc";

        Gson gson = new Gson();
        String json = gson.toJson(loginmodel);

        when(ctx.body()).thenReturn(json);
        Post.PostUser.userLogin.handle(ctx);
    }

}

