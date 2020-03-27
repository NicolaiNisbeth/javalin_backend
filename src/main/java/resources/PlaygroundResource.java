package resources;


import database.dao.Controller;
import io.javalin.http.Handler;
import database.utils.ViewUtil;

import java.util.Map;

//@Path("/galgeleg")
public class PlaygroundResource {

    public static Handler AllPlaygroundsHandlerGet = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("playgrounds", Controller.getInstance().getPlaygrounds());
    };

    public static Handler OnePlaygroundHandlerGet = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("playgrounds", Controller.getInstance().getPlayground(ctx.queryParam("playground_name")));
    };

    public static Handler OnePlaygroundEmployeesGet = ctx -> {
        Map<String, Object> model = ViewUtil.baseModel(ctx);
        model.put("playgrounds", Controller.getInstance().getPlayground(ctx.queryParam("playground_name")).getAssignedPedagogue());
    };
}
