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

  public static Handler deletePlaygroundMessage = ctx -> {
    String id = ctx.pathParam("id"); //PLAYGROUND_MESSAGE_ID
    try {
      Controller.getInstance().deletePlaygroundMessage(id);
      deleteMessageImage(id);
      ctx.status(HttpStatus.OK_200);
      ctx.result("Success - playground message was deleted");
      ctx.contentType(ContentType.JSON);
    } catch (NoSuchElementException e) {
      ctx.status(HttpStatus.NOT_FOUND_404);
      ctx.result(String.format("Not found - No playground message with ID=%s", id));
      ctx.contentType(ContentType.JSON);
    } catch (MongoException | NoModificationException e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
      ctx.result("Server error - playground message could not be deleted");
      ctx.contentType(ContentType.JSON);
    }
  };

  /**
   * GET
   */
  public static Handler readOneMessage = ctx -> {
    MessageDTO message = Controller.getInstance().getMessage(ctx.pathParam(("id"))); //MESSAGE_ID
    if (message != null) {
      ctx.json(message).contentType("json");
      ctx.status(200);
    } else
      ctx.status(404).result("Failed to retrieve message");
  };

  public static Handler readAllMessages = ctx -> {
    List<MessageDTO> messages = Controller.getInstance().getMessagesInPlayground(ctx.pathParam(PLAYGROUND_NAMES));
    if (messages != null) {
      ctx.json(messages).contentType("json");
      ctx.status(200);
    } else
      ctx.status(404).result("Failed to retrieve any messages");
  };

  /**
   * POST
   */
  public static Handler createPlaygroundMessage = ctx -> {

    BufferedImage bufferedImage = null;
    String messageJson = ctx.formParam(("message"));
    JSONObject jsonObject = new JSONObject(messageJson);

    // TODO: Details
    //Details details = new Details();
    Calendar cal = Calendar.getInstance();
    //cal.set(Calendar.YEAR, jsonObject.getInt(EVENT_YEAR));
    //cal.set(Calendar.DAY_OF_MONTH, jsonObject.getInt(EVENT_DAY));
    //cal.set(Calendar.MONTH, jsonObject.getInt(EVENT_MONTH));

    //cal.set(Calendar.HOUR, jsonObject.getInt(EVENT_HOUR_START));
    //cal.set(Calendar.MINUTE, jsonObject.getInt(EVENT_MINUTE_START));

    Date date = cal.getTime();

    MessageDTO message = new MessageDTO.Builder()
      .setMessageString(jsonObject.getString(MESSAGE_STRING))
      .setCategory(jsonObject.getString(MESSAGE_CATEGORY))
      .setPlaygroundID(jsonObject.getString("playgroundID"))
      .setDate(date)
      .setHasImage(jsonObject.getBoolean(MESSAGE_HASIMAGE))
      .build();

    try {
      bufferedImage = ImageIO.read(ctx.uploadedFile("image").getContent());
    } catch (Exception e) {
      System.out.println("Server: No message image was added...");
    }

    if (Controller.getInstance().createPlaygroundMessage(jsonObject.getString("playgroundID"), message).wasAcknowledged()) { //PLAYGROUND_ID
      ctx.status(200).result("Message posted");
      ctx.json(Controller.getInstance().getMessage(message.getID()));
      if (bufferedImage != null) {
        saveMessageImage(message.getID(), bufferedImage);
      }
    } else {
      ctx.status(404).result("Failed to post message");
    }
  };

  /**
   * PUT
   */
  public static Handler updatePlaygroundMessage = ctx -> {

    BufferedImage bufferedImage = null;
    String messageJson = ctx.formParam(("message"));
    JSONObject jsonObject = new JSONObject(messageJson);
    MessageDTO message = Controller.getInstance().getMessage(jsonObject.getString("id"));

    // TODO Hvordan kommer den detail parameter til at foregå?
    /*
    if (jsonObject.get(HOUR) != null) {
      Calendar cal = Calendar.getInstance();

      cal.set(Calendar.YEAR, jsonObject.getInt(YEAR));
      cal.set(Calendar.DAY_OF_MONTH, jsonObject.getInt(DAY));
      cal.set(Calendar.MONTH, jsonObject.getInt(MONTH));


      cal.set(Calendar.HOUR, jsonObject.getInt(HOUR));
      cal.set(Calendar.MINUTE, jsonObject.getInt(MINUTE));
      message.setDate(cal.getTime());
    }*/

    if (jsonObject.get(MESSAGE_CATEGORY) != null)
      message.setCategory(jsonObject.getString(MESSAGE_CATEGORY));

    /*if (jsonObject.get(MESSAGE_ICON) != null)
      message.setIcon(jsonObject.getString(MESSAGE_ICON));*/

    if (jsonObject.get(MESSAGE_STRING) != null)
      message.setMessageString(jsonObject.getString(MESSAGE_STRING));

    if (jsonObject.get("playgroundID") != null) //PLAYGROUND_ID
      message.setPlaygroundID(jsonObject.getString("playgroundID"));

    if (jsonObject.get(MESSAGE_WRITTENBY_ID) != null)
      message.setWrittenByID(MESSAGE_WRITTENBY_ID);

    message.setHasImage(jsonObject.getBoolean("hasImage"));

    try {
      bufferedImage = ImageIO.read(ctx.uploadedFile("image").getContent());
    } catch (Exception e) {
      System.out.println("Server: No message image was added...");
    }

    if (Controller.getInstance().updatePlaygroundMessage(message).wasAcknowledged()) {
      ctx.status(200).result("Updated message with ID: " + message.getID());
      ctx.json(Controller.getInstance().getMessage(message.getID()));
      if (bufferedImage != null) {
        saveMessageImage(message.getID(), bufferedImage);
      }
    } else {
      ctx.status(404).result("There was an error");
    }
  };

  public static Handler getMessageImage = ctx -> {
    File homeFolder = new File(System.getProperty("user.home"));
    Path path = Paths.get(String.format(homeFolder.toPath() +
      "/server_resource/messages/%s.png", ctx.pathParam("id")));

    File initialFile = new File(path.toString());
    InputStream targetStream = null;
    try {
      targetStream = new FileInputStream(initialFile);
    } catch (IOException e) {
      System.out.println("Server: The message have no image...");
    }

    if (targetStream != null) {
      ctx.result(targetStream).contentType("image/png");
    }
  };


  public static void saveMessageImage(String messageID, BufferedImage bufferedImage) {
    File homeFolder = new File(System.getProperty("user.home"));
    Path path = Paths.get(String.format(homeFolder.toPath() +
      "/server_resource/messages/%s.png", messageID));

    File imageFile = new File(path.toString());
    try {
      ImageIO.write(bufferedImage, "png", imageFile);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void deleteMessageImage(String messageID) {
    File homeFolder = new File(System.getProperty("user.home"));
    Path path = Paths.get(String.format(homeFolder.toPath() +
      "/server_resource/message_images/%s.png", messageID));

    File imageFile = new File(path.toString());
    if (imageFile.delete()) {
      System.out.println("Image delete for message with ID: " + messageID);
    } else {
      System.out.println("No image found for the deleted message.");
    }
  }

}
