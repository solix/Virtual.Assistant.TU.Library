package controllers;


import play.mvc.*;

import views.html.*;

public class Application extends Controller {
    /**
     * responds to the index page request
     *
     * @return
     */
    public static Result index() {
        return ok(index.render("Dashboard"));
    }

    /**
     * responds to the index page request
     *
     * @return
     */
    public static Result task() {
        return ok(task.render());
    }

    /**
     * responds to the index page request
     *
     * @return
     */
    public static Result login() {
        return ok(login.render());
    }


    public static Result calendar() {

        return ok(calendar.render());
    }
}
