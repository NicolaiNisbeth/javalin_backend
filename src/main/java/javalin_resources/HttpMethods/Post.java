package javalin_resources.HttpMethods;

import brugerautorisation.data.Bruger;
import brugerautorisation.transport.rmi.Brugeradmin;
import com.mongodb.WriteResult;
import database.DALException;
import database.collections.*;
import database.dao.Controller;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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


        public static User userLogin(Context ctx) {
            Brugeradmin ba = null;

            JSONObject jsonObject = new JSONObject(ctx.body());
            String username = jsonObject.getString(USERNAME);
            String password = jsonObject.getString(PASSWORD);
            try {
                ba = (Brugeradmin) Naming.lookup(Brugeradmin.URL);
            } catch (NotBoundException | MalformedURLException | RemoteException e) {
                e.printStackTrace();
            }
            Bruger bruger;
            try {
                bruger = ba.hentBruger(username, password);
                if (bruger != null) {
                    return findUserInDB(bruger);
                }
                //todo njl lav bedre
            } catch (Exception e) {
                if (username.equalsIgnoreCase("root")) {
                    User root = null;
                    try {
                        root = Controller.getInstance().getUser(username);
                    } catch (DALException e1) {
                        e1.printStackTrace();
                    }
                    if (root.getPassword().equalsIgnoreCase(password)) {
                        return root;
                    }
                }
            }
            ctx.status(401).result("Unauthorized");
            return null;
        }

        // Metoden opretter brugeren i databasen, hvis han ikke allerede findes.
        public static User findUserInDB(Bruger bruger) {
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
