package resources;

import com.google.gson.Gson;
import database.collections.User;
import database.dao.*;
import io.javalin.http.Context;
import io.javalin.plugin.json.JavalinJson;
import javalin_resources.HttpMethods.Post;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import main.Main;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
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


    @Test
    public void GET_to_fetch_users_returns_list_of_users() throws Exception {
        Main.start();
        HttpResponse<String> response = Unirest.get("http://localhost:8080/rest/employee/all").asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(usersJson);
        Main.stop();
    }




   /* @Test
    public void POST_to_create_users_gives_201_for_valid_username() throws Exception {
        when(ctx.queryParam("username")).thenReturn("Roland");
        Post.PostUser.createUser.handle(ctx);

        // UserController.create(ctx); // the handler we're testing
        verify(ctx).status(201);
    }*/

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
        userModel.phoneNumber = "";
        userModel.website = "";
        userModel.playgroundsIDs = new String[2];
        Gson gson = new Gson();
        String json = gson.toJson(userModel);

        when(ctx.formParam("usermodel")).thenReturn(json);
/*        when(ctx.json(Controller.getInstance().getUsers()).status(201)
                .result("User created.")).thenCallRealMethod();*/

        //when(ctx.json(Controller.getInstance().getUsers())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        when(ctx.json(Controller.getInstance().getUsers())).thenCallRealMethod();

        verify(ctx).status(201);

        //when(ctx.queryParam("username")).thenReturn("Roland");
        // UserController.create(ctx); // the handler we're testing
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

        when(ctx.body()).thenReturn(json);

        //when(ctx.json(User.class).contentType("json")).thenCallRealMethod();
        //doCallRealMethod().when(ctx).json(User.class).contentType("json");
       // doCallRealMethod().when(ctx).json(loginUser).contentType("json");

       // when(ctx.json(loginmodel)).thenReturn(ctx.json(loginmodel));
        when(ctx.json(loginmodel)).thenAnswer(new ReturnFirstArg<Integer>());
        //when(object.method(7)).thenAnswer(new ReturnFirstArg<Integer>());

        Post.PostUser.userLogin.handle(ctx);
    }
    public class ReturnFirstArg<T> implements Answer<T> {
        public T answer(InvocationOnMock invocation) {
            return (T) invocation.getArguments()[0];
        }
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

