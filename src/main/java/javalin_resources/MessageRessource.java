package javalin_resources;

import database.collections.Message;
import database.dao.Controller;
import io.javalin.http.Handler;
import javalin_resources.Util.ViewUtil;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MessageRessource {




    public static Handler PlaygroundMessageInsertPost = ctx -> {

        Date date = new Date();
        JSONObject jsonObject = new JSONObject(ctx.body());
        date.setYear(jsonObject.getInt("year"));
        date.setMonth((Calendar.MARCH));
        date.setDate(jsonObject.getInt("day"));
        date.setHours(jsonObject.getInt("hour"));
        date.setMinutes(jsonObject.getInt("minute"));

        Message message = new Message.Builder()
                .setMessageString(jsonObject.getString("messageString"))
                .setIcon(jsonObject.getString("icon"))
                .setOutDated(jsonObject.getBoolean("outDated"))
                .setWrittenByID(jsonObject.getString("writtenByID"))
                .setPlaygroundID(jsonObject.getString("playgroundID"))
                .setDate(date)
                .build();


        if (Controller.getInstance().addPlaygroundMessage(jsonObject.getString("playgroundID"), message).wasAcknowledged()) {
            ctx.status(200);
            System.out.println("message created");
        } else {
            ctx.status(404);
            System.out.println("message faiiiiiiiled");
        }

    };

    public static Handler PlaygroundMessageUpdatePut = ctx -> {

        JSONObject jsonObject = new JSONObject(ctx.body());
        Message message = Controller.getInstance().getMessage(ctx.pathParam("id"));

// TODO Hvordan kommer den detail parameter til at foregå?
        if (jsonObject.get("hour") != null) {
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

        if (jsonObject.get("messageString") != null)
            message.setMessageString(jsonObject.getString("messageString"));

        if (jsonObject.get("playgroundID") != null)
            message.setPlaygroundID(jsonObject.getString("playgroundID"));

        if (jsonObject.get("WrittenByID") != null)
            message.setWrittenByID("WrittenByID");

        if (Controller.getInstance().addPlaygroundMessage(jsonObject.getString("playgroundID"), message).wasAcknowledged())
            ctx.status(200).result("The message was created for the playground " + jsonObject.getString("playgroundID"));


        else {
            ctx.status(404).result("there was an error");
        }
    };

    public static Handler removePlaygroundMessageHandlerDelete = ctx -> {
        if (Controller.getInstance().removePlaygroundMessage(ctx.pathParam("id")))
            ctx.status(200);
        else
            ctx.status(404);
    };
}
