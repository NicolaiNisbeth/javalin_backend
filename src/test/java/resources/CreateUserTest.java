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

import java.nio.channels.WritableByteChannel;

import static org.mockito.Mockito.*;

class CreateUserTest {
    private static Context ctx;
    private JsonModels.UserModel userModel = new JsonModels.UserModel();
    private static Gson gson;
    private static String json;

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

    @Test
    void createUser() throws Exception {
        // Normal oprettelse af bruger

        Playground playground = new Playground.Builder("KålPladsen i Kildevældsparken")
                .setCommune("København Ø")
                .setZipCode(2100)
                .setStreetName("Vognmandsmarken")
                .setStreetNumber(69)
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("https://scontent-ams4-1.xx.fbcdn.net/v/t1.0-9/35925882_1752144438212095_2872486595854860288_o.jpg?_nc_cat=110&_nc_sid=6e5ad9&_nc_ohc=niAAIcBtSkEAX_InvHT&_nc_ht=scontent-ams4-1.xx&oh=9244ce211671c878bbb58aeb41d6e1d8&oe=5E9AE2B2")
                .build();
        WriteResult writeResult = Controller.getInstance().createPlayground(playground);

        userModel.playgroundsIDs[0] = writeResult.getUpsertedId().toString();

        Context ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);
        userModel.username = "abc-test-user";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(201);
        verify(ctx).result("User created.");

        User user = Controller.getInstance().getUser("abc-test-user");
        Assertions.assertEquals(1, user.getPlaygroundsIDs().size());
        System.out.println("ids " + user.getPlaygroundsIDs());
        Playground playground1 = Controller.getInstance().getPlayground("KålPladsen i Kildevældsparken");
        Assertions.assertEquals(1, playground1.getAssignedPedagogue().size());
        Controller.getInstance().deletePlayground("KålPladsen i Kildevældsparken");
    }


    @Test
    void deleteUser() throws Exception {
        Playground playground = new Playground.Builder("KålPladsen i Kildevældsparken")
                .setCommune("København Ø")
                .setZipCode(2100)
                .setStreetName("Vognmandsmarken")
                .setStreetNumber(69)
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .setImagePath("https://scontent-ams4-1.xx.fbcdn.net/v/t1.0-9/35925882_1752144438212095_2872486595854860288_o.jpg?_nc_cat=110&_nc_sid=6e5ad9&_nc_ohc=niAAIcBtSkEAX_InvHT&_nc_ht=scontent-ams4-1.xx&oh=9244ce211671c878bbb58aeb41d6e1d8&oe=5E9AE2B2")
                .build();
        WriteResult writeResult = Controller.getInstance().createPlayground(playground);

        userModel.playgroundsIDs[0] = writeResult.getUpsertedId().toString();

        Context ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);
        userModel.username = "abc-test-user";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(201);
        verify(ctx).result("User created.");

        User user = Controller.getInstance().getUser("abc-test-user");
        Assertions.assertEquals(1, user.getPlaygroundsIDs().size());
        System.out.println("ids " + user.getPlaygroundsIDs());
        Playground playground1 = Controller.getInstance().getPlayground("KålPladsen i Kildevældsparken");
        Assertions.assertEquals(1, playground1.getAssignedPedagogue().size());

        Controller.getInstance().deleteUser(user.getUsername());
        Playground playground2 = Controller.getInstance().getPlayground("KålPladsen i Kildevældsparken");


        Assertions.assertNull(playground2.getAssignedPedagogue());

       // System.out.println(playground2.getAssignedPedagogue());
        //Assertions.assertEquals(0, playground2.getAssignedPedagogue().size());


        Controller.getInstance().deletePlayground("KålPladsen i Kildevældsparken");


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
        //Clean up
        Controller.getInstance().deleteUser("abc");
    }

    @Test
    void userWithNoUsername() throws Exception {
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
                .setPassword("abc-wrong-adm-stat")
                .status("pædagog")
                .build();
        Controller.getInstance().createUser(abcUser);
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled
        ctx.result("");
        ctx.status(0);
        userModel.usernameAdmin = "abc-wrong-adm-stat";
        userModel.passwordAdmin = "abc-wrong-adm-stat";
        userModel.username = "abctest";
        userModel.password = "abc-kodeord";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        Post.PostUser.createUser.handle(ctx);
        verify(ctx).status(401);
        verify(ctx).result("Unauthorized - Wrong admin status");
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
