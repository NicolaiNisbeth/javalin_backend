package resources;

import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import database.Controller;
import database.dto.DetailsDTO;
import database.dto.EventDTO;
import database.dto.PlaygroundDTO;
import database.dto.UserDTO;
import database.exceptions.NoModificationException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.ContentType;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;

import java.util.*;

public class Event implements Tag {

  public static Handler getEvent = ctx -> {
    EventDTO event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
    if (event != null) {
      ctx.json(event).contentType("json");
      ctx.status(200);
    } else
      ctx.status(404).result("Couldn't find an event");
  };

  public static Handler getEvents = ctx -> {
    List<EventDTO> events = Controller.getInstance().getEventsInPlayground(ctx.pathParam(PLAYGROUND_NAMES));
    if (events != null) {
      ctx.json(events).contentType("json");
      ctx.status(200);
    } else {
      ctx.status(404).result("Couldn't find any events for this playground");
    }
  };

  public static Handler deleteEvent = ctx -> {
    String id = ctx.pathParam(EVENT_ID);

    try {
      Controller.getInstance().deletePlaygroundEvent(id);
      ctx.status(HttpStatus.OK_200);
      ctx.result("Success - event was deleted successfully");
      ctx.contentType(ContentType.JSON);
    } catch (NoSuchElementException e) {
      ctx.status(HttpStatus.NOT_FOUND_404);
      ctx.result(String.format("Not found - event with id=%s was not found", id));
      ctx.contentType(ContentType.JSON);
    } catch (NoModificationException | MongoException e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
      ctx.result("Server error - event could not be deleted");
      ctx.contentType(ContentType.JSON);
    }
  };

  public static Handler createEvent = ctx -> {
    JSONObject jsonObject = new JSONObject(ctx.body());
    EventDTO event = new EventDTO();

    Set<UserDTO> users = new HashSet<>();
    for (int i = 0; i < jsonObject.getJSONArray(USERS).length(); i++) {
      String userid = jsonObject.getJSONArray(USERS).getString(i);
      users.add(Controller.getInstance().getUser(userid));
    }

    DetailsDTO detailsModel = new DetailsDTO();
    Calendar cal = Calendar.getInstance();

    cal.set(Calendar.YEAR, jsonObject.getInt(EVENT_YEAR));
    cal.set(Calendar.DAY_OF_MONTH, jsonObject.getInt(EVENT_DAY));
    cal.set(Calendar.MONTH, jsonObject.getInt(EVENT_MONTH));

    detailsModel.setDate(cal.getTime());

    cal.set(Calendar.HOUR, jsonObject.getInt(EVENT_HOUR_START));
    cal.set(Calendar.MINUTE, jsonObject.getInt(EVENT_MINUTE_START));

    detailsModel.setStartTime(cal.getTime());

    cal.set(Calendar.HOUR, jsonObject.getInt(EVENT_HOUR_END));
    cal.set(Calendar.MINUTE, jsonObject.getInt(EVENT_MINUTE_END));

    detailsModel.setStartTime(cal.getTime());

    event.setPlayground(jsonObject.getString(PLAYGROUND_NAMES));
    event.setName(jsonObject.getString(EVENT_NAME));
    event.setParticipants(jsonObject.getInt(EVENT_PARTICIPANTS));
    event.setImagepath(jsonObject.getString(EVENT_IMAGEPATH));
    event.setAssignedUsers(users);
    event.setDetails(detailsModel);
    event.setDescription(jsonObject.getString(EVENT_DESCRIPTION));

    if (Controller.getInstance().createPlaygroundEvent(jsonObject.getString(PLAYGROUND_NAMES), event).wasAcknowledged()) {
      ctx.status(200).result("Event Created");
      System.out.println("inserted event");
    } else {
      ctx.status(404);
      System.out.println("Event not created");
    }
  };

  public static Handler getParticipants = ctx -> {
    EventDTO event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
    if (event != null) {
      ctx.json(event.getParticipants()).contentType("json");
      ctx.status(200);
    } else
      ctx.status(404).result("Couldn't find any participants for this event");
  };

  public static Handler getUserInEvent = ctx -> {
    EventDTO event = Controller.getInstance().getEvent(ctx.pathParam(EVENT_ID));
    for (UserDTO user : event.getAssignedUsers())
      if (user.getUsername().equals(ctx.pathParam(USER_NAME))) {
        ctx.json(user).contentType("json");
        ctx.status(200);
        return;
      } else
        ctx.status(404).result("Couldn't find the participant for this event");
  };



  public static Handler registerUser = ctx -> {
    String id = ctx.pathParam("id");
    String username = ctx.pathParam("username");
    if (id.isEmpty() || username.isEmpty()) {
      ctx.status(HttpStatus.BAD_REQUEST_400);
      ctx.result("Bad request - No event id or username in path param");
      ctx.contentType(ContentType.JSON);
      return;
    }

    try {
      WriteResult writeResult = Controller.getInstance().addUserToEvent(id, username);
      ctx.status(HttpStatus.OK_200);
      ctx.result("OK - user joined event successfully");
      ctx.json(new UserDTO.Builder(username));
      ctx.contentType(ContentType.JSON);
    } catch (NoSuchElementException e) {
      ctx.status(HttpStatus.NOT_FOUND_404);
      ctx.result(String.format("Not found - event %s or user %s is not in database", id, username));
      ctx.contentType(ContentType.JSON);
    } catch (NoModificationException | MongoException e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
      ctx.result("Internal error - user failed to join event");
      ctx.contentType(ContentType.JSON);
    }
  };

  public static Handler unregister = ctx -> {
    String id = ctx.pathParam("id");
    String username = ctx.pathParam("username");
    if (id.isEmpty() || username.isEmpty()) {
      ctx.status(HttpStatus.BAD_REQUEST_400);
      ctx.result("Bad request - No event id or username in path param");
      ctx.contentType(ContentType.JSON);
      return;
    }

    try {
      Controller.getInstance().removeUserFromEvent(id, username);
      ctx.status(HttpStatus.OK_200);
      ctx.result("OK - user was removed from event successfully");
      ctx.json(new UserDTO.Builder(username));
      ctx.contentType(ContentType.JSON);
    } catch (NoSuchElementException e) {
      ctx.status(HttpStatus.NOT_FOUND_404);
      ctx.result(String.format("Not found - event %s or user %s is not in database", id, username));
      ctx.contentType(ContentType.JSON);
    } catch (NoModificationException | MongoException e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
      ctx.result("Internal error - failed to remove user from event");
      ctx.contentType(ContentType.JSON);
    }
  };
}
