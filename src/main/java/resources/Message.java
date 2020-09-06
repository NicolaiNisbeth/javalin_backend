package resources;

import com.mongodb.MongoException;
import database.Controller;
import database.dto.MessageDTO;
import database.exceptions.NoModificationException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.ContentType;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

public class Message implements Tag {

  public static Handler createMessage = ctx -> {
    String messageJson = ctx.formParam(("message"));
    JSONObject jsonObject = new JSONObject(messageJson);
    Calendar cal = Calendar.getInstance();
    Date date = cal.getTime();

    MessageDTO message = new MessageDTO.Builder()
            .setMessageString(jsonObject.getString(MESSAGE_STRING))
            .setCategory(jsonObject.getString(MESSAGE_CATEGORY))
            .setPlaygroundID(jsonObject.getString("playgroundID"))
            .setDate(date)
            .setHasImage(jsonObject.getBoolean(MESSAGE_HASIMAGE))
            .build();

    if (Controller.getInstance().createPlaygroundMessage(jsonObject.getString("playgroundID"), message).wasAcknowledged()) { //PLAYGROUND_ID
      ctx.status(200).result("Message posted");
      ctx.json(Controller.getInstance().getMessage(message.getID()));
    } else {
      ctx.status(404).result("Failed to post message");
    }
  };

  public static Handler getMessage = ctx -> {
    MessageDTO message = Controller.getInstance().getMessage(ctx.pathParam(("id"))); //MESSAGE_ID
    if (message != null) {
      ctx.json(message).contentType("json");
      ctx.status(200);
    } else
      ctx.status(404).result("Failed to retrieve message");
  };

  public static Handler getMessages = ctx -> {
    List<MessageDTO> messages = Controller.getInstance().getMessagesInPlayground(ctx.pathParam(PLAYGROUND_NAMES));
    if (messages != null) {
      ctx.json(messages).contentType("json");
      ctx.status(200);
    } else
      ctx.status(404).result("Failed to retrieve any messages");
  };

  public static Handler deleteMessage = ctx -> {
    String id = ctx.pathParam("id"); //PLAYGROUND_MESSAGE_ID
    try {
      MessageDTO message = Controller.getInstance().getMessage(id);
      Controller.getInstance().deletePlaygroundMessage(id);
      ctx.status(200).result("Success - playground message was deleted");    //HttpStatus.OK_200
      ctx.json(message);
    } catch (NoSuchElementException e) {
      ctx.status(404); //HttpStatus.NOT_FOUND_404
      ctx.result(String.format("Not found - No playground message with ID=%s", id));
      ctx.contentType(ContentType.JSON);
    } catch (MongoException | NoModificationException e) {
      ctx.status(500); //HttpStatus.INTERNAL_SERVER_ERROR_500
      ctx.result("Server error - playground message could not be deleted");
      ctx.contentType(ContentType.JSON);
    }
  };

  public static Handler updateMessage = ctx -> {
    String messageJson = ctx.formParam(("message"));
    JSONObject jsonObject = new JSONObject(messageJson);
    MessageDTO message = Controller.getInstance().getMessage(jsonObject.getString("id"));

    if (jsonObject.get(MESSAGE_CATEGORY) != null)
      message.setCategory(jsonObject.getString(MESSAGE_CATEGORY));

    if (jsonObject.get(MESSAGE_STRING) != null)
      message.setMessageString(jsonObject.getString(MESSAGE_STRING));

    if (jsonObject.get("playgroundID") != null) //PLAYGROUND_ID
      message.setPlaygroundID(jsonObject.getString("playgroundID"));

    if (jsonObject.get(MESSAGE_WRITTENBY_ID) != null)
      message.setWrittenByID(MESSAGE_WRITTENBY_ID);

    message.setHasImage(jsonObject.getBoolean("hasImage"));

    if (Controller.getInstance().updatePlaygroundMessage(message).wasAcknowledged()) {
      ctx.status(200).result("Updated message with ID: " + message.getID());
      ctx.json(Controller.getInstance().getMessage(message.getID()));
    } else {
      ctx.status(404).result("There was an error");
    }
  };
}
