package controllers;



import com.avaje.ebean.Ebean;
import models.*;
import org.apache.commons.io.*;
import play.Logger;
import play.data.*;
import play.mvc.*;
import views.html.*;
import java.lang.String;
import java.util.List;
import java.util.Set;


public class Application extends Controller {

    /**
     * index view
     *
     * @return
     */
    public static Result index() {
        return ok(index.render("welcome name"));
    }

    /**
     *Task view
     *
     * @return
     */
    public static Result task() {return ok(task.render("your tasks"));}



    /**
     * Calendar page
     * @return
     */
    public static Result calendar() {return ok(calendar.render("My Calendar"));}

    /**
     *
     * @return project page
     */

    public static Result project() {
        User user= User.find.where().eq("email", "alex@gmail.com").findUnique();
        return ok(project.render("My Projects", user.email));
    }
    
    /**
     * suggestion page
     * @return
     */
    public static Result suggestions() {

        return TODO;
    }

    /**
     * chat page
     * @return
     */
    public static Result discussion() {

        return ok(discussion.render("gap"));
    }

}
