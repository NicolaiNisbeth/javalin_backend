package javalin_resources.HttpMethods;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import com.mongodb.WriteResult;
import database.exceptions.DALException;
import database.exceptions.NoModificationException;
import database.dto.*;
import database.Controller;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.ContentType;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.rmi.Naming;
import java.util.*;

public class Post implements Tag {

    public static class Playground {

        public static Handler createPlaygroundPost = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            Set<UserDTO> users = new HashSet<>();
            for (int i = 0; i < jsonObject.getJSONArray(USERS).length(); i++) {
                String userId = (String) jsonObject.getJSONArray(USERS).get(i);
                try {
                    users.add(Controller.getInstance().getUser(userId));
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                }
            }

            PlaygroundDTO playground = new PlaygroundDTO.Builder(jsonObject.getString(PLAYGROUND_NAME))
                    .setStreetName(jsonObject.getString(PLAYGROUND_STREET_NAME))
                    .setStreetNumber(jsonObject.getInt(PLAYGROUND_STREET_NUMBER))
                    .setZipCode(jsonObject.getInt(PLAYGROUND_ZIPCODE))
                    .setCommune(jsonObject.getString(PLAYGROUND_COMMUNE))
                    .setToiletPossibilities(jsonObject.getBoolean(PLAYGROUND_TOILET_POSSIBILITIES))
                    .setHasSoccerField(jsonObject.getBoolean(PLAYGROUND_HASSOCCERFIELD))
                    .setAssignedPedagogue(users)
                    .build();

            WriteResult ws = Controller.getInstance().createPlayground(playground);
            if (ws.wasAcknowledged()) {
                ctx.status(200).result("Playground was created");
            } else {
                ctx.status(401).result("Playground was not created");
            }
        };

    }

    public static class Event {

        public static Handler createPlaygroundEventPost = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            EventDTO event = new EventDTO();

            Set<UserDTO> users = new HashSet<>();
            for (int i = 0; i < jsonObject.getJSONArray(USERS).length(); i++) {
                String userid = jsonObject.getJSONArray(USERS).getString(i);
                users.add(Controller.getInstance().getUser(userid));
            }

            DetailsDTO detailsModel = new DetailsDTO();
            Calendar cal = Calendar.getInstance();

            cal.set(Calendar.YEAR, jsonObject.getInt(EVENT_YEAR));
            cal.set(Calendar.DAY_OF_MONTH, jsonObject.getInt(EVENT_DAY));
            cal.set(Calendar.MONTH, jsonObject.getInt(EVENT_MONTH));

            detailsModel.setDate(cal.getTime());

            cal.set(Calendar.HOUR, jsonObject.getInt(EVENT_HOUR_START));
            cal.set(Calendar.MINUTE, jsonObject.getInt(EVENT_MINUTE_START));

            detailsModel.setStartTime(cal.getTime());

            cal.set(Calendar.HOUR, jsonObject.getInt(EVENT_HOUR_END));
            cal.set(Calendar.MINUTE, jsonObject.getInt(EVENT_MINUTE_END));

            detailsModel.setStartTime(cal.getTime());

            event.setPlayground(jsonObject.getString(PLAYGROUND_NAME));
            event.setName(jsonObject.getString(EVENT_NAME));
            event.setParticipants(jsonObject.getInt(EVENT_PARTICIPANTS));
            event.setImagepath(jsonObject.getString(EVENT_IMAGEPATH));
            event.setAssignedUsers(users);
            event.setDetailsModel(detailsModel);
            event.setDescription(jsonObject.getString(EVENT_DESCRIPTION));

            if (Controller.getInstance().createPlaygroundEvent(jsonObject.getString(PLAYGROUND_NAME), event).wasAcknowledged()) {
                ctx.status(200).result("Event Created");
                System.out.println("inserted event");
            } else {
                ctx.status(404);
                System.out.println("Event not created");
            }
        };

        public static Handler createUserToPlaygroundEventPost = ctx -> {
            String id = ctx.pathParam("id");
            String username = ctx.pathParam("username");
            WriteResult successful = Controller.getInstance().addUserToEvent(id, username);
            if (true) {
                ctx.status(200).result("Update successful");
                ctx.json(new UserDTO.Builder(username));
                return;
            } else {
                ctx.status(404).result("Failed to update");
                ctx.json(new UserDTO.Builder(username));
            }

        };

    }

    public static class Message {

        public static Handler createPlaygroundMessagePost = ctx -> {

            JSONObject jsonObject = new JSONObject(ctx.body());

            // TODO: Details
            DetailsDTO detailsModel = new DetailsDTO();
            Calendar cal = Calendar.getInstance();

            cal.set(Calendar.YEAR, jsonObject.getInt(EVENT_YEAR));
            cal.set(Calendar.DAY_OF_MONTH, jsonObject.getInt(EVENT_DAY));
            cal.set(Calendar.MONTH, jsonObject.getInt(EVENT_MONTH));

            cal.set(Calendar.HOUR, jsonObject.getInt(EVENT_HOUR_START));
            cal.set(Calendar.MINUTE, jsonObject.getInt(EVENT_MINUTE_START));

            Date date = cal.getTime();

            MessageDTO message = new MessageDTO.Builder()
                    .setMessageString(jsonObject.getString(MESSAGE_STRING))
                    .set_id(jsonObject.getString(MESSAGE_ID))
                    .setIcon(jsonObject.getString(MESSAGE_ICON))
                    .setCategory(jsonObject.getString(MESSAGE_CATEGORY))
                    .setPlaygroundID(jsonObject.getString(PLAYGROUND_ID))
                    .setWrittenByID(jsonObject.getString(MESSAGE_WRITTENBY_ID))
                    .setDate(date)
                    .build();


            if (Controller.getInstance().createPlaygroundMessage(jsonObject.getString(PLAYGROUND_ID), message).wasAcknowledged()) {
                ctx.status(200).result("Message posted");
            } else {
                ctx.status(404).result("Failed to post message");
            }
        };

    }

   /* lad os holde os til employee eller user
    public static class PostPedagogue {

        public static Handler createPedagogueToPlaygroundPost = ctx -> {


        };
    }*/

    public static class User {
        public static Handler createParticipantsToPlaygroundEventPost = ctx -> {

        };

        public static Handler createUserToPlaygroundPost = ctx -> {
            WriteResult successful = Controller.getInstance().addPedagogueToPlayground(ctx.pathParam("name"), ctx.pathParam("username"));
            if (true) {
                ctx.status(200).result("Update successful");
            } else {
                ctx.status(404).result("Failed to update");
            }
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
                ctx.result("User created.");
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
                ctx.contentType(ContentType.JSON);
                ctx.result("Body has no username or password");
                return;
            }

            UserDTO fetchedUser;
            boolean root = username.equalsIgnoreCase("root");
            if (root) {
                try {
                    fetchedUser = getOrCreateRootUser(username);
                    ctx.status(HttpStatus.OK_200);
                    ctx.result("User login with root was successful");
                    ctx.json(fetchedUser);
                    ctx.contentType(ContentType.JSON);
                    return;
                } catch (Exception e){
                    ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                    ctx.contentType(ContentType.JSON);
                    ctx.result("Creating root user failed");
                    return;
                }
            }

            Bruger bruger = getUserInBrugerAuthorization(username, password);
            try {
                fetchedUser = Controller.getInstance().getUser(username);
            } catch (NoSuchElementException | IllegalArgumentException e) {
                fetchedUser = null;
            } catch (Exception e){
                // if database is down - don't allow login even if user is valid
                // in bruger authorization module
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
                ctx.contentType(ContentType.JSON);
                ctx.result("Couldn't connect to database");
                return;
            }

            // user was not found in user authorization and database
            if (bruger == null && fetchedUser == null) {
                ctx.status(HttpStatus.NOT_FOUND_404);
                ctx.contentType(ContentType.JSON);
                ctx.result("Wrong username or password");
                return;
            }

            // if user exists in nordfalk but not in db
            if (fetchedUser == null) {
                fetchedUser = new UserDTO.Builder(bruger.brugernavn)
                        .setFirstname(bruger.fornavn)
                        .setLastname(bruger.efternavn)
                        .setEmail(bruger.email)
                        .setPassword(bruger.adgangskode)
                        .status(STATUS_PEDAGOG)
                        .setWebsite(bruger.ekstraFelter.get("webside").toString())
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
                ctx.result("user login was successful");
                ctx.json(fetchedUser);
                ctx.contentType(ContentType.JSON);
            } else {
                ctx.status(HttpStatus.UNAUTHORIZED_401);
                ctx.contentType(ContentType.JSON);
                ctx.result("wrong password");

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

    }

}
