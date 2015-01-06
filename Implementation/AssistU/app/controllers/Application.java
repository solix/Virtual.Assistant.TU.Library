package controllers;

import models.*;
import play.mvc.*;
import views.html.*;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;

import java.util.List;


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
    public static Result task() {
        List<Task> tasks = Task.find.all();

        return ok(task.render("your tasks",tasks));

    }



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
            String email = User.find.where().eq("socialId", authUser.getId()).eq("socialKey", authUser.getProvider()).findUnique().email;
//            AuthUserIdentity authIdentity = (AuthUserIdentity)authUser;
//            EmailIdentity emailIdentity = (EmailIdentity)authIdentity;
            return ok(project.render("My Projects", email));
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
