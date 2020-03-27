package resources;


import database.collections.Playground;
import database.dao.Controller;
import io.javalin.http.Handler;
import util.ViewUtil;

import java.util.ArrayList;
import java.util.Map;

//@Path("/galgeleg")
public class PlaygroundResource {

    public static Handler AllPlaygroundsHandlerGet = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("playgrounds", Controller.getController().getAllPlaygrounds());
    };

    public static Handler OnePlaygroundHandlerGet = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("playgrounds", Controller.getController().getPlayground());
    };

    public static Handler OnePlaygroundEmployeesGet = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("playgrounds", Controller.getController().getPlayground().getEmployees);
    };
}
