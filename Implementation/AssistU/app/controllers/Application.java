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



    public static Result project() {

        return ok(project.render());
    }

<<<<<<< HEAD
    public static Result suggestions() {

        return ok(suggestions.render());
=======

    
    /**respond to inbox page request*/
    public static Result inbox() {

        return ok(inbox.render());
    }

     /**respond to tips and suggestions page request*/
    public static Result tips() {

        return ok(tips.render());

>>>>>>> dbfb9e1b43d23e33ed20c63f0ae93904ae687646
    }
}
