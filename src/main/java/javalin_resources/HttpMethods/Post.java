package javalin_resources.HttpMethods;

import com.mongodb.WriteResult;
import database.DALException;
import database.collections.*;
import database.dao.Controller;
import io.javalin.http.Handler;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Post {

    private static final String EVENT_ID = "id";
    private static final String EVENT_NAME = "name";
    private static final String EVENT_DESCRIPTION = "description";
    private static final String EVENT_YEAR = "year";
    private static final String EVENT_MONTH = "month";
    private static final String EVENT_DAY = "day";
    private static final String EVENT_HOUR = "hour";
    private static final String EVENT_HOUR_START = "hourstart";
    private static final String EVENT_HOUR_END = "hourend";
    private static final String EVENT_MINUTE_START = "minutestart";
    private static final String EVENT_MINUTE_END = "minuteend";
    private static final String EVENT_ASSIGNED_USERS = "assignedusers";
    private static final String EVENT_IMAGEPATH = "imagepath";
    private static final String EVENT_PARTICIPANTS = "participants";
    private static final String EVENT_DETAILS = "details";

    private static final String MESSAGE_ID = "id";
    private static final String MESSAGE_CATEGORY = "category";
    private static final String MESSAGE_ICON = "icon";
    private static final String MESSAGE_STRING = "messageString";
    private static final String MESSAGE_OUTDATED = "outDated";
    private static final String MESSAGE_WRITTENBY_ID = "writtenByID";

    private static final String USER_ID = "id";
    private static final String USER = "user";
    private static final String USERS = "users";
    private static final String USER_NAME = "username";

    private static final String PEDAGOGUE = "pedagogue";

    private static final String PLAYGROUND_ID = "id";
    private static final String PLAYGROUND_NAME = "name";
    private static final String PLAYGROUND_STREET_NAME = "streetname";
    private static final String PLAYGROUND_STREET_NUMBER = "streetnumber";
    private static final String PLAYGROUND_ZIPCODE = "zipcode";
    private static final String PLAYGROUND_PEDAGOGUES = "pedagogues";
    private static final String PLAYGROUND_COMMUNE = "commune";
    private static final String PLAYGROUND_EVENTS = "events";
    private static final String PLAYGROUND_HASSOCCERFIELD = "hassoccerfield";
    private static final String PLAYGROUND_TOILETS = "toilets";
    private static final String PLAYGROUND_TOILET_POSSIBILITIES = "ToiletPossibilities";
    private static final String PLAYGROUND_IMAGEPATH = "imagepath";
    private static final String PLAYGROUND_MESSAGE_ID = "messageid";
    private static final String PLAYGROUND_MESSAGES = "messages";





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

        public static Handler addPlaygroundEventPost = ctx -> {
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

        public static Handler addPlaygroundMessagePost = ctx -> {

            JSONObject jsonObject = new JSONObject(ctx.body());

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


}
