package resources;
import com.google.gson.Gson;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.ContentType;
import javalin_resources.HttpMethods.Post;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;


public class UserLoginTest {

    private static Context ctx;
    private final int EMPTY_STATUS = 0;
    private final String EMPTY_RESULT = "";
    private JsonModels.LoginModel model;
    private Gson gson;


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
        ctx.status(EMPTY_STATUS);
        ctx.result(EMPTY_RESULT);

        model.username = "root";
        model.password = "root";
        String inputBody = gson.toJson(model);

        when(ctx.body()).thenReturn(inputBody);
        Post.User.userLogin.handle(ctx);

        verify(ctx).status(200);
        verify(ctx).result("user login with root was successful");
        verify(ctx).contentType(ContentType.JSON);
    }

    @Test
    void invalidInfoShouldReturn404() throws Exception {
        ctx.status(EMPTY_STATUS);
        ctx.result(EMPTY_RESULT);

        model.username = "testUsername";
        model.password = "testPassword";
        String inputBody = gson.toJson(model);

        when(ctx.body()).thenReturn(inputBody);
        Post.User.userLogin.handle(ctx);

        verify(ctx).status(404);
        verify(ctx).result("Unauthorized - No such username!");
        verify(ctx).contentType(ContentType.JSON);
    }

    @Test
    void nullShouldReturn400() throws Exception {
        ctx.status(EMPTY_STATUS);
        ctx.result(EMPTY_RESULT);

        model.username = null;
        model.password = null;
        String inputBody = gson.toJson(model);

        when(ctx.body()).thenReturn(inputBody);
        Post.User.userLogin.handle(ctx);

        verify(ctx).status(400);
        verify(ctx).result("body has no username and password");
        verify(ctx).contentType(ContentType.JSON);
    }

    @Test
    void noBodyShouldReturn400() throws Exception {
        ctx.status(EMPTY_STATUS);
        ctx.result(EMPTY_RESULT);

        Post.User.userLogin.handle(ctx);

        verify(ctx).status(400);
        verify(ctx).result("body has no username and password");
        verify(ctx).contentType(ContentType.JSON);
    }

    @Test
    void validInfoShouldReturn200() throws Exception {
        ctx.status(EMPTY_STATUS);
        ctx.result(EMPTY_RESULT);

        model.username = "s175565";
        model.password = "kodeNWHN";
        String inputBody = gson.toJson(model);

        when(ctx.body()).thenReturn(inputBody);
        Post.User.userLogin.handle(ctx);

        verify(ctx).status(200);
        verify(ctx).result("user login was successful");
        verify(ctx).contentType(ContentType.JSON);
    }

    @Test
    void wrongPasswordShouldReturn401() throws Exception {
        ctx.status(EMPTY_STATUS);
        ctx.result(EMPTY_RESULT);

        model.username = "s175565";
        model.password = "wrongpassword";
        String inputBody = gson.toJson(model);

        when(ctx.body()).thenReturn(inputBody);
        Post.User.userLogin.handle(ctx);

        verify(ctx).status(401);
        verify(ctx).result("Unauthorized - Wrong password");
        verify(ctx).contentType(ContentType.JSON);
    }
}
