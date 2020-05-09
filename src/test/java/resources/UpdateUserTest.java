package resources;

import com.google.gson.Gson;
import database.Controller;
import database.IController;
import database.TestDB;
import database.dto.PlaygroundDTO;
import database.dto.UserDTO;
import database.exceptions.NoModificationException;
import io.javalin.http.Context;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.*;

class UpdateUserTest {
  private static Gson gson;
  private static String json;
  private static IController controller = Controller.getInstance();
  private JsonModels.UserModel userModel = new JsonModels.UserModel();

  @BeforeAll
  static void setUp() throws NoModificationException {
    //"mock-maker-inline" must be enabled
    controller.setDataSource(TestDB.getInstance());
    controller.killAll();

    try {
      controller.getUser("root");
    } catch (NoSuchElementException e) {
      UserDTO root = new UserDTO.Builder("root")
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
  }

  @Test
  void updateUserWithoutPlaygroundIDs() throws Exception {
    // Normal update af bruger

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
    userModel.playgroundsNames = new String[0];
    gson = new Gson();
    json = gson.toJson(userModel);

    UserDTO updateUser = new UserDTO.Builder(userModel.username)
      .setPassword(userModel.password)
      .setFirstname(userModel.firstname)
      .setLastname(userModel.lastname)
      .setStatus(userModel.status)
      .build();
    controller.createUser(updateUser);

    Assertions.assertTrue(updateUser.getPlaygroundsNames().isEmpty());
    UserDTO fromDB = controller.getUser(updateUser.getUsername());
    System.out.println(fromDB.getPlaygroundsNames());

    Context ctx = mock(Context.class); // "mock-maker-inline" must be enabled
    json = gson.toJson(userModel);
    when(ctx.formParam("usermodel")).thenReturn(json);
    when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
    User.updateUser.handle(ctx);


    verify(ctx).status(200);
    verify(ctx).result("OK - user was updated successfully");

    updateUser = controller.getUser(updateUser.getUsername());
    Assertions.assertTrue(updateUser.getPlaygroundsNames().isEmpty());

    controller.deleteUser(userModel.username);
  }

  @Test
  void updateUserWithPlaygroundIDs() throws Exception {
    // Normal update af bruger
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
    userModel.playgroundsNames = new String[0];
    gson = new Gson();
    json = gson.toJson(userModel);

    PlaygroundDTO playground = new PlaygroundDTO.Builder("KålPladsen1")
      .setCommune("København Ø")
      .setZipCode(2100)
      .build();
    controller.createPlayground(playground);

    PlaygroundDTO playground2 = new PlaygroundDTO.Builder("KålPladsen2")
      .setCommune("København Ø")
      .setZipCode(2100)
      .build();
    controller.createPlayground(playground2);

    String[] pgIDs = new String[2];
    pgIDs[0] = "KålPladsen1";
    pgIDs[1] = "KålPladsen2";

    UserDTO updateUser = new UserDTO.Builder(userModel.username)
      .setPassword(userModel.password)
      .setFirstname(userModel.firstname)
      .setLastname(userModel.lastname)
      .setStatus(userModel.status)
      .build();

    controller.createUser(updateUser);
    Assertions.assertTrue(updateUser.getPlaygroundsNames().isEmpty());

    Context ctx = mock(Context.class); // "mock-maker-inline" must be enabled

    userModel.firstname = "KÅLHOVED";
    userModel.playgroundsNames = pgIDs;

    json = gson.toJson(userModel);
    when(ctx.formParam("usermodel")).thenReturn(json);
    when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
    User.updateUser.handle(ctx);
    verify(ctx).status(HttpStatus.OK_200);
    verify(ctx).result("OK - user was updated successfully");

    updateUser = controller.getUser(updateUser.getUsername());
    Assertions.assertEquals(2, updateUser.getPlaygroundsNames().size());

      /*  playground = controller.getPlayground(playground.getName());
        Assertions.assertEquals(1, playground.getAssignedPedagogue().size());

        playground2 = controller.getPlayground(playground2.getName());
        Assertions.assertEquals(1, playground2.getAssignedPedagogue().size());
*/
    controller.deleteUser(updateUser.getUsername());
    controller.deletePlayground(playground.getName());
    controller.deletePlayground(playground2.getName());

  }

  /**
   * Edge cases
   */

  @Test
  void updateUserWithNoUserName() throws Exception {
    userModel = new JsonModels.UserModel();
    userModel.usernameAdmin = "root";
    userModel.passwordAdmin = "root";
    userModel.username = "";
    userModel.password = "abc";
    userModel.firstname = "Hans";
    userModel.lastname = "Bertil";
    userModel.email = "kål";
    userModel.status = "pædagog";
    userModel.imagePath = "";
    userModel.phoneNumbers = new String[2];
    userModel.website = "";
    userModel.playgroundsNames = new String[0];
    gson = new Gson();
    json = gson.toJson(userModel);

    PlaygroundDTO playground = new PlaygroundDTO.Builder("KålPladsen1")
      .setCommune("København Ø")
      .setZipCode(2100)
      .build();
    controller.createPlayground(playground);

    PlaygroundDTO playground2 = new PlaygroundDTO.Builder("KålPladsen2")
      .setCommune("København Ø")
      .setZipCode(2100)
      .build();
    controller.createPlayground(playground2);


    Context ctx = mock(Context.class); // "mock-maker-inline" must be enabled

    userModel.username = "";
    userModel.playgroundsNames = new String[2];
    List<PlaygroundDTO> playgrounds = controller.getPlaygrounds();
    userModel.playgroundsNames[0] = playgrounds.get(0).getId();
    userModel.playgroundsNames[1] = playgrounds.get(1).getId();

    json = gson.toJson(userModel);
    when(ctx.formParam("usermodel")).thenReturn(json);
    when(ctx.uploadedFile(Mockito.any())).thenCallRealMethod();
    User.updateUser.handle(ctx);
    verify(ctx).status(HttpStatus.NOT_FOUND_404);
    verify(ctx).result("Not found - user does not exist");
  }
}
