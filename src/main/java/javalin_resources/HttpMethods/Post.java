package javalin_resources.HttpMethods;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import com.mongodb.WriteResult;
import database.DALException;
import database.collections.*;
import database.dao.Controller;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;

public class Post implements Tag {

    public static class PostPlayground {

        public static Handler createPlaygroundPost = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            Set<User> users = new HashSet<>();
            for (int i = 0; i < jsonObject.getJSONArray(USERS).length(); i++) {
                String userId = (String) jsonObject.getJSONArray(USERS).get(i);
                try {
                    users.add(Controller.getInstance().getUser(userId));
                } catch (DALException e) {
                    e.printStackTrace();
                }
            }

            Playground playground = new Playground.Builder(jsonObject.getString(PLAYGROUND_NAME))
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

    public static class PostEvent {

        public static Handler createPlaygroundEventPost = ctx -> {
            JSONObject jsonObject = new JSONObject(ctx.body());
            Event event = new Event();

            Set<User> users = new HashSet<>();
            for (int i = 0; i < jsonObject.getJSONArray(USERS).length(); i++) {
                String userid = jsonObject.getJSONArray(USERS).getString(i);
                users.add(Controller.getInstance().getUser(userid));
            }

            Details details = new Details();
            Calendar cal = Calendar.getInstance();

            cal.set(Calendar.YEAR, jsonObject.getInt(EVENT_YEAR));
            cal.set(Calendar.DAY_OF_MONTH, jsonObject.getInt(EVENT_DAY));
            cal.set(Calendar.MONTH, jsonObject.getInt(EVENT_MONTH));

            details.setDate(cal.getTime());

            cal.set(Calendar.HOUR, jsonObject.getInt(EVENT_HOUR_START));
            cal.set(Calendar.MINUTE, jsonObject.getInt(EVENT_MINUTE_START));

            details.setStartTime(cal.getTime());

            cal.set(Calendar.HOUR, jsonObject.getInt(EVENT_HOUR_END));
            cal.set(Calendar.MINUTE, jsonObject.getInt(EVENT_MINUTE_END));

            details.setStartTime(cal.getTime());

            event.setPlayground(jsonObject.getString(PLAYGROUND_NAME));
            event.setName(jsonObject.getString(EVENT_NAME));
            event.setParticipants(jsonObject.getInt(EVENT_PARTICIPANTS));
            event.setImagepath(jsonObject.getString(EVENT_IMAGEPATH));
            event.setAssignedUsers(users);
            event.setDetails(details);
            event.setDescription(jsonObject.getString(EVENT_DESCRIPTION));

            if (Controller.getInstance().addPlaygroundEvent(jsonObject.getString(PLAYGROUND_NAME), event).wasAcknowledged()) {
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
            Boolean successful = Controller.getInstance().addUserToPlaygroundEvent(id, username);
            if (successful) {
                ctx.status(200).result("Update successful");
                ctx.json(new User.Builder(username));
                return;
            } else {
                ctx.status(404).result("Failed to update");
                ctx.json(new User.Builder(username));
            }

        };

    }

    public static class PostMessage {

        public static Handler createPlaygroundMessagePost = ctx -> {

            JSONObject jsonObject = new JSONObject(ctx.body());

            // TODO: Details
            Details details = new Details();
            Calendar cal = Calendar.getInstance();

            cal.set(Calendar.YEAR, jsonObject.getInt(EVENT_YEAR));
            cal.set(Calendar.DAY_OF_MONTH, jsonObject.getInt(EVENT_DAY));
            cal.set(Calendar.MONTH, jsonObject.getInt(EVENT_MONTH));

            cal.set(Calendar.HOUR, jsonObject.getInt(EVENT_HOUR_START));
            cal.set(Calendar.MINUTE, jsonObject.getInt(EVENT_MINUTE_START));

            Date date = cal.getTime();

            Message message = new Message.Builder()
                    .setMessageString(jsonObject.getString(MESSAGE_STRING))
                    .set_id(jsonObject.getString(MESSAGE_ID))
                    .setIcon(jsonObject.getString(MESSAGE_ICON))
                    .setCategory(jsonObject.getString(MESSAGE_CATEGORY))
                    .setPlaygroundID(jsonObject.getString(PLAYGROUND_ID))
                    .setWrittenByID(jsonObject.getString(MESSAGE_WRITTENBY_ID))
                    .setDate(date)
                    .build();


            if (Controller.getInstance().addPlaygroundMessage(jsonObject.getString(PLAYGROUND_ID), message).wasAcknowledged()) {
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

    public static class PostUser {
        public static Handler createParticipantsToPlaygroundEventPost = ctx -> {

        };

        public static Handler createUserToPlaygroundPost = ctx -> {
            boolean successful = Controller.getInstance().addPedagogueToPlayground(ctx.pathParam("name"), ctx.pathParam("username"));
            if (successful) {
                ctx.status(200).result("Update successful");
            } else {
                ctx.status(404).result("Failed to update");
            }
        };



        /*JBCrypt.
BCrypt.hashpw(password, BCrypt.gensalt());
 - Returnerer et saltet hash (med kendt salt)
BCrypt.checkpw(candidate, hashed);
- Returnerer true, hvis kodeordet matcher
HUSK: Kodeord må aldrig opbevares i clear-text!!!!
- Skal saltes og hashes med det samme og originalen glemmes!
- Saltet Hash gemmes i db.*/

        public static Handler createUser = ctx -> {
            BufferedImage bufferedImage;
            String usermodel = ctx.formParam(("usermodel"));
            System.out.println(usermodel);
            JSONObject jsonObject = new JSONObject(usermodel);
            String usernameAdmin = jsonObject.getString(USERNAME_ADMIN);
            String passwordAdmin = jsonObject.getString(PASSWORD_ADMIN);
            String username = jsonObject.getString(USERNAME);
            String password = jsonObject.getString(PASSWORD);
            String firstName = jsonObject.getString(FIRSTNAME);
            String lastName = jsonObject.getString(LASTNAME);
            String email = jsonObject.getString(EMAIL);
            String status = jsonObject.getString(STATUS);

            JSONArray playgroundIDs = null;
            //if pedg
            try {
                playgroundIDs = jsonObject.getJSONArray(PLAYGROUNDSIDS);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            JSONArray phoneNumber = jsonObject.getJSONArray(PHONENUMBER);
            String website = jsonObject.getString(WEBSITE);
            User admin = null;
            User newUser;

            try {
                admin = Controller.getInstance().getUser(usernameAdmin);
            } catch (DALException e) {
                e.printStackTrace();
                ctx.status(411).result("Unauthorized - Forkert brugernavn eller adgangskode...");
            }
            if (admin.getPassword().equalsIgnoreCase(passwordAdmin)) {

                newUser = new User.Builder(username)
                        .build();
                newUser.setFirstname(firstName);
                newUser.setLastname(lastName);
                newUser.setStatus(status);

                String hashPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                newUser.setPassword(hashPassword);

                newUser.setEmail(email);
                newUser.setWebsite(website);
                String[] phoneNumbers = new String[1];
                phoneNumbers[0] = phoneNumber.getString(0);
                newUser.setPhonenumbers(phoneNumbers);
                newUser.setImagePath(String.format(IMAGEPATH + "/%s/profile-picture", username));

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
                    ctx.json(Controller.getInstance().getUsers()).status(201).result("User created.");
                } else {
                    ctx.status(401).result("User was not created");
                    //Controller.getInstance().getUsers();
                }
                // Hvis admin har skrevet forkert adgangskode
            } else {
                ctx.status(401).result("Unauthorized");
            }
            //  ctx.json(Controller.getInstance().getUsers());
        };

        public static Handler userLogin = ctx -> {
            Brugeradmin ba = null;
            JSONObject jsonObject = new JSONObject(ctx.body());
            String username = jsonObject.getString(USERNAME);
            String password = jsonObject.getString(PASSWORD);
            User user = null;

            //Er det root admin der logger ind?
            if (username.equalsIgnoreCase("root")) {
                try {
                    user = Controller.getInstance().getUser(username);

                } catch (DALException e) {
                    //e.printStackTrace();
                    System.out.println("Opretter root");
                    user = new User.Builder("root")
                            .status("admin")
                            .password("root")
                            .setFirstname("Københavns")
                            .setLastname("Kommune")
                            .build();
                    Controller.getInstance().createUser(user);
                }
                ctx.json(user).contentType("json");
                return;
            }

            //Findes brugeren i Brugeradminmodulet?
            try {
                ba = (Brugeradmin) Naming.lookup(Brugeradmin.URL);
            } catch (NotBoundException | MalformedURLException | RemoteException e) {
                e.printStackTrace();
            }
            Bruger bruger;
            try {
                bruger = ba.hentBruger(username, password);
                if (bruger != null) {
                    //Findes han i brugeradmin så se om han også findes i databasen
                    //hvis ikke oprettes han der, med data fra brugeradminmodulet
                    findUserInDB(bruger);
                }
            } catch (Exception e) {
                System.out.println("Server: User is not registered in Brugeradminmodule");
            }
            //Findes brugeren ikke i brugeradminmodulet, findes han så i databasen?
            try {
                user = Controller.getInstance().getUser(username);
                if (BCrypt.checkpw(password, user.getPassword())) {
                    ctx.json(user).contentType("json").status(200);
                } else {
                    ctx.status(401).result("Unauthorized - Wrong password");
                }
            } catch (DALException e) {
                System.out.println("Server: Username doesn't exist.");
                ctx.status(401).json("Unauthorized - No such username!");
            }
        };

        // Metoden opretter brugeren i databasen, hvis han ikke allerede findes.
        private static User findUserInDB(Bruger bruger) {
            User user = null;
            try {
                user = Controller.getInstance().getUser(bruger.brugernavn);
            } catch (DALException e) {
                e.printStackTrace();
            }

            if (user == null) {
                System.out.println("Bruger findes ikke i databasen. \nBruger oprettes i databasen");
                user = new User.Builder(bruger.brugernavn)
                        .setFirstname(bruger.fornavn)
                        .setLastname(bruger.efternavn)
                        .email(bruger.email)
                        .password(bruger.adgangskode)
                        .status(STATUS_PEDAGOG)
                        .setWebsite(bruger.ekstraFelter.get("webside").toString())
                        .setImagePath(String.format(IMAGEPATH + "/%s/profile-picture", bruger.brugernavn))
                        .build();
                Controller.getInstance().createUser(user);
            }
            //Brugeren har ikke selv logget ind før og skal derfor ikke oprettes i DB men opdateres
            if (!user.isLoggedIn()) {
                user.setFirstname(bruger.fornavn);
                user.setLastname(bruger.efternavn);
                user.setEmail(bruger.email);
                user.setPassword(bruger.adgangskode);
                user.setStatus(user.getStatus());
                user.setWebsite(bruger.ekstraFelter.get("webside").toString());
                user.setLoggedIn(true);
                user.setImagePath(String.format(IMAGEPATH + "/%s/profile-picture", bruger.brugernavn));
                Controller.getInstance().updateUser(user);
            }
            return user;
        }
    }
}
