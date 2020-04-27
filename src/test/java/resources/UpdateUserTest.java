package resources;

import com.google.gson.Gson;
import com.mongodb.WriteResult;
import database.DALException;
import database.collections.Playground;
import database.collections.User;
import database.dao.Controller;
import database.dao.UserDAO;
import io.javalin.http.Context;
import javalin_resources.HttpMethods.Put;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.*;

class UpdateUserTest {
    private static Context ctx;
    private JsonModels.UserModel userModel = new JsonModels.UserModel();
    private static Gson gson;
    private static String json;

    @BeforeAll
    static void setUp() {
        //"mock-maker-inline" must be enabled
        ctx = mock(Context.class);

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
        userModel.website = "";
        userModel.playgroundsIDs = new String[2];
        gson = new Gson();
        json = gson.toJson(userModel);

       /* try {
            Controller.getInstance().deleteUser(userModel.username);
        } catch (Exception e) {

        }*/
    }

    @AfterAll
    static void printAll() throws DALException {
        UserDAO userDAO = new UserDAO();
        System.out.println("Test-userlist after test: ");
        for (User user : userDAO.getUserList()) {
            if (user.getUsername().length() < 1 || user.getUsername().substring(0, 3).equalsIgnoreCase("abc")) {
                System.out.println(user);
                System.out.println("Deleting test user: " + user.getUsername());
                //userDAO.deleteUser(user.getUsername());
            }
        }
    }

    @Test
    void updateUserWithoutPlaygroundIDs() throws Exception {
        // Normal update af bruger

        User updateUser = new User.Builder(userModel.username)
                .setPassword(userModel.password)
                .setFirstname(userModel.firstname)
                .setLastname(userModel.lastname)
                .setStatus(userModel.status)
                .build();

        Controller.getInstance().createUser(updateUser);
        Assertions.assertTrue(updateUser.getPlaygroundsIDs().isEmpty());

        Context ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);
        userModel.firstname = "KÅLHOVED";

        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Put.PutUser.updateUser.handle(ctx);
        verify(ctx).status(201);
        verify(ctx).result("User updated");

        updateUser = Controller.getInstance().getUser(updateUser.getUsername());
        Assertions.assertTrue(updateUser.getPlaygroundsIDs().isEmpty());
    }

    @Test
    void updateUserWithPlaygroundIDs() throws Exception {
        // Normal update af bruger

        Playground playground = new Playground.Builder("KålPladsen1")
                .setCommune("København Ø")
                .setZipCode(2100)
                .build();
        WriteResult writeResult = Controller.getInstance().createPlayground(playground);

        Playground playground2 = new Playground.Builder("KålPladsen2")
                .setCommune("København Ø")
                .setZipCode(2100)
                .build();
        WriteResult writeResult2 = Controller.getInstance().createPlayground(playground2);

        String[] pgIDs = new String[2];
        pgIDs[0] = writeResult.getUpsertedId().toString();
        pgIDs[1] = writeResult2.getUpsertedId().toString();

        User updateUser = new User.Builder(userModel.username)
                .setPassword(userModel.password)
                .setFirstname(userModel.firstname)
                .setLastname(userModel.lastname)
                .setStatus(userModel.status)
                .build();

        Controller.getInstance().createUser(updateUser);
        Assertions.assertTrue(updateUser.getPlaygroundsIDs().isEmpty());

        Context ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);

        userModel.firstname = "KÅLHOVED";
        userModel.playgroundsIDs = pgIDs;

        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Put.PutUser.updateUser.handle(ctx);
        verify(ctx).status(201);
        verify(ctx).result("User updated");

        updateUser = Controller.getInstance().getUser(updateUser.getUsername());
        Assertions.assertEquals(2, updateUser.getPlaygroundsIDs().size());

        playground = Controller.getInstance().getPlayground(playground.getName());
        Assertions.assertEquals(1, playground.getAssignedPedagogue().size());

        playground2 = Controller.getInstance().getPlayground(playground2.getName());
        Assertions.assertEquals(1, playground2.getAssignedPedagogue().size());

        Controller.getInstance().deletePlayground(playground.getName());
        Controller.getInstance().deletePlayground(playground2.getName());
    }

    /**
     * Edge cases
     */

    @Test
    void updateUserWithNoUserName() throws Exception {
        // Normal update af bruger
        Context ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);

        userModel.username = "";
        userModel.playgroundsIDs = new String[2];
        List<Playground> playgrounds = Controller.getInstance().getPlaygrounds();
        userModel.playgroundsIDs[0] = playgrounds.get(0).getId();
        userModel.playgroundsIDs[1] = playgrounds.get(1).getId();

        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Put.PutUser.updateUser.handle(ctx);
        verify(ctx).status(400);
        verify(ctx).result("Bad Request - Error in user data");
    }
}
