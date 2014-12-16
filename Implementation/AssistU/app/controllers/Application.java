package controllers;



import com.avaje.ebean.Ebean;
import models.*;
import org.apache.commons.io.*;
import play.Logger;
import play.data.*;
import play.mvc.*;
import views.html.*;
import controllers.*;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
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
     * TODO: Unify the plugin/regular style login
     * @return project page
     */

    public static Result project() {
        AuthUser authUser = PlayAuthenticate.getUser(session());
        if(authUser != null) {
            User user = User.find.where().eq("socialId", authUser.getId()).eq("socialKey", authUser.getProvider()).findUnique();
            return ok(project.render("My Projects", user.email));
        }else{
            User user = User.find.ref(session().get("email"));
            return ok(project.render("My Projects", user.email));
        }
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
