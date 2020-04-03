package javalin_resources;

import database.collections.Details;
import database.collections.Event;
import database.collections.User;
import database.dao.Controller;
import javalin_resources.Util.ViewUtil;
import io.javalin.http.Handler;
import org.json.JSONObject;

import java.util.*;

public class EventRessource {
    static final String EVENT_ID = "id";
    static final String PLAYGROUND_NAME = "name";
    static final String USER_NAME = "username";

    public static Handler OneEventHandlerGet = ctx -> {
        Event event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
        if (event != null) {
            ctx.json(event).contentType("json");
            ctx.status(200);
        } else
            ctx.status(404).result("Couldn't find an event");
    };

    public static Handler OneEventParticipantsHandlerGet = ctx -> {
        Event event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
        if (event != null) {
            ctx.json(event.getParticipants()).contentType("json");
            ctx.status(200);
        } else
            ctx.status(404);
    };

    public static Handler OneEventOneParticipantHandlerGet = ctx -> {
        Event event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
        for (User user : event.getAssignedUsers())
            if (user.getUsername().equals(ctx.pathParam(USER_NAME))) {
                ctx.json(user).contentType("json");
                ctx.status(200);
                return;
            } else
                ctx.status(404);
    };


    public static Handler PlayGroundAllEventsHandlerGet = ctx -> {
        List<Event> events = Controller.getInstance().getPlaygroundEvents(ctx.pathParam(PLAYGROUND_NAME));
        if (events != null) {
            ctx.json(events).contentType("json");
            ctx.status(200);
        } else {
            ctx.status(404);
        }
    };

    public static Handler PlayGroundUpdateEventHandlerPut = ctx -> {
        JSONObject jsonObject = new JSONObject(ctx.body());
        Event event = Controller.getInstance().getEvent(ctx.queryParam("id"));

        if (event != null) {
            ctx.status(200);

            if (jsonObject.has("description")) {
                event.setDescription(jsonObject.getString("description"));
// TODO Hvordan kommer den detail parameter til at foregå?
                if (jsonObject.has("hour")) {
                    Details details = new Details();
                    Calendar cal = Calendar.getInstance();

                    cal.set(Calendar.YEAR, jsonObject.getInt("year"));
                    cal.set(Calendar.DAY_OF_MONTH, jsonObject.getInt("day"));
                    cal.set(Calendar.MONTH, jsonObject.getInt("month"));

                    details.setDate(cal.getTime());

                    cal.set(Calendar.HOUR, jsonObject.getInt("hourstart"));
                    cal.set(Calendar.MINUTE, jsonObject.getInt("minutestart"));

                    details.setStartTime(cal.getTime());

                    cal.set(Calendar.HOUR, jsonObject.getInt("hourend"));
                    cal.set(Calendar.MINUTE, jsonObject.getInt("minuteend"));

                    details.setStartTime(cal.getTime());

                    event.setDetails(details);
                }
            }
            if (jsonObject.has("assignedusers")) {
                Set<User> users = new HashSet<>();
//TODO don't have the slightest idea if this will work. - Gustav
                for (int i = 0; i < jsonObject.getJSONArray("user").length(); i++) {
                    String username = jsonObject.getJSONArray("assignedusers").getJSONObject(i).getString("username");
                    users.add(Controller.getInstance().getUser(username));
                }
                event.setAssignedUsers(users);
            }
            if (jsonObject.has("imagepath"))
                event.setImagepath(jsonObject.getString("imagepath"));

            if (jsonObject.has("participants"))
                event.setParticipants(jsonObject.getInt("participants"));

            if (jsonObject.has("name"))
                event.setName(jsonObject.getString("name"));


            if (jsonObject.has("playgroundname"))
                event.setPlayground(jsonObject.getString("playgroundname"));

            Controller.getInstance().updatePlaygroundEvent(event);
        } else {
            ctx.status(404);
        }
    };

    public static Handler removeUserFromPlaygroundEventPut = ctx -> {
        JSONObject jsonObject = new JSONObject(ctx.body());
        if (Controller.getInstance().removeUserFromPlaygroundEvent(jsonObject.getString("eventID"), jsonObject.getString("userID")))
            ctx.status(200);
        else
            ctx.status(404);
    };
}
