package javalin_resources.collections;

import com.mongodb.MongoException;
import database.Controller;
import database.dto.PlaygroundDTO;
import database.dto.UserDTO;
import database.exceptions.NoModificationException;
import io.javalin.http.Handler;
import io.javalin.plugin.openapi.annotations.ContentType;
import org.eclipse.jetty.http.HttpStatus;
import org.json.JSONObject;

import java.util.NoSuchElementException;

public class Pedagogue implements Tag {

  /**
   * DELETE
   */
  public static Handler deletePedagogueFromPlayground = ctx -> {
    String playgroundName = ctx.pathParam(PLAYGROUND_NAME);
    String username = ctx.pathParam(USER_NAME);

    try {
      Controller.getInstance().removePedagogueFromPlayground(playgroundName, username);
      ctx.status(HttpStatus.OK_200);
      ctx.result("Success - pedagogue was deleted successfully");
      ctx.contentType(ContentType.JSON);
    } catch (NoSuchElementException e){
      ctx.status(HttpStatus.NOT_FOUND_404);
      ctx.result(String.format("Not found - pedagogue with username=%s was not found", username));
      ctx.contentType(ContentType.JSON);
    } catch (NoModificationException | MongoException e){
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
      ctx.result("Server error - pedagogue could not be deleted");
      ctx.contentType(ContentType.JSON);
    }
  };

  /**
   * PUT
   */
  public static Handler updatePedagogueToPlayGround = ctx -> {
    JSONObject jsonObject = new JSONObject(ctx.body());
    PlaygroundDTO playground = Controller.getInstance().getPlayground(jsonObject.getString(PLAYGROUND_NAME));
    UserDTO user = Controller.getInstance().getUser(jsonObject.getString(PEDAGOGUE));
    playground.getAssignedPedagogue().add(user);
    Controller.getInstance().updatePlayground(playground);
    if (jsonObject.getString(PEDAGOGUE) != null && jsonObject.getString(PLAYGROUND_NAME) != null) {
      ctx.status(200).result("Updated Successfull");
    } else {
      ctx.status(404).result("Failed to update");
    }
  };


}
