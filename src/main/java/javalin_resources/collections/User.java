package javalin_resources.collections;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import com.mongodb.WriteResult;
import database.Controller;
import database.dto.UserDTO;
import database.exceptions.DALException;
import database.exceptions.NoModificationException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.ContentType;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;


public class User implements Tag {

  /**
   * DELETE
   */
  public static Handler deleteUser = ctx -> {
    JSONObject jsonObject, deleteUserModel;
    jsonObject = new JSONObject(ctx.body());
    deleteUserModel = jsonObject.getJSONObject("deleteUserModel");
    String usernameAdmin = deleteUserModel.getString(USERNAME_ADMIN);
    String passwordAdmin = deleteUserModel.getString(PASSWORD_ADMIN);
    String username = deleteUserModel.getString(USERNAME);
    // todo slet ham fra legeplader også
    // todo Nisbeth?? JSONArray adminRightsOfNewUser = jsonObject.getJSONArray("userAdminRights");

    boolean adminAuthorized = Shared.checkAdminCredentials(usernameAdmin, passwordAdmin, ctx);
    if (!adminAuthorized) {
      return;
    }

    Controller.getInstance().deleteUser(username);
    ctx.status(200);
    ctx.json("OK - User deleted");
    ctx.contentType("json");
  };

  /**
   * GET
   */
  public static Handler getUserPicture = ctx -> {
    File homeFolder = new File(System.getProperty("user.home"));
    Path path = Paths.get(String.format(homeFolder.toPath() +
      "/server_resource/profile_images/%s.png", ctx.pathParam("username")));

    File initialFile = new File(path.toString());
    InputStream targetStream = null;
    try {
      targetStream = new FileInputStream(initialFile);
         /*   BufferedImage in = ImageIO.read(initialFile);
            UserAdminResource.printImage(in);*/

    } catch (IOException e) {
      System.out.println("Server: User have no profile picture...");
    }

    if (targetStream != null) {
      ctx.result(targetStream).contentType("image/png");
    } else {
      System.out.println("Server: Returning random user picture...");
      targetStream = User.class.getResourceAsStream("/images/profile_pictures/random_user.png");
      ctx.result(targetStream).contentType("image/png");
    }
  };

  public static Handler getAllUsers = ctx -> {
    ctx.json(Controller.getInstance().getUsers()).contentType("json");
  };

  /**
   * POST
   */
  public static Handler createParticipantsToPlaygroundEvent = ctx -> {

  };

  public static Handler createUser = ctx -> {
    BufferedImage bufferedImage;
    String usernameAdmin, passwordAdmin, username, password,
      firstName, lastName, email, status, website;
    JSONArray phoneNumbers, playgroundIDs;

    try {
      String usermodel = ctx.formParam(("usermodel"));
      JSONObject jsonObject = new JSONObject(usermodel);
      usernameAdmin = jsonObject.getString(USERNAME_ADMIN);
      passwordAdmin = jsonObject.getString(PASSWORD_ADMIN);
      username = jsonObject.getString(USERNAME);
      password = jsonObject.getString(PASSWORD);
      firstName = jsonObject.getString(FIRSTNAME);
      lastName = jsonObject.getString(LASTNAME);
      email = jsonObject.getString(EMAIL);
      status = jsonObject.getString(STATUS);
      website = jsonObject.getString(WEBSITE);
      //todo test med angular
      playgroundIDs = jsonObject.getJSONArray(PLAYGROUNDSIDS);
      phoneNumbers = jsonObject.getJSONArray(PHONENUMBERS);
      if (username.length() < 1 || password.length() < 1) {
        throw new DALException("Missing username or setPassword");

      }
    } catch (Exception e) {
      e.printStackTrace();
      ctx.status(400);
      ctx.result("Bad Request - Error in user data");
      return;
    }

    UserDTO newUser = null;
    boolean adminAuthorized = Shared.checkAdminCredentials(usernameAdmin, passwordAdmin, ctx);
    if (!adminAuthorized) {
      return;
    }

    //Se om brugeren allerede er oprettet
    try {
      newUser = Controller.getInstance().getUser(username);
    } catch (NoSuchElementException e) {
      //Brugeren er ikke i databasen og kan derfor oprettes
    }
    if (newUser != null) {
      ctx.status(401);
      ctx.result("Unauthorized - User already exists");
      return;
    }

    newUser = new UserDTO.Builder(username)
      .setPassword(password)
      .setFirstname(firstName)
      .setLastname(lastName)
      .setStatus(status)
      .setEmail(email)
      .setWebsite(website)
      .setImagePath(String.format(IMAGEPATH + "/%s/profile-picture", username))
      .build();

    if (phoneNumbers.length() > 0) {
      String[] usersNewPhoneNumbers = new String[phoneNumbers.length()];
      if (phoneNumbers.get(0) != null) {
        usersNewPhoneNumbers[0] = (String) phoneNumbers.get(0);
      }
      if (phoneNumbers.get(1) != null) {
        usersNewPhoneNumbers[1] = (String) phoneNumbers.get(1);
      }
      newUser.setPhoneNumbers(usersNewPhoneNumbers);
    }

    try {
      bufferedImage = ImageIO.read(ctx.uploadedFile("image").getContent());
      Shared.saveProfilePicture(username, bufferedImage);
    } catch (Exception e) {
      System.out.println("Server: No profile picture was chosen...");
    }

    if (playgroundIDs != null) {
      for (Object id : playgroundIDs) {
        newUser.getPlaygroundsIDs().add(id.toString());
      }
    }

    WriteResult ws = Controller.getInstance().createUser(newUser);
    if (ws.wasAcknowledged()) {
      ctx.status(201);
      ctx.json("Created - User created");
      ctx.json(newUser);


      //Controller.getInstance().addPedagogueToPlayground(newUser);

    } else {
      ctx.status(401);
      ctx.result("User was not created");
    }
  };

  public static Handler userLogin = ctx -> {
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
        .setImagePath(String.format(IMAGEPATH + "/%s/profile-picture", bruger.brugernavn))
        .build();
      Controller.getInstance().createUser(fetchedUser);
    }

    boolean userIsCreatedByAdmin = !fetchedUser.isLoggedIn() && bruger != null;
    if (userIsCreatedByAdmin) {
      fetchedUser.setFirstname(bruger.fornavn);
      fetchedUser.setLastname(bruger.efternavn);
      fetchedUser.setEmail(bruger.email);
      fetchedUser.setStatus(fetchedUser.getStatus());
      //user.setPassword(user.getPassword());
      fetchedUser.setWebsite(bruger.ekstraFelter.get("webside").toString());
      fetchedUser.setLoggedIn(true);
      fetchedUser.setImagePath(String.format(IMAGEPATH + "/%s/profile-picture", bruger.brugernavn));
      Controller.getInstance().updateUser(fetchedUser);
    }

    // validate credentials
    String hashed = fetchedUser.getPassword();
    if (BCrypt.checkpw(password, hashed)) {
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

  /**
   * PUT
   */
  public static Handler resetPassword = ctx -> {
    JSONObject jsonObject = new JSONObject(ctx.body());
    String username = jsonObject.getString(USERNAME);
    UserDTO user = null;

    try {
      user = Controller.getInstance().getUser(username);
    } catch (NoSuchElementException e) {
      ctx.status(401).result("Unauthorized");
      e.printStackTrace();
    }
    if (user.getEmail() == null) {
      //reset setPassword
    } else {
      try {
        String newPassword = "1234";
        user.setPassword(newPassword);
        Controller.getInstance().updateUser(user);
        Controller.getInstance().getUser(user.getUsername());
        SendMail.sendMail("Your new setPassword", "Your new setPassword is: " + newPassword, user.getEmail());
      } catch (MessagingException | NoSuchElementException e) {
        e.printStackTrace();
      }
    }

    ctx.status(401).result("Unauthorized - Wrong setPassword");
  };

  public static Handler updateUser = ctx -> {
    BufferedImage bufferedImage;
    String usernameAdmin, passwordAdmin, username, password,
      firstName, lastName, email, status, website;
    JSONArray phoneNumbers, playgroundIDs;

    try {
      String usermodel = ctx.formParam(("usermodel"));
      JSONObject jsonObject = new JSONObject(usermodel);
      usernameAdmin = jsonObject.getString(USERNAME_ADMIN);
      passwordAdmin = jsonObject.getString(PASSWORD_ADMIN);
      username = jsonObject.getString(USERNAME);
      password = jsonObject.getString(PASSWORD);
      firstName = jsonObject.getString(FIRSTNAME);
      lastName = jsonObject.getString(LASTNAME);
      email = jsonObject.getString(EMAIL);
      status = jsonObject.getString(STATUS);
      website = jsonObject.getString(WEBSITE);
      playgroundIDs = jsonObject.getJSONArray(PLAYGROUNDSIDS);
      phoneNumbers = jsonObject.getJSONArray(PHONENUMBERS);

      if (username.length() < 1 || password.length() < 1)
        throw new DALException("Missing username or setPassword");
    } catch (Exception e) {
      e.printStackTrace();
      ctx.status(400);
      ctx.result("Bad Request - Error in user data");
      return;
    }

    UserDTO userToUpdate = null;

    //Hvis user ikke opdaterer sig selv, er det en admin der opdaterer
    if (!username.equalsIgnoreCase(usernameAdmin)) {
      boolean adminAuthorized = Shared.checkAdminCredentials(usernameAdmin, passwordAdmin, ctx);
      if (!adminAuthorized) {
        return;
      }
    }

    try {
      userToUpdate = Controller.getInstance().getUser(username);
    } catch (NoSuchElementException e) {
      ctx.status(401);
      ctx.result("Unauthorized - Username doesn't exist");
    }
    userToUpdate.setFirstname(firstName);
    userToUpdate.setLastname(lastName);
    userToUpdate.setStatus(status);
    userToUpdate.setEmail(email);
    userToUpdate.setWebsite(website);
    userToUpdate.setImagePath(String.format(IMAGEPATH + "/%s/profile-picture", username));

    String[] usersNewPhoneNumbers = new String[phoneNumbers.length()];
    for (int i = 0; i < phoneNumbers.length(); i++) {
      try {
        usersNewPhoneNumbers[i] = (String) phoneNumbers.get(i);
      } catch (ClassCastException e) {
      }
    }
    userToUpdate.setPhoneNumbers(usersNewPhoneNumbers);

    Set<String> usersOldPGIds = userToUpdate.getPlaygroundsIDs();
    System.out.println("Old pgs " + usersOldPGIds);

    Set<String> usersNewPGIds = new HashSet<>();
    for (int i = 0; i < playgroundIDs.length(); i++) {
      try {
        usersNewPGIds.add((String) playgroundIDs.get(i));
      } catch (ClassCastException e) {
      }
    }
    userToUpdate.setPlaygroundsIDs(usersNewPGIds);

    if (usersOldPGIds != null || usersOldPGIds.size() > 0) {
      for (String oldPlaygroundName : usersOldPGIds) {
        if (!usersNewPGIds.contains(oldPlaygroundName)) {
          Controller.getInstance().removePedagogueFromPlayground(oldPlaygroundName, userToUpdate.getUsername());
        }
      }
    }

           /* for (String oldPlaygroundName : usersOldPGIds) {
                boolean match = false;
                for (String newPlaygroundName : usersNewPGIds) {
                    if (oldPlaygroundName.equalsIgnoreCase(newPlaygroundName)) {
                        match = true;
                    }
                    if (!match) {
                        Controller.getInstance().removePedagogueFromPlayground(oldPlaygroundName, userToUpdate.getUsername());
                    }
                }
            }*/

    try {
      bufferedImage = ImageIO.read(ctx.uploadedFile("image").getContent());
      Shared.saveProfilePicture(username, bufferedImage);
    } catch (
      Exception e) {
      System.out.println("Server: No image in upload");
    }
    if (Controller.getInstance().updateUser(userToUpdate).wasAcknowledged()) {
      ctx.status(201);
      ctx.result("User updated");
      ctx.json(userToUpdate);

      //Tilføj brugeren til de playgrounds han er tilknyttet
      //Controller.getInstance().addPedagogueToPlayground(userToUpdate);

    } else {
      ctx.status(500);
      ctx.result("User not updated");
    }
  };


}
