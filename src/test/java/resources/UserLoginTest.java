package resources;
import com.google.gson.Gson;
import database.Controller;
import database.IController;
import database.TestDB;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.ContentType;
import javalin_resources.HttpMethods.Post;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;


public class UserLoginTest {

    private static Context ctx;
    private JsonModels.LoginModel model;
    private Gson gson;
    private static IController controller = Controller.getInstance();

    @BeforeAll
    static void beforeAll(){
        controller.setDataSource(TestDB.getInstance());
    }

    @BeforeEach
    void setup(){
        ctx = mock(Context.class);
        model = new JsonModels.LoginModel();
        gson = new Gson();
    }

    @AfterEach
    void teardown(){
        ctx = null;
        model = null;
        gson = null;
    }

    @Test
    void rootShouldReturn200() throws Exception {
        model.username = "root";
        model.password = "root";
        String inputBody = gson.toJson(model);

        when(ctx.body()).thenReturn(inputBody);
        Post.User.userLogin.handle(ctx);

        verify(ctx).status(HttpStatus.OK_200);
        verify(ctx).contentType(ContentType.JSON);
    }

    @Test
    void invalidInfoShouldReturn404() throws Exception {
        model.username = "testUsername";
        model.password = "testPassword";
        String inputBody = gson.toJson(model);

        when(ctx.body()).thenReturn(inputBody);
        Post.User.userLogin.handle(ctx);

        verify(ctx).status(HttpStatus.NOT_FOUND_404);
        verify(ctx).contentType(ContentType.JSON);
    }

    @Test
    void nullShouldReturn400() throws Exception {
        model.username = null;
        model.password = null;
        String inputBody = gson.toJson(model);

        when(ctx.body()).thenReturn(inputBody);
        Post.User.userLogin.handle(ctx);

        verify(ctx).status(HttpStatus.BAD_REQUEST_400);
        verify(ctx).contentType(ContentType.JSON);
    }

    @Test
    void noBodyShouldReturn400() throws Exception {
        Post.User.userLogin.handle(ctx);

        verify(ctx).status(HttpStatus.BAD_REQUEST_400);
        verify(ctx).contentType(ContentType.JSON);
    }

    @Test
    void validInfoShouldReturn200() throws Exception {
        model.username = "s175565";
        model.password = "kodeNWHN";
        String inputBody = gson.toJson(model);

        when(ctx.body()).thenReturn(inputBody);
        Post.User.userLogin.handle(ctx);

        verify(ctx).status(HttpStatus.OK_200);
        verify(ctx).contentType(ContentType.JSON);
    }

    @Test
    void wrongPasswordShouldReturn401() throws Exception {
        model.username = "s175565";
        model.password = "wrongpassword";
        String inputBody = gson.toJson(model);

        when(ctx.body()).thenReturn(inputBody);
        Post.User.userLogin.handle(ctx);

        verify(ctx).status(HttpStatus.UNAUTHORIZED_401);
        verify(ctx).contentType(ContentType.JSON);
    }
}
