package resources;

import com.google.gson.Gson;
import database.Controller;
import database.IController;
import database.exceptions.NoModificationException;
import database.TestDB;
import database.dto.PlaygroundDTO;
import database.dto.UserDTO;
import io.javalin.http.Context;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;

class CreateUserTest {
    private static Context ctx;
    private JsonModels.UserModel userModel = new JsonModels.UserModel();
    private static Gson gson;
    private static String json;
    private static PlaygroundDTO playground;
    private static IController controller = Controller.getInstance();

    @BeforeAll
    static void setUp() {
        //"mock-maker-inline" must be enabled
        ctx = mock(Context.class);
        controller.setDataSource(TestDB.getInstance());
        controller.killAll();

    }

    @BeforeEach
    void setUpUser() throws NoModificationException {
        controller.setDataSource(TestDB.getInstance());
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
            controller.getUser("root");
        } catch (NoSuchElementException e) {
            UserDTO root = new UserDTO.Builder("root")
                    .status("admin")
                    .setPassword("root")
                    .setFirstname("Københavns")
                    .setLastname("Kommune")
                    .build();
            controller.createUser(root);
        }

        playground = new PlaygroundDTO.Builder("KålPladsen")
                .setCommune("København Ø")
                .setZipCode(2100)
                .setStreetName("Vognmandsmarken")
                .setStreetNumber(69)
                .setToiletPossibilities(true)
                .setHasSoccerField(true)
                .build();
        controller.createPlayground(playground);
    }

    @AfterEach
    void cleanUp() {
        try {
            controller.deletePlayground("KålPladsen");
        } catch (Exception e) {
        }
        try {
            controller.deleteUser("abc");
        } catch (Exception e) {

        }

    }

    @Test
    void createUser() throws Exception {
        // Normal oprettelse af bruger
        userModel.playgroundsIDs[0] = playground.getName();
        Context ctx = mock(Context.class); // "mock-maker-inline" must be enabled

        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        User.createUser.handle(ctx);
        verify(ctx).status(201);
        //verify(ctx).json("Created - User created");
        controller.addPedagogueToPlayground("KålPladsen", "abc");

        UserDTO user = controller.getUser("abc");
        Assertions.assertEquals(1, user.getPlaygroundsNames().size());
        System.out.println("ids " + user.getPlaygroundsNames());
        PlaygroundDTO playground1 = controller.getPlayground("KålPladsen");
        Assertions.assertEquals(1, playground1.getAssignedPedagogue().size());
    }

    @Test
    void deleteUser() throws Exception {
        controller.createPlayground(playground);
        userModel.playgroundsIDs[0] = playground.getName();

        Context ctx = mock(Context.class);

        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        User.createUser.handle(ctx);
        verify(ctx).status(201);
        //verify(ctx).json("Created - User created");

        controller.addPedagogueToPlayground("KålPladsen", "abc");
        UserDTO user = controller.getUser("abc");
        Assertions.assertEquals(1, user.getPlaygroundsNames().size());


        playground = controller.getPlayground("KålPladsen");
        Assertions.assertEquals(1, playground.getAssignedPedagogue().size());
        controller.deleteUser(user.getUsername());
        playground = controller.getPlayground("KålPladsen");
        Assertions.assertEquals(0, playground.getAssignedPedagogue().size());
    }
    /**
     * User edge cases
     */
    @Test
    void dublicateUserInDB() throws Exception {
        UserDTO abcUser = new UserDTO.Builder("abc")
                .setPassword("abc")
                .status("pædagog")
                .build();
        controller.createUser(abcUser);

        // Forsøg på oprettelse af user der allerede er i db
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled

        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        User.createUser.handle(ctx);
        verify(ctx).status(401);
        //verify(ctx).result("Unauthorized - User already exists");
    }

    @Test
    void userWithNoUsername() throws Exception {
        // Forsøg på oprettelse af user uden username
        ctx = mock(Context.class);

        userModel.username = "";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        User.createUser.handle(ctx);
        verify(ctx).status(400);
        //verify(ctx).result("Bad Request - Error in user data");
    }

    @Test
    void userWithNoPassword() throws Exception {
        // Forsøg på oprettelse af user uden setPassword
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled

        userModel.password = "";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        User.createUser.handle(ctx);
        verify(ctx).status(400);
        //verify(ctx).result("Bad Request - Error in user data");
    }

    /**
     * Admin edge cases
     */
    @Test
    void wrongAdminStatus() throws Exception {
        // Forkert admin status
        UserDTO abcUser = new UserDTO.Builder("abc-wrong-adm-stat")
                .setPassword("abc")
                .status("pædagog")
                .build();
        controller.createUser(abcUser);
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled

        userModel.usernameAdmin = "abc-wrong-adm-stat";
        userModel.passwordAdmin = "abc-wrong-adm-stat";
        userModel.username = "abc";
        userModel.password = "abc";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        User.createUser.handle(ctx);
        verify(ctx).status(401);
        //verify(ctx).result("Unauthorized - Wrong admin status");

        controller.deleteUser("abc-wrong-adm-stat");
    }

    @Test
    void wrongAdminUsername() throws Exception {
        // Forkert admin brugernavn
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled

        userModel.usernameAdmin = "rot";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        User.createUser.handle(ctx);
        verify(ctx).status(401);
        //verify(ctx).result("Unauthorized - Wrong admin username");
    }

    @Test
    void wrongAdminPassword() throws Exception {
        //Forkert admin setPassword
        ctx = mock(Context.class); // "mock-maker-inline" must be enabled

        userModel.passwordAdmin = "rot";
        json = gson.toJson(userModel);
        when(ctx.formParam("usermodel")).thenReturn(json);
        when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
        User.createUser.handle(ctx);
        verify(ctx).status(401);
        //verify(ctx).result("Unauthorized - Wrong admin setPassword");
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
