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
        AuthUser authUser = PlayAuthenticate.getUser(session());
        if(authUser != null) {
            String uid = User.find.where().eq("socialId", authUser.getId()).eq("socialKey", authUser.getProvider()).findUnique().id.toString();
//            AuthUserIdentity authIdentity = (AuthUserIdentity)authUser;
//            EmailIdentity emailIdentity = (EmailIdentity)authIdentity;
            return ok(index.render("welcome " + User.find.ref(uid).name, uid));
        }else{
            User user = User.find.ref(session().get("email"));
            return ok(index.render("welcome " + user.name, user.id.toString()));
        }

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
            String uid = User.find.where().eq("socialId", authUser.getId()).eq("socialKey", authUser.getProvider()).findUnique().id.toString();
//            AuthUserIdentity authIdentity = (AuthUserIdentity)authUser;
//            EmailIdentity emailIdentity = (EmailIdentity)authIdentity;
            return ok(project.render("My Projects", uid));
        }else{
            User user = User.find.ref(session().get("email"));
            return ok(project.render("My Projects", user.id.toString()));
        }
    }

    /**
     * suggestion page
     * @return
     */
    public static Result suggestions() {

        return ok(suggestions.render("Suggestions"));
    }

    /**
     * chat page
     * @return
     */
    public static Result discussion() {

        return ok(discussion.render("gap"));
    }

}
