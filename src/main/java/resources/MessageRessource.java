package resources;

import database.collections.Details;
import database.collections.Event;
import database.collections.Message;
import database.dao.Controller;
import database.utils.ViewUtil;
import io.javalin.http.Handler;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MessageRessource {

    public static Handler OneMessageHandlerGet = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        Message message = Controller.getInstance().getMessage(ctx.pathParam("message"));
        if (message != null) {
            model.put("message", message);
            ctx.status(200);
        } else
            ctx.status(404);
    };
    public static Handler PlayGroundMessagesHandlerGet = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        List<Message> messages = Controller.getInstance().getPlaygroundMessages(ctx.pathParam("playground"));
        if (messages != null) {
            model.put("messages", messages);
            ctx.status(200);
        } else {
            ctx.status(404);
        }
    };

    public static Handler PlaygroundMessageUpdatePut = ctx -> {

        JSONObject jsonObject = new JSONObject(ctx.body());
        Message message = Controller.getInstance().getMessage(jsonObject.getString("id"));

        if (message != null) {
            ctx.status(200);

// TODO Hvordan kommer den detail parameter til at foregå?
            if (jsonObject.get("hour") != null) {
                Details details = new Details();
                Calendar cal = Calendar.getInstance();

                cal.set(Calendar.YEAR, jsonObject.getInt("year"));
                cal.set(Calendar.DAY_OF_MONTH, jsonObject.getInt("day"));
                cal.set(Calendar.MONTH, jsonObject.getInt("month"));


                cal.set(Calendar.HOUR, jsonObject.getInt("hour"));
                cal.set(Calendar.MINUTE, jsonObject.getInt("minute"));
                message.setDate(cal.getTime());
            }
            if (jsonObject.get("category") != null)
                message.setCategory(jsonObject.getString("category"));

            if (jsonObject.get("icon") != null)
                message.setIcon(jsonObject.getString("icon"));

            if (jsonObject.get("message") != null)
                message.setMessageString(jsonObject.getString("message"));

            if (jsonObject.get("playgroundID") != null)
                message.setPlaygroundID(jsonObject.getString("playgroundID"));

            if (jsonObject.get("writtenbyID") != null)
                message.setWrittenByID("writtenbyID");
        } else {
            ctx.status(404);
        }
    };

    public static Handler removePlaygroundMessageHandlerPut = ctx -> {
        JSONObject jsonObject = new JSONObject(ctx.body());
        if (Controller.getInstance().removePlaygroundMessage(jsonObject.getString("messageID")))
            ctx.status(200);
        else
            ctx.status(404);
    };
}
