package javalin_resources.collections;

import com.mongodb.MongoException;
import database.Controller;
import database.dto.DetailsDTO;
import database.dto.MessageDTO;
import database.exceptions.NoModificationException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.ContentType;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

public class Message implements Tag {

  /**
   * DELETE
   */
  public static Handler deletePlaygroundMessage = ctx -> {
    String id = ctx.pathParam(PLAYGROUND_MESSAGE_ID);
    try {
      Controller.getInstance().deletePlaygroundMessage(id);
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
    MessageDTO message = Controller.getInstance().getMessage(ctx.pathParam((MESSAGE_ID)));
    if (message != null) {
      ctx.json(message).contentType("json");
      ctx.status(200);
    } else
      ctx.status(404).result("Failed to retrieve message");
  };

  public static Handler readAllMessages = ctx -> {
    List<MessageDTO> messages = Controller.getInstance().getMessagesInPlayground(ctx.pathParam(PLAYGROUND_NAME));
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

    JSONObject jsonObject = new JSONObject(ctx.body());

    // TODO: Details
    DetailsDTO detailsModel = new DetailsDTO();
    Calendar cal = Calendar.getInstance();

    cal.set(Calendar.YEAR, jsonObject.getInt(EVENT_YEAR));
    cal.set(Calendar.DAY_OF_MONTH, jsonObject.getInt(EVENT_DAY));
    cal.set(Calendar.MONTH, jsonObject.getInt(EVENT_MONTH));

    cal.set(Calendar.HOUR, jsonObject.getInt(EVENT_HOUR_START));
    cal.set(Calendar.MINUTE, jsonObject.getInt(EVENT_MINUTE_START));

    Date date = cal.getTime();

    MessageDTO message = new MessageDTO.Builder()
      .setMessageString(jsonObject.getString(MESSAGE_STRING))
      .set_id(jsonObject.getString(MESSAGE_ID))
      .setIcon(jsonObject.getString(MESSAGE_ICON))
      .setCategory(jsonObject.getString(MESSAGE_CATEGORY))
      .setPlaygroundID(jsonObject.getString(PLAYGROUND_ID))
      .setWrittenByID(jsonObject.getString(MESSAGE_WRITTENBY_ID))
      .setDate(date)
      .build();


    if (Controller.getInstance().createPlaygroundMessage(jsonObject.getString(PLAYGROUND_ID), message).wasAcknowledged()) {
      ctx.status(200).result("Message posted");
    } else {
      ctx.status(404).result("Failed to post message");
    }
  };

  /**
   * PUT
   */
  public static Handler updatePlaygroundMessage = ctx -> {

    JSONObject jsonObject = new JSONObject(ctx.body());
    MessageDTO message = Controller.getInstance().getMessage(ctx.pathParam("id"));

    // TODO Hvordan kommer den detail parameter til at foregå?
    if (jsonObject.get(HOUR) != null) {
      Calendar cal = Calendar.getInstance();

      cal.set(Calendar.YEAR, jsonObject.getInt(YEAR));
      cal.set(Calendar.DAY_OF_MONTH, jsonObject.getInt(DAY));
      cal.set(Calendar.MONTH, jsonObject.getInt(MONTH));


      cal.set(Calendar.HOUR, jsonObject.getInt(HOUR));
      cal.set(Calendar.MINUTE, jsonObject.getInt(MINUTE));
      message.setDate(cal.getTime());
    }
    if (jsonObject.get(MESSAGE_CATEGORY) != null)
      message.setCategory(jsonObject.getString(MESSAGE_CATEGORY));

    if (jsonObject.get(MESSAGE_ICON) != null)
      message.setIcon(jsonObject.getString(MESSAGE_ICON));

    if (jsonObject.get(MESSAGE_STRING) != null)
      message.setMessageString(jsonObject.getString(MESSAGE_STRING));

    if (jsonObject.get(PLAYGROUND_ID) != null)
      message.setPlaygroundID(jsonObject.getString(PLAYGROUND_ID));

    if (jsonObject.get(MESSAGE_WRITTENBY_ID) != null)
      message.setWrittenByID(MESSAGE_WRITTENBY_ID);

    if (Controller.getInstance().createPlaygroundMessage(jsonObject.getString(PLAYGROUND_ID), message).wasAcknowledged())
      ctx.status(200).result("The message was created for the playground " + jsonObject.getString(PLAYGROUND_ID));

    else {
      ctx.status(404).result("There was an error");
    }
  };

}
