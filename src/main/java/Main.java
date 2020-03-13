
import io.javalin.Javalin;
import io.javalin.http.Context;
import resources.GalgelegResource;
import resources.UserLogin;

public class Main {
    public static Javalin app;


    public static void main(String[] args) throws Exception {
        start();
    }

    public static void stop() {
        app.stop();
        app = null;
    }

    public static void start() throws Exception {
        if (app != null) return;

        app = Javalin.create().start(8080);
        app.before(ctx -> {
            System.out.println("Javalin Server fik " + ctx.method() + " på " + ctx.url() + " med query " + ctx.queryParamMap() + " og form " + ctx.formParamMap());
        });
        app.exception(Exception.class, (e, ctx) -> {
            e.printStackTrace();
        });
        app.config.addStaticFiles("webapp");

        // Serverside gemererede websider
        app.get("/prut", ctx -> ctx.status(404).result("Ups, der kom en...!").contentType("text/html"));
        app.get("/formular", ctx -> formular(ctx));

        // REST endpoints
        app.config.enableCorsForAllOrigins();
        app.get("/rest/hej", ctx -> ctx.result("Hejsa, godt at møde dig!"));
        app.get("/rest/hej/:fornavn", ctx -> ctx.result("Hej " + ctx.queryParam("fornavn") + ", godt at møde dig!"));
        //  app.get("/rest/bruger/:brugernavn", ctx -> bruger(ctx));
        //app.post("/rest/sendGlemtAdgangskodeEmail", ctx -> sendGlemtAdgangskodeEmail(ctx));
        app.post("rest/brugerLogin", ctx ->
                ctx.json(UserLogin.verificerLogin(ctx.body())).contentType("json"));
        app.get("rest/galgeleg/highscore", ctx ->
                ctx.json(GalgelegResource.getHighscoreListe()).contentType("json"));
        app.post("rest/galgeleg/:username", ctx ->
                ctx.result(GalgelegResource.startGame(ctx.pathParam("username"))).contentType("json"));
        app.get("rest/galgeleg/:username/:guess", ctx ->
                ctx.result(GalgelegResource.makeGuess(ctx.pathParam("username"), ctx.pathParam("guess"))).contentType("json"));

    }

    private static void formular(Context ctx) {
        String fornavn = ctx.queryParam("fornavn");
        if (fornavn == null) {
            ctx.contentType("text/html; charset=utf-8").result("<html><body><form method=get>Skriv dit fornavn: <input name=fornavn type=text></form></html>");
        } else {
            ctx.contentType("text/html; charset=utf-8").result("<html><body>Hej " + fornavn + ", godt at møde dig!</html>");
        }
    }



   /* private static void bruger(Context ctx) throws Exception {
        String brugernavn = ctx.pathParam("brugernavn");    // del af path  /bruger/s123456
        String adgangskode = ctx.queryParam("adgangskode"); // del af query  ?adgangskode=kode1xyz
        Brugeradmin ba = (Brugeradmin) Naming.lookup("server.rmi://javabog.dk/brugeradmin");
        if (adgangskode == null) {
            Bruger bruger = ba.hentBrugerOffentligt(brugernavn);
            ctx.json(bruger);
        } else try {
            Bruger bruger = ba.hentBruger(brugernavn, adgangskode);
            ctx.json(bruger);
        } catch (Exception e) {
            ctx.status(401).result("Unauthorized");
        }
    }*/

/*    private static void sendGlemtAdgangskodeEmail(Context ctx) throws Exception {
        Brugeradmin ba = (Brugeradmin) Naming.lookup("server.rmi://javabog.dk/brugeradmin");
        String brugernavn = ctx.formParam("brugernavn");
        String følgetekst = ctx.formParam("foelgetekst");
        if (brugernavn == null) brugernavn = ctx.queryParam("brugernavn");
        ba.sendGlemtAdgangskodeEmail(brugernavn, følgetekst);
        ctx.result("Der blev sendt en mail til " + brugernavn + " med teksten " + følgetekst);
    }*/

}
