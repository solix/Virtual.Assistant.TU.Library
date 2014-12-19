package controllers;

import models.*;
import play.mvc.*;
import views.html.*;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;



public class Application extends Controller {

    /**
     * index view
     *
     * @return
     */
    public static Result index() {

        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        return ok(index.render("welcome name", user));
    }

    /**
     *Task view
     *
     * @return
     */
    public static Result task() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));

        return ok(task.render("your tasks", user));}



    /**
     * Calendar page
     * @return
     */
    public static Result calendar() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));

        return ok(calendar.render("My Calendar", user));}

    /**
     * TODO: Unify the plugin/regular style login
     * @return project page
     */
    public static Result project() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        return ok(project.render("My Projects", user.id, user));
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
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));

        return ok(discussion.render("gap", user));
    }

}
