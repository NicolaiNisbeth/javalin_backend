package javalin_resources.http_methods;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import com.google.gson.JsonObject;
import com.google.gson.internal.$Gson$Preconditions;
import com.mongodb.WriteResult;
import database.Controller;
import database.dto.UserDTO;
import database.exceptions.DALException;
import database.exceptions.NoModificationException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.*;
import io.swagger.v3.core.util.Json;
import org.eclipse.jetty.http.HttpStatus;
import org.json.*;
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
import java.util.List;


public class User implements Tag {

    @OpenApi(
            summary = "Delete one user",
            path = "/rest/users",
            tags = {"User"},
            composedRequestBody = @OpenApiComposedRequestBody(required = true,
                    description = "credentials of the admin and username of the user to be deleted")

    )
    public static Handler deleteUser = ctx -> {
        JSONObject jsonObject, deleteUserModel;
        jsonObject = new JSONObject(ctx.body());
        deleteUserModel = jsonObject.getJSONObject("deleteUserModel");
        String usernameAdmin = deleteUserModel.getString(USERNAME_ADMIN);
        String passwordAdmin = deleteUserModel.getString(PASSWORD_ADMIN);
        String username = deleteUserModel.getString(USERNAME);

        boolean adminAuthorized = Shared.checkAdminCredentials(usernameAdmin, passwordAdmin, ctx);
        if (!adminAuthorized) {
            return;
        }

        Controller.getInstance().deleteUser(username);
        ctx.status(200);
        ctx.json("OK - User deleted");
        ctx.contentType("json");
    };

    @OpenApi(
            summary = "Get one users profile picture",
            path = "/rest/users",
            tags = {"User"}
    )
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
            //System.out.println("Server: User have no profile picture...");
        }

        if (targetStream != null) {
            ctx.result(targetStream).contentType("image/png");
        } else {
            //System.out.println("Server: Returning random user picture...");
            targetStream = User.class.getResourceAsStream("/images/profile_pictures/random_user.png");
            ctx.result(targetStream).contentType("image/png");
        }
    };

    @OpenApi(
            summary = "Get all users",
            operationId = "getAllUsers",
            path = "/rest/users",
            method = HttpMethod.GET,
            tags = {"User"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = UserDTO[].class)})
            }
    )
    public static Handler getAllUsers = ctx -> {
        ctx.json(Controller.getInstance().getUsers()).contentType("json");
    };

    @OpenApi(
            summary = "Get all users",
            operationId = "getAllUsers",
            path = "/rest/users/all-employees",
            method = HttpMethod.GET,
            tags = {"User"},
            responses = {
                    @OpenApiResponse(status = "200", content = {@OpenApiContent(from = UserDTO[].class)})
            }
    )
    public static Handler getAllEmployees = ctx -> {
        System.out.println("er inde");
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

    @OpenApi(
            summary = "Create user",
            path = "/rest/employee/create",
            tags = {"User"},
            formParams = {@OpenApiFormParam(name = "usermodel", type = JSONString.class, required = true)},
            description = "usermodel containing credentials of the admin and the user the data of the user to be created",
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = User.class)})
            }
    )
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
    @OpenApi(
            summary = "Logs user into the system",
            path = javalin_resources.Util.Path.User.USERS_LOGIN,
            tags = {"User"},
            method = HttpMethod.POST,
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = UserDTO.class, type = ContentType.JSON),
                    required = true,
                    description = "Insert your password and username instead of \"string\""
            ),
            responses = {
                    @OpenApiResponse(status = "200", description = "Successful login returns user object"),
                    @OpenApiResponse(status = "400", description = "Body has no username or password"),
                    @OpenApiResponse(status = "401", description = "Invalid username or password supplied"),
                    @OpenApiResponse(status = "404", description = "User not found"),
                    @OpenApiResponse(status = "500", description = "Server error")
            }
    )
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

    @OpenApi(
            summary = "Update one user",
            path = "/rest/users",
            tags = {"User"},
            formParams = {@OpenApiFormParam(name = "usermodel", type = JSONString.class, required = true)},
            description = "usermodel containing credentials of the admin and the user the be updated with relevant fields",
            responses = {
                    @OpenApiResponse(status = "201", content = {@OpenApiContent(from = User[].class)})
            }
    )
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
        } catch (JSONException | NullPointerException e){
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
            playgroundIDs = jsonObject.getJSONArray(PLAYGROUNDSIDS);
            boolean isAdminUpdatingUser = !username.equalsIgnoreCase(usernameAdmin);
            boolean isAdminAuthorized = Shared.checkAdminCredentials(usernameAdmin, passwordAdmin, ctx);
            if (isAdminUpdatingUser && !isAdminAuthorized) {
                ctx.status(HttpStatus.UNAUTHORIZED_401);
                ctx.json(String.format("Unauthorized - User %s has no privileges to update user %s", usernameAdmin, username));
                ctx.contentType(ContentType.JSON);
                return;
            }
        } catch (JSONException e){
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
        userToUpdate.setImagePath(String.format(IMAGEPATH + "/%s/profile-picture", username));
        String[] usersNewPhoneNumbers = new String[phoneNumbers.length()];
        for (int i = 0; i < phoneNumbers.length(); i++) {
            try { usersNewPhoneNumbers[i] = (String) phoneNumbers.get(i); } catch (ClassCastException e) {}
        }
        userToUpdate.setPhoneNumbers(usersNewPhoneNumbers);
        try {
            BufferedImage bufferedImage = ImageIO.read(ctx.uploadedFile("image").getContent());
            Shared.saveProfilePicture(username, bufferedImage);
        } catch (Exception e) { System.out.println("Server: No image in upload"); }

        // check if non-trivial data can be updated
        if (privileges){
            // remove references to old playgrounds
            Set<String> usersOldPGIds = userToUpdate.getPlaygroundsIDs();
            System.out.println("Old pgs " + usersOldPGIds);
            if (usersOldPGIds != null && usersOldPGIds.size() > 0) {
                for (String oldPlaygroundName : usersOldPGIds) {
                    Controller.getInstance().removePedagogueFromPlayground(oldPlaygroundName, userToUpdate.getUsername());
                }
            }
            // add references to new playgrounds
            Set<String> usersNewPGIds = new HashSet<>();
            for (int i = 0; i < playgroundIDs.length(); i++) {
                try { usersNewPGIds.add((String) playgroundIDs.get(i)); } catch (ClassCastException e) {}
            }
            userToUpdate.setPlaygroundsIDs(usersNewPGIds);
            userToUpdate.setStatus(status);
        }

        try {
            Controller.getInstance().updateUser(userToUpdate);
            ctx.status(HttpStatus.OK_200);
            ctx.result("OK - user was updated successfully");
            ctx.json(userToUpdate);
            ctx.contentType(ContentType.JSON);
        } catch (NoModificationException e){
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            ctx.result("Server error - Update user failed");
            ctx.contentType(ContentType.JSON);
        }
    };


    // TODO: NOT IMPLEMENTED
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
                Shared.sendMail("Your new setPassword", "Your new setPassword is: " + newPassword, user.getEmail());
            } catch (MessagingException | NoSuchElementException e) {
                e.printStackTrace();
            }
        }

        ctx.status(401).result("Unauthorized - Wrong setPassword");
    };
}
