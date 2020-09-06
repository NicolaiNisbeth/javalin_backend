package resources;

import JWT.JWTHandler;
import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import com.mongodb.MongoException;
import database.Controller;
import database.dto.UserDTO;
import database.exceptions.NoModificationException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import javalinjwt.examples.JWTResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;
import org.mindrot.jbcrypt.BCrypt;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.util.*;


public class User implements Tag {

  public static Handler getUserPicture = ctx -> {
    File homeFolder = new File(System.getProperty("user.home"));
    Path path = Paths.get(String.format(homeFolder.toPath() +
            "/server_resource/users/%s.png", ctx.pathParam("username")));

    File initialFile = new File(path.toString());
    InputStream targetStream = null;
    try {
      targetStream = new FileInputStream(initialFile);

    } catch (IOException e) {
      System.out.println("Server: User have no profile picture...");
    }

    if (targetStream == null) {
      targetStream = User.class.getResourceAsStream("/images/users/random_user.png");
    }
    ctx.result(targetStream).contentType("image/png");
  };

  public static Handler deleteUser = ctx -> {
    JSONObject jsonObject, deleteUserModel;
    jsonObject = new JSONObject(ctx.body());
    deleteUserModel = jsonObject.getJSONObject("deleteUserModel");

    String username = deleteUserModel.getString(USERNAME);
    Controller.getInstance().deleteUser(username);
    ctx.status(200);
    ctx.json("OK - User deleted");
    ctx.contentType("json");
  };

  public static Handler getUsers = ctx -> {
    ctx.json(Controller.getInstance().getUsers()).contentType("json");
  };

  public static Handler getEmployees = ctx -> {
    List<UserDTO> users;
    try {
      users = Controller.getInstance().getUsers();
    } catch (NoSuchElementException e) {
      ctx.status(HttpStatus.NOT_FOUND_404);
      ctx.result("Not found - no users in database");
      ctx.contentType(ContentType.JSON);
      return;
    } catch (Exception e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
      ctx.result("Internal error - failed to fetch users in database");
      ctx.contentType(ContentType.JSON);
      return;
    }

    List<UserDTO> returnUsers = new ArrayList<>();
    for (UserDTO user : users) {
      if (!user.getStatus().equalsIgnoreCase("client")) {
        returnUsers.add(user);
      }
    }
    ctx.json(returnUsers).contentType("json").status(201);
  };

  public static Handler createUser = ctx -> {
    String usernameAdmin, passwordAdmin, username, password,
      firstName, lastName, email, status, website;
    JSONArray phoneNumbers, playgroundIDs = null;
    JSONObject jsonObject;

    try {
      String usermodel = ctx.formParam(("usermodel"));
      jsonObject = new JSONObject(usermodel);
      username = jsonObject.getString(USERNAME);
      password = jsonObject.getString(PASSWORD);
      firstName = jsonObject.getString(FIRSTNAME);
      lastName = jsonObject.getString(LASTNAME);
      email = jsonObject.getString(EMAIL);
      website = jsonObject.getString(WEBSITE);
      phoneNumbers = jsonObject.getJSONArray(PHONENUMBERS);
      status = jsonObject.getString(STATUS);
      if (status.isEmpty()) status = "client"; //TODO: create an enum
      if (username.isEmpty() || password.isEmpty() || firstName.isEmpty()) throw new NullPointerException();
    } catch (JSONException | NullPointerException e) {
      ctx.status(HttpStatus.BAD_REQUEST_400);
      ctx.result("Bad request - error in user data");
      ctx.contentType(ContentType.JSON);
      return;
    }

    // check if admin user can create another user
    boolean privileges = true;
    try {
      usernameAdmin = jsonObject.getString(USERNAME_ADMIN);
      passwordAdmin = jsonObject.getString(PASSWORD_ADMIN);
      playgroundIDs = jsonObject.getJSONArray(PLAYGROUNDSNAMES);
      boolean isAdminUpdatingUser = !username.equalsIgnoreCase(usernameAdmin);
      boolean isAdminAuthorized = Shared.verifyAdminCredentials(usernameAdmin, passwordAdmin, ctx);
      if (isAdminUpdatingUser && !isAdminAuthorized) {
        ctx.status(HttpStatus.UNAUTHORIZED_401);
        ctx.json(String.format("Unauthorized - User %s has no privileges to create user %s", usernameAdmin, username));
        ctx.contentType(ContentType.JSON);
        return;
      }
    } catch (JSONException e) {
      privileges = false;
    }

    boolean isUsernameAvailable = false;
    try {
      Controller.getInstance().getUser(username);
    } catch (NoSuchElementException e) {
      isUsernameAvailable = true;
    }

    if (!isUsernameAvailable) {
      ctx.status(HttpStatus.CONFLICT_409);
      ctx.result("Conflict - Username is already in use");
      ctx.contentType(ContentType.JSON);
      return;
    }

    // setup user fields
    UserDTO newUser = new UserDTO.Builder(username)
      .setPassword(password)
      .setFirstname(firstName)
      .setLastname(lastName)
      .setStatus(status)
      .setEmail(email)
      .setWebsite(website)
      .setImagePath(String.format(IMAGEPATH + "/users/%s/profile-picture", username))
      .build();

    // add phone numbers
    String[] usersNewPhoneNumbers = new String[phoneNumbers.length()];
    for (int i = 0; i < phoneNumbers.length(); i++) {
      try {
        usersNewPhoneNumbers[i] = (String) phoneNumbers.get(i);
      } catch (ClassCastException e) {
      }
    }
    newUser.setPhoneNumbers(usersNewPhoneNumbers);

    // add image
    try {
      BufferedImage bufferedImage = ImageIO.read(ctx.uploadedFile("image").getContent());
      saveUserPicture(username, bufferedImage);
    } catch (Exception e) {
      System.out.println("Server: No image in upload");
    }

    // add references to playgrounds
    if (privileges) {
      try {
        Set<String> usersNewPGIds = new HashSet<>();
        for (int i = 0; i < playgroundIDs.length(); i++) {
          try {
            usersNewPGIds.add((String) playgroundIDs.get(i));
          } catch (ClassCastException e) {
          }
        }
        newUser.setPlaygroundsNames(usersNewPGIds);
        Controller.getInstance().createUser(newUser);
        for (String playgroundID : usersNewPGIds) {
          Controller.getInstance().addPedagogueToPlayground(playgroundID, username);
        }

      } catch (NoSuchElementException | NoModificationException | MongoException e) {
        ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
        ctx.result("Server error - creating user failed");
        ctx.contentType(ContentType.JSON);
        return;
      }
    }

    try {
      if (!privileges) Controller.getInstance().createUser(newUser);
      ctx.status(HttpStatus.CREATED_201);
      ctx.result("Created - User was signed up successfully");
      ctx.json(newUser);
      ctx.contentType(ContentType.JSON);
    } catch (NoModificationException e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
      ctx.result("Server error - creating user failed");
      ctx.contentType(ContentType.JSON);
    }
  };

  public static Handler login = ctx -> {
    String username, password;
    try {
      JSONObject jsonObject = new JSONObject(ctx.body());
      username = jsonObject.getString(USERNAME);
      password = jsonObject.getString(PASSWORD);
    } catch (JSONException | NullPointerException e) {
      ctx.status(HttpStatus.BAD_REQUEST_400);
      ctx.json("Bad request - Body has no username or password");
      ctx.contentType(ContentType.JSON);
      return;
    }

    UserDTO fetchedUser;
    boolean root = username.equalsIgnoreCase("root");
    if (root) {
      try {
        fetchedUser = getOrCreateRootUser(username);
        String token = JWTHandler.provider.generateToken(fetchedUser);
        ctx.header("Authorization", new JWTResponse(token).jwt);
        ctx.header("Access-Control-Expose-Headers", "Authorization");
        ctx.status(HttpStatus.OK_200);
        ctx.result("Success - User login with root was successful");
        ctx.json(fetchedUser);
        ctx.contentType(ContentType.JSON);
        return;
      } catch (Exception e) {
        ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
        ctx.contentType(ContentType.JSON);
        ctx.result("Internal error - Creating root user failed");
        return;
      }
    }

    Bruger bruger = getUserInBrugerAuthorization(username, password);
    try {
      fetchedUser = Controller.getInstance().getUser(username);
      System.out.println("USER in mongo " + fetchedUser);
    } catch (NoSuchElementException | IllegalArgumentException e) {
      fetchedUser = null;
    } catch (Exception e) {
      // if database is down - don't allow login even if user is valid
      // in bruger authorization module
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
      ctx.contentType(ContentType.JSON);
      ctx.result("Internal error - Couldn't connect to database");
      return;
    }

    // user was not found in user authorization and database
    if (bruger == null && fetchedUser == null) {
      ctx.status(HttpStatus.NOT_FOUND_404);
      ctx.contentType(ContentType.JSON);
      ctx.result("Not found - wrong username");
      return;
    }

    // if user exists in nordfalk but not in database
    if (fetchedUser == null) {
      fetchedUser = new UserDTO.Builder(bruger.brugernavn)
        .setFirstname(bruger.fornavn)
        .setLastname(bruger.efternavn)
        .setEmail(bruger.email)
        .setPassword(bruger.adgangskode)
        .status(STATUS_PEDAGOG)
        .setWebsite(bruger.ekstraFelter.get("webside").toString())
        .setLoggedIn(true)
        .setImagePath(String.format(IMAGEPATH + "/users/%s/profile-picture", bruger.brugernavn))
        .build();
      Controller.getInstance().createUser(fetchedUser);
    }

    boolean userIsCreatedByAdmin = !fetchedUser.isLoggedIn() && bruger != null;
    if (userIsCreatedByAdmin) {
      fetchedUser.setFirstname(bruger.fornavn);
      fetchedUser.setLastname(bruger.efternavn);
      fetchedUser.setEmail(bruger.email);
      fetchedUser.setStatus(fetchedUser.getStatus());
      fetchedUser.setWebsite(bruger.ekstraFelter.get("webside").toString());
      fetchedUser.setLoggedIn(true);
      fetchedUser.setImagePath(String.format(IMAGEPATH + "/users/%s/profile-picture", bruger.brugernavn));
      Controller.getInstance().updateUser(fetchedUser);
    }

    // validate credentials
    String hashed = fetchedUser.getPassword();
    if (BCrypt.checkpw(password, hashed)) {
      String token = JWTHandler.provider.generateToken(fetchedUser);
      ctx.header("Authorization", new JWTResponse(token).jwt);
      // the golden line. All hail this statement
      ctx.header("Access-Control-Expose-Headers", "Authorization");
      ctx.status(HttpStatus.OK_200);
      ctx.result("Success - User login was successful");
      ctx.json(fetchedUser);

      ctx.contentType(ContentType.JSON);
    } else {
      ctx.status(HttpStatus.UNAUTHORIZED_401);
      ctx.contentType(ContentType.JSON);
      ctx.result("Unauthorized - Wrong password");

    }
  };

  public static Handler updateUser = ctx -> {
    String username, password, firstName, lastName, email, website;
    String usernameAdmin, passwordAdmin, status = null;
    JSONArray phoneNumbers, playgroundIDs = null;
    JSONObject jsonObject;

    // check if possible to update client
    try {
      String usermodel = ctx.formParam(("usermodel"));
      jsonObject = new JSONObject(usermodel);
      username = jsonObject.getString(USERNAME);
      password = jsonObject.getString(PASSWORD);
      firstName = jsonObject.getString(FIRSTNAME);
      lastName = jsonObject.getString(LASTNAME);
      email = jsonObject.getString(EMAIL);
      website = jsonObject.getString(WEBSITE);
      phoneNumbers = jsonObject.getJSONArray(PHONENUMBERS);
    } catch (JSONException | NullPointerException e) {
      ctx.status(HttpStatus.BAD_REQUEST_400);
      ctx.result("Bad request - error in user data");
      ctx.contentType(ContentType.JSON);
      return;
    }

    // check if admin user can update another user
    boolean privileges = true;

    try {
      usernameAdmin = jsonObject.getString(USERNAME_ADMIN);
      passwordAdmin = jsonObject.getString(PASSWORD_ADMIN);
      status = jsonObject.getString(STATUS);
      playgroundIDs = jsonObject.getJSONArray(PLAYGROUNDSNAMES);
      boolean isAdminUpdatingUser = !username.equalsIgnoreCase(usernameAdmin);
      boolean isAdminAuthorized = Shared.verifyAdminCredentials(usernameAdmin, passwordAdmin, ctx);
      if (isAdminUpdatingUser && !isAdminAuthorized) {
        ctx.status(HttpStatus.UNAUTHORIZED_401);
        ctx.json(String.format("Unauthorized - User %s has no privileges to update user %s", usernameAdmin, username));
        ctx.contentType(ContentType.JSON);
        return;
      }
    } catch (JSONException e) {
      privileges = false;
    }

    // find user in db
    UserDTO userToUpdate;
    try {
      userToUpdate = Controller.getInstance().getUser(username);
    } catch (NoSuchElementException | IllegalArgumentException e) {
      ctx.status(HttpStatus.NOT_FOUND_404);
      ctx.result("Not found - user does not exist");
      ctx.contentType(ContentType.JSON);
      return;
    }

    // update user fields
    userToUpdate.setFirstname(firstName);
    userToUpdate.setLastname(lastName);
    userToUpdate.setEmail(email);
    userToUpdate.setWebsite(website);
    userToUpdate.setImagePath(String.format(IMAGEPATH + "/users/%s/profile-picture", username));
    String[] usersNewPhoneNumbers = new String[phoneNumbers.length()];

    for (int i = 0; i < phoneNumbers.length(); i++) {
      try {
        usersNewPhoneNumbers[i] = (String) phoneNumbers.get(i);
      } catch (ClassCastException e) {
      }
    }
    userToUpdate.setPhoneNumbers(usersNewPhoneNumbers);
    try {
      BufferedImage bufferedImage = ImageIO.read(ctx.uploadedFile("image").getContent());
      saveUserPicture(username, bufferedImage);
    } catch (Exception e) {
      System.out.println("Server: No image in upload");
    }

    // check if non-trivial data can be updated
    if (privileges) {
      try {
        // remove references to old playgrounds
        Set<String> usersOldPGIds = userToUpdate.getPlaygroundsNames();
        if (usersOldPGIds != null && !usersOldPGIds.isEmpty()) {
          for (String oldPlaygroundName : usersOldPGIds) {
            Controller.getInstance().removePedagogueFromPlayground(oldPlaygroundName, userToUpdate.getUsername());
          }
        }
        // add references to new playgrounds
        Set<String> usersNewPGIds = new HashSet<>();
        for (int i = 0; i < playgroundIDs.length(); i++) {
          try {
            usersNewPGIds.add((String) playgroundIDs.get(i));
          } catch (ClassCastException e) {
          }
        }
        for (String playgroundID : usersNewPGIds) {
          Controller.getInstance().addPedagogueToPlayground(playgroundID, username);
        }

        userToUpdate.setPlaygroundsNames(usersNewPGIds);
      } catch (NoSuchElementException | NoModificationException | MongoException e) {
      }
      userToUpdate.setStatus(status);
    }

    try {
      Controller.getInstance().updateUser(userToUpdate);
      ctx.status(HttpStatus.OK_200);
      ctx.result("OK - user was updated successfully");
      ctx.json(userToUpdate);
      ctx.contentType(ContentType.JSON);
    } catch (NoModificationException e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
      ctx.result("Server error - Update user failed");
      ctx.contentType(ContentType.JSON);
    }
  };

  private static Bruger getUserInBrugerAuthorization(String username, String password) {
    Bruger bruger = null;
    try {
      Brugeradmin ba = (Brugeradmin) Naming.lookup(Brugeradmin.URL);
      bruger = ba.hentBruger(username, password);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return bruger;
  }

  private static UserDTO getOrCreateRootUser(String username) throws NoModificationException {
    UserDTO root;
    try {
      root = Controller.getInstance().getUser(username);
    } catch (NoSuchElementException e) {
      root = new UserDTO.Builder("root")
        .status("admin")
        .setPassword("root")
        .setFirstname("Københavns")
        .setLastname("Kommune")
        .build();
      Controller.getInstance().createUser(root);
    }
    return root;
  }

  public static void saveUserPicture(String username, BufferedImage bufferedImage) {
    File homeFolder = new File(System.getProperty("user.home"));
    Path path = Paths.get(String.format(homeFolder.toPath() +
      "/server_resource/users/%s.png", username));

    //String path = String.format("src/main/resources/images/profile_pictures/%s.png", username);
    File imageFile = new File(path.toString());
    try {
      ImageIO.write(bufferedImage, "png", imageFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
