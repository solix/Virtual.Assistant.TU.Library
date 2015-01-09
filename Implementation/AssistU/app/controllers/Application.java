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
        if(user != null)
            return ok(index.render("Welcome, " + user.name, user));
        else
            return Authentication.login();
    }

    /**
     *Task view
     *
     * @return
     */
    public static Result task() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null)
            return ok(task.render("My tasks", user));
        else
            return Authentication.login();
    }

    /**
     * Calendar page
     * @return
     */
    public static Result calendar() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null)
            return ok(calendar.render("My Calendar", user));
        else
            return Authentication.login();
    }

    /**
     * TODO: Unify the plugin/regular style login
     * @return project page
     */
    public static Result project() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null)
            return ok(project.render("My Projects (alternate)", user));
        else
            return Authentication.login();
    }

    /**
     * suggestion page
     * @return
     */
    public static Result suggestions() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null)
            return ok(suggestions.render("Suggestions", user));
        else
            return Authentication.login();
    }

    /**
     * chat page
     * @return
     */
    public static Result discussion() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null)
            return ok(discussion.render("My Discussions", user));
        else
            return Authentication.login();
    }

}
