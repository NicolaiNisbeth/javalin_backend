package javalin_resources.Util;

import io.javalin.http.Context;
import io.javalin.http.ErrorHandler;

import java.util.HashMap;
import java.util.Map;

import static javalin_resources.Util.RequestUtil.*;

public class ViewUtil {

    public static Map<String, Object> baseModel(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("msg", new MessageBundle(getSessionLocale(ctx)));
        model.put("currentUser", getSessionCurrentUser(ctx));
        return model;
    }

    public static ErrorHandler notFound = ctx -> {
        ctx.render("/NOT_FOUND", baseModel(ctx));
    };
}