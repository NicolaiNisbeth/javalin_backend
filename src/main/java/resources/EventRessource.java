package resources;

import com.google.gson.JsonObject;
import database.collections.Details;
import database.collections.Event;
import database.collections.Message;
import database.collections.User;
import database.dao.Controller;
import database.utils.ViewUtil;
import io.javalin.http.Handler;
import org.json.JSONObject;

import java.util.*;

public class EventRessource {

    public static Handler OneEventHandlerGet = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        // TODO er parameteren name?
        Event event = Controller.getInstance().getEvent(ctx.pathParam("name"));
        if (model != null) {
            model.get(event);
            ctx.status(200);
        } else
            ctx.status(404);
    };

    public static Handler PlayGroundAllEventsHandlerGet = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        List<Event> events = Controller.getInstance().getPlaygroundEvents(ctx.pathParam("playground"));
        if (events != null) {
            model.put("messages", events);
            ctx.status(200);
        } else {
            ctx.status(404);
        }
    };

    public static Handler PlayGroundUpdateEventHandlerGet = ctx -> {
        JSONObject jsonObject = new JSONObject(ctx.body());
        Event event = Controller.getInstance().getEvent(jsonObject.getString("id"));

        if (event != null) {
            ctx.status(200);

            if (jsonObject.get("description") != null) {
                event.setDescription(jsonObject.getString("description"));
// TODO Hvordan kommer den detail parameter til at foregå?
                if (jsonObject.get("hour") != null) {
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
                } }
                if (jsonObject.get("assignedusers") != null) {
                    Set<User> users = new HashSet<>();
//TODO don't have the slightest idea if this will work. - Gustav
                    for (int i = 0; i < jsonObject.getJSONArray("user").length(); i++) {
                        String username = jsonObject.getJSONArray("assignedusers").getJSONObject(i).getString("username");
                        users.add(Controller.getInstance().getUser(username));
                    }
                    event.setAssignedUsers(users);
                } if (jsonObject.get("imagepath") != null)
                    event.setImagepath(jsonObject.getString("imagepath"));

                if (jsonObject.get("participants") != null)
                    event.setParticipants(jsonObject.getInt("participants"));

                if (jsonObject.get("name") != null)
                event.setName(jsonObject.getString("name"));

                if (jsonObject.get("playgroundname") != null)
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
