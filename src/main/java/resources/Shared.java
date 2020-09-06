package resources;

import database.Controller;
import database.dto.UserDTO;
import io.javalin.http.Context;
import org.mindrot.jbcrypt.BCrypt;
import java.util.NoSuchElementException;

public class Shared {

  public static boolean verifyAdminCredentials(String username, String password, Context ctx) {
    UserDTO admin;

    try {
      admin = Controller.getInstance().getUser(username);
    } catch (NoSuchElementException e) {
      e.printStackTrace();
      ctx.json("Unauthorized - Wrong admin username");
      ctx.contentType("json");
      return false;
    }

    if (!admin.getStatus().equalsIgnoreCase("admin")) {
      ctx.json("Unauthorized - Wrong admin status");
      ctx.contentType("json");
      return false;
    }

    if (password.equalsIgnoreCase(admin.getPassword())) {
      return true;
    }

    if (BCrypt.checkpw(password, admin.getPassword())) {
      return true;
    } else {
      ctx.json("Unauthorized - Wrong admin password");
      ctx.contentType("json");
      return false;
    }
  }

}
