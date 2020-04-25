package resources;

import com.google.gson.Gson;
import database.DALException;
import database.collections.User;
import database.dao.Controller;
import database.dao.UserDAO;
import io.javalin.http.Context;
import javalin_resources.HttpMethods.Put;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class PutUserTest {
    private static Context ctx;
    static Controller beta;
    private static User testUser;

    @BeforeAll
    static void setUp() {
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        beta = Controller.getInstance();

        testUser = new User.Builder("abc")
                .password("abc")
                .status("pædagog")
                .setEmail("abc.test@user.com")
                .build();

        beta.createUser(testUser);
    }

    @AfterAll
    static void printAll() throws DALException {
        UserDAO userDAO = new UserDAO();
        System.out.println("Test-userlist after test: ");
        for (User user : userDAO.getUserList()) {
            if (user.getUsername().length() < 1 || user.getUsername().substring(0, 3).equalsIgnoreCase("abc")) {
                System.out.println(user);
                System.out.println("Deleting test user: " + user.getUsername());
                userDAO.deleteUser(user.getUsername());
            }
        }
    }

    /**
     * PUT USER TESTS
     */
    @Test
    void updateUser() throws Exception {
        JsonModels.UserModel userModel = new JsonModels.UserModel();
        userModel.usernameAdmin = "root";
        userModel.passwordAdmin = "root";
        userModel.username = testUser.getUsername();
        userModel.password = testUser.getPassword();
        userModel.firstname = "Hans";
        userModel.lastname = "Bertil";
        userModel.email = "kål";
        userModel.status = "admin";
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
        Put.PutUser.updateUser.handle(ctx);
        verify(ctx).status(201);
        verify(ctx).result("User updated");

        /**
         * User edge cases
         */
        // Forsøg på oprettelse af user der allerede er i db
      /*  ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);

        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(401);
        verify(ctx).result("Unauthorized - User already exists");*/

        // Forsøg på oprettelse af user uden username
    /*    ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);

        userModel.username = "";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(400);
        verify(ctx).result("Bad Request - Error in user data");*/

        /**
         * Admin edge cases
         */
        // Forkert admin status
   /*     ctx = mock(Context.class); // "mock-maker-inline" must be enabled
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
        verify(ctx).result("Unauthorized - Wrong admin username");*/

        // Fejlagtig oprettelse af bruger med forkert admin password
      /*  ctx = mock(Context.class); // "mock-maker-inline" must be enabled
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
*/

    }
}
