package javalin_resources;


import com.mongodb.WriteResult;
import database.DALException;
import database.collections.*;
import database.dao.Controller;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import javalin_resources.Util.ViewUtil;
import org.json.JSONObject;

import java.util.*;

//@Path("/galgeleg")
public class PlaygroundResource {
    static final String PLAYGROUND_NAME = "name";
    static final String EMPLOYEE_USERNAME = "username";

    public static Handler AllPlaygroundsHandlerGet = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("playgrounds", Controller.getInstance().getPlaygrounds());
        Controller.getInstance().getPlaygrounds();
    };

    public static Handler OnePlaygroundGet = ctx -> {
        ctx.json(Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME))).contentType("json");
    };

    public static Handler OnePlaygroundAllEmployeeHandlerGet = ctx -> {
        ctx.json(Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME)).getAssignedPedagogue()).contentType("json");
    };

    public static Handler OnePlaygroundOneEmployeeHandlerGet = ctx -> {
        ctx.json(Controller.getInstance().getUser(ctx.pathParam(EMPLOYEE_USERNAME))).contentType("json");
    };

    public static Handler UpdatePlaygroundHandlerPut = ctx -> {
        Playground playground = Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME));
        JSONObject jsonObject = new JSONObject(ctx.body());
        if (playground != null) {
        if (jsonObject.getString("streetname") != null)
            playground.setStreetName(jsonObject.getString("streetname"));

            if (jsonObject.getJSONArray("pedagogues") != null) {
                Set<User> pedagoges = new HashSet<>();
//TODO don't have the slightest idea if this will work. - Gustav
                for (int i = 0; i < jsonObject.getJSONArray("pedagogues").length(); i++) {
                    String username = jsonObject.getJSONArray("pedagogues").getString(i);
                    pedagoges.add(Controller.getInstance().getUser(username));
                }
                playground.setAssignedPedagogue(pedagoges);
            }

            if (jsonObject.getString("commune") != null)
                playground.setCommune(jsonObject.getString("commune"));
/*
            if (jsonObject.get("events") != null) {
                Set<Event> eventSet = new HashSet<>();
                for (int i = 0; i < jsonObject.getJSONArray("events").length(); i++) {
                    String eventid = jsonObject.getJSONArray("events").getJSONObject(i).getString("events");
                    eventSet.add(Controller.getInstance().getEvent(eventid));
                }
                playground.setEvents(eventSet);
            }
 */
            if (jsonObject.get("soccerfield") != null)
                playground.setHasSoccerField(jsonObject.getBoolean("soccerfield"));

          //  if (jsonObject.get("id") != null)
          //      playground.setId(jsonObject.getString("id"));

            if (jsonObject.get("imagepath") != null)
                playground.setImagePath(jsonObject.getString("imagepath"));
/*
            if (jsonObject.getJSONArray("messages") != null) {
                Set<Message> messagesSet = new HashSet<>();
                for (int i = 0; i < jsonObject.getJSONArray("messageid").length(); i++) {
                    String messageid = jsonObject.getJSONArray("messageid").getJSONObject(i).getString("messageid");
                    messagesSet.add(Controller.getInstance().getMessage(messageid));
                }
                playground.setMessages(messagesSet);
            }
 */

            if (jsonObject.get("streetnumber") != null)
                playground.setStreetNumber(jsonObject.getInt("streetnumber"));

            if (jsonObject.get("toilets") != null)
                playground.setToiletPossibilities(jsonObject.getBoolean("toilets"));

            if (jsonObject.get("zipcode") != null)
                playground.setZipCode(jsonObject.getInt("zipcode"));

        if (Controller.getInstance().updatePlayground(playground)) {
        ctx.status(200);
            System.out.println("update playground with name " + playground.getName());
        }
        } else {
            ctx.status(404);
        }
    };

//TODO does this return null if it doesnt work out?
    public static Handler CreatePlaygroundHandlerPost = ctx -> {
        ctx.json(createPlayground(ctx.body(), ctx)).contentType("json");
    };

    public static Playground createPlayground(String request, Context ctx) {
        JSONObject jsonObject = new JSONObject(request);
        Set<User> users = new HashSet<>();
        for (int i = 0; i < jsonObject.getJSONArray("users").length(); i++) {
            String userid = (String) jsonObject.getJSONArray("users").get(i);
            try {
                users.add(Controller.getInstance().getUser(userid));
            } catch (DALException e) {
                e.printStackTrace();
            }
        }

        Playground playground = new Playground.Builder(jsonObject.getString("name"))
                .setStreetName(jsonObject.getString("streetname"))
                .setStreetNumber(jsonObject.getInt("streetnumber"))
                .setZipCode(jsonObject.getInt("zipcode"))
                .setCommune(jsonObject.getString("commune"))
                .setToiletPossibilities(jsonObject.getBoolean("ToiletPossibilities"))
                .setHasSoccerField(jsonObject.getBoolean("soccerfield"))
                .setAssignedPedagogue(users)
                .build();

        WriteResult ws = Controller.getInstance().createPlayground(playground);
        if (ws.wasAcknowledged()) {
            ctx.status(200).result("Playground was created");
        } else {
            ctx.status(401).result("Playground was not created");
        }
        return Controller.getInstance().getPlayground(jsonObject.getString("name"));
    }

    public static Handler DeleteOnePlaygroundDelete = ctx -> {
        String playgroundname = "";
        playgroundname = ctx.pathParam("name");

        if (playgroundname != "") {
      Controller.getInstance().deletePlayground(playgroundname);
      ctx.status(200);
            System.out.println("Deleted playground with name " + playgroundname);
        } else {
            ctx.status(404);
            System.out.println("Found no playground");
        }
    };

    public static Handler AddPedagogueToPlayGroundPut = ctx -> {
        JSONObject jsonObject = new JSONObject(ctx.body());
        Playground playground = Controller.getInstance().getPlayground(jsonObject.getString("playgroundname"));
        User user = Controller.getInstance().getUser(jsonObject.getString("pedagogue"));
        playground.getAssignedPedagogue().add(user);
        Controller.getInstance().updatePlayground(playground);
        if (jsonObject.getString("pedagogue") != null && jsonObject.getString("playgroundname") != null) {
            ctx.status(200);
        } else {
        ctx.status(404);
        }
    };

    public static Handler addUserToPlaygroundEventPut = ctx -> {
        JSONObject jsonObject = new JSONObject(ctx.body());
        Event event = Controller.getInstance().getEvent(jsonObject.getString("eventid"));
        User user = Controller.getInstance().getUser(jsonObject.getString("userid"));
        event.getAssignedUsers().add(user);
        Controller.getInstance().updatePlaygroundEvent(event);
        if (jsonObject.getString("eventid") != null && jsonObject.getString("userid") != null) {
            ctx.status(200);
        } else {
            ctx.status(404);
        }
    };

    public static Handler addPlaygroundEventPost = ctx -> {
        JSONObject jsonObject = new JSONObject(ctx.body());
        Event event = new Event();

        Set<User> users = new HashSet<>();
        for (int i = 0; i < jsonObject.getJSONArray("users").length(); i++) {
            String userid = jsonObject.getJSONArray("users").getString(i);
            users.add(Controller.getInstance().getUser(userid));
        }

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

        event.setPlayground(jsonObject.getString("playgroundname"));
        event.setName(jsonObject.getString("name"));
        event.setParticipants(jsonObject.getInt("participants"));
        event.setImagepath(jsonObject.getString("imagepath"));
        event.setAssignedUsers(users);
        event.setDetails(details);
        event.setDescription(jsonObject.getString("description"));

        if (Controller.getInstance().addPlaygroundEvent(jsonObject.getString("playgroundname"), event).wasAcknowledged()) {
        ctx.status(200);
            System.out.println("inserted event");
        } else {
            ctx.status(404);
            System.out.println("event not created");
        }
    };

    public static Handler addPlaygroundMessagePost = ctx -> {

            JSONObject jsonObject = new JSONObject(ctx.body());

        Details details = new Details();
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, jsonObject.getInt("year"));
        cal.set(Calendar.DAY_OF_MONTH, jsonObject.getInt("day"));
        cal.set(Calendar.MONTH, jsonObject.getInt("month"));

        cal.set(Calendar.HOUR, jsonObject.getInt("hourstart"));
        cal.set(Calendar.MINUTE, jsonObject.getInt("minutestart"));

        Date date = cal.getTime();

            Message message = new Message.Builder()
                    .setMessageString(jsonObject.getString("message"))
                    .set_id(jsonObject.getString("id"))
                    .setIcon(jsonObject.getString("icon"))
                    .setName(jsonObject.getString("name"))
                    .setCategory(jsonObject.getString("category"))
                    .setPlaygroundID(jsonObject.getString("playgroundID"))
                    .setWrittenByID(jsonObject.getString("writtenbyID"))
                    .setDate(date)
                    .build();



            if (Controller.getInstance().addPlaygroundMessage(jsonObject.getString("playgroundID"), message).wasAcknowledged()) {
                ctx.status(200);
            } else {
                ctx.status(404);
            }
    };

    public static Handler removePedagogueFromPlaygroundPut = ctx -> {
        JSONObject jsonObject = new JSONObject(ctx.body());
        if (Controller.getInstance().removePedagogueFromPlayground(jsonObject.getString("playgroudID"), jsonObject.getString("pedagogueID")))
            ctx.status(200);
        else
            ctx.status(404);
    };
}
