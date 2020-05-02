package javalin_resources.collections;

import com.mongodb.WriteResult;
import database.Controller;
import database.dto.EventDTO;
import database.dto.MessageDTO;
import database.dto.PlaygroundDTO;
import database.dto.UserDTO;
import database.exceptions.NoModificationException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.ContentType;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public class Playground implements Tag {

  /**
   * DELETE
   */
  public static Handler deleteOnePlayground = ctx -> {
    String playgroundname = "";
    playgroundname = ctx.pathParam(PLAYGROUND_NAME);

    if (playgroundname != "") {
      Controller.getInstance().deletePlayground(playgroundname);
      ctx.status(200);
      System.out.println("Deleted playground with name " + playgroundname);
    } else {
      ctx.status(404).result("Couldn't delete playground or it doesn't exist");
      System.out.println("Found no playground");
    }
  };

  /**
   * GET
   */
  public static Handler readAllPlaygrounds = ctx -> {
    try {
      List<PlaygroundDTO> playgrounds = Controller.getInstance().getPlaygrounds();
      ctx.status(HttpStatus.OK_200);
      ctx.result("Ok - playgrounds were fetched successfully");
      ctx.json(playgrounds);
      ctx.contentType(ContentType.JSON);
    } catch (NoSuchElementException e) {
      ctx.status(HttpStatus.NOT_FOUND_404);
      ctx.result("Not found - no playgrounds in database");
      ctx.contentType(ContentType.JSON);
    } catch (Exception e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
      ctx.result("Internal error - failed to fetch playgrounds in database");
      ctx.contentType(ContentType.JSON);
    }
  };

  public static Handler readOnePlayground = ctx -> {
    ctx.json(Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME))).contentType("json");

  };

  public static Handler readOnePlaygroundAllEmployee = ctx -> {
    ctx.json(Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME)).getAssignedPedagogue()).contentType("json");
  };

  public static Handler readOnePlaygroundOneEmployee = ctx -> {
    ctx.json(Controller.getInstance().getUser(ctx.pathParam(USER_NAME))).contentType("json");
  };

  /**
   * POST
   */
  public static Handler createPlayground = ctx -> {
    JSONObject jsonObject = new JSONObject(ctx.body());
    Set<UserDTO> users = new HashSet<>();
    for (int i = 0; i < jsonObject.getJSONArray(USERS).length(); i++) {
      String userId = (String) jsonObject.getJSONArray(USERS).get(i);
      try {
        users.add(Controller.getInstance().getUser(userId));
      } catch (NoSuchElementException e) {
        e.printStackTrace();
      }
    }

    PlaygroundDTO playground = new PlaygroundDTO.Builder(jsonObject.getString(PLAYGROUND_NAME))
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

  /**
   * PUT
   */
  public static Handler updatePlayground = ctx -> {
    PlaygroundDTO playground = Controller.getInstance().getPlayground(ctx.pathParam(PLAYGROUND_NAME));
    JSONObject jsonObject = new JSONObject(ctx.body());
    if (playground != null) {
      if (jsonObject.has(PLAYGROUND_STREET_NAME))
        playground.setStreetName(jsonObject.getString(PLAYGROUND_STREET_NAME));

      if (jsonObject.has(PLAYGROUND_PEDAGOGUES)) {
        Set<UserDTO> pedagoges = new HashSet<>();

        for (int i = 0; i < jsonObject.getJSONArray(PLAYGROUND_PEDAGOGUES).length(); i++) {
          String username = jsonObject.getJSONArray(PLAYGROUND_PEDAGOGUES).getString(i);
          pedagoges.add(Controller.getInstance().getUser(username));
        }
        playground.setAssignedPedagogue(pedagoges);
      }

      if (jsonObject.has(PLAYGROUND_COMMUNE))
        playground.setCommune(jsonObject.getString(PLAYGROUND_COMMUNE));

      if (jsonObject.has(PLAYGROUND_EVENTS)) {
        Set<EventDTO> eventSet = new HashSet<>();
        for (int i = 0; i < jsonObject.getJSONArray(PLAYGROUND_EVENTS).length(); i++) {
          String eventid = jsonObject.getJSONArray(PLAYGROUND_EVENTS).getJSONObject(i).getString(PLAYGROUND_EVENTS);
          eventSet.add(Controller.getInstance().getEvent(eventid));
        }
        playground.setEvents(eventSet);
      }
      if (jsonObject.has(PLAYGROUND_HASSOCCERFIELD))
        playground.setHasSoccerField(jsonObject.getBoolean(PLAYGROUND_HASSOCCERFIELD));

      if (jsonObject.has(PLAYGROUND_ID))
        playground.setId(jsonObject.getString(PLAYGROUND_ID));

      if (jsonObject.has(PLAYGROUND_IMAGEPATH))
        playground.setImagePath(jsonObject.getString(PLAYGROUND_IMAGEPATH));

      if (jsonObject.has(PLAYGROUND_MESSAGES)) {
        Set<MessageDTO> messagesSet = new HashSet<>();
        for (int i = 0; i < jsonObject.getJSONArray(PLAYGROUND_MESSAGE_ID).length(); i++) {
          String messageid = jsonObject.getJSONArray(PLAYGROUND_MESSAGE_ID).getJSONObject(i).getString(PLAYGROUND_MESSAGE_ID);
          messagesSet.add(Controller.getInstance().getMessage(messageid));
        }
        playground.setMessages(messagesSet);
      }

      if (jsonObject.has(PLAYGROUND_STREET_NUMBER))
        playground.setStreetNumber(jsonObject.getInt(PLAYGROUND_STREET_NUMBER));

      if (jsonObject.has(PLAYGROUND_TOILETS))
        playground.setToiletPossibilities(jsonObject.getBoolean(PLAYGROUND_TOILETS));

      if (jsonObject.has(PLAYGROUND_ZIPCODE))
        playground.setZipCode(jsonObject.getInt(PLAYGROUND_ZIPCODE));

      // TODO: remove true and catch exception and set corresponding status code

      try {
        Controller.getInstance().updatePlayground(playground);
        ctx.status(HttpStatus.OK_200);
        ctx.result("Successful - playground was updated successfully");
        ctx.contentType(ContentType.JSON);
      } catch (NoModificationException e){
        ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
        ctx.result("Internal error - playground could not be updated");
        ctx.contentType(ContentType.JSON);
      }

    } else {
      ctx.status(404).result("Playground didn't update");
    }
  };




}
