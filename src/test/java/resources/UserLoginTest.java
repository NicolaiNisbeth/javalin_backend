package resources;
import com.google.gson.Gson;
import database.DALException;
import database.collections.User;
import database.dao.Controller;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.ContentType;
import javalin_resources.HttpMethods.Post;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.mockito.Mockito.*;


public class UserLoginTest {

    private static Context ctx;
    private final int EMPTY_STATUS = 0;
    private final String EMPTY_RESULT = "";
    private JsonModels.LoginModel model;
    private Gson gson;
    private static User rootUser;

    @BeforeEach
    void setup(){
        ctx = mock(Context.class);
        model = new JsonModels.LoginModel();
        gson = new Gson();

        /*try {
            rootUser = Controller.getInstance().getUser("root");
        } catch (DALException e) {
            //root er oprettet
        }
        if (rootUser == null){

        }*/

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
        Post.PostUser.userLogin.handle(ctx);
        verify(ctx).status(200);
        verify(ctx).json("Success - User login successful");
        verify(ctx).json(Controller.getInstance().getUser("root"));
        verify(ctx).contentType(ContentType.JSON);
    }

    //NJL Forstår ikke promlemet her
    @Test
    void invalidInfoShouldReturn404() throws Exception {
        ctx.status(EMPTY_STATUS);
        ctx.result(EMPTY_RESULT);

        model.username = "testUsername";
        model.password = "testPassword";
        String inputBody = gson.toJson(model);

        when(ctx.body()).thenReturn(inputBody);
        Post.PostUser.userLogin.handle(ctx);

        verify(ctx).status(404);
        verify(ctx).json("Not found - No such username!");
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
        Post.PostUser.userLogin.handle(ctx);

        verify(ctx).status(400);
        verify(ctx).json("Bad request - body has no username or password");
        verify(ctx).contentType(ContentType.JSON);
    }

    @Test
    void noBodyShouldReturn400() throws Exception {
        ctx.status(EMPTY_STATUS);
        ctx.result(EMPTY_RESULT);

        Post.PostUser.userLogin.handle(ctx);

        verify(ctx).status(400);
        verify(ctx).json("Bad request - body has no username or password");
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
        Post.PostUser.userLogin.handle(ctx);

        verify(ctx).status(200);
        verify(ctx).json("Success - User login successful");
        verify(ctx).json(Controller.getInstance().getUser("s175565"));
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
        Post.PostUser.userLogin.handle(ctx);

        verify(ctx).status(401);
        verify(ctx).json("Unauthorized - Wrong password");
        verify(ctx).contentType(ContentType.JSON);
    }
}
