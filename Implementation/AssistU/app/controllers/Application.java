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
    public static Result task() {
        AuthUser authUser = PlayAuthenticate.getUser(session());
        if(authUser != null) {
            String uid = User.find.where().eq("socialId", authUser.getId()).eq("socialKey", authUser.getProvider()).findUnique().id.toString();
            return ok(task.render("My Tasks", uid));
        }else{
            User user = User.find.ref(session().get("email"));
            return ok(task.render("My Tasks", user.id.toString()));
        }
    }



    /**
     * Calendar page
     * @return
     */
    public static Result calendar() {
        AuthUser authUser = PlayAuthenticate.getUser(session());
        if(authUser != null) {
            String uid = User.find.where().eq("socialId", authUser.getId()).eq("socialKey", authUser.getProvider()).findUnique().id.toString();
            return ok(calendar.render("My Calendar", uid));
        }else{
            User user = User.find.ref(session().get("email"));
            return ok(calendar.render("My Calendar", user.id.toString()));
        }
    }

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
        AuthUser authUser = PlayAuthenticate.getUser(session());
        if(authUser != null) {
            String uid = User.find.where().eq("socialId", authUser.getId()).eq("socialKey", authUser.getProvider()).findUnique().id.toString();
            return ok(suggestions.render("Suggestions", uid));
        }else{
            User user = User.find.ref(session().get("email"));
            return ok(suggestions.render("Suggestions", user.id.toString()));
        }
    }

    /**
     * chat page
     * @return
     */
    public static Result discussion() {
        AuthUser authUser = PlayAuthenticate.getUser(session());
        if(authUser != null) {
            String uid = User.find.where().eq("socialId", authUser.getId()).eq("socialKey", authUser.getProvider()).findUnique().id.toString();
            return ok(discussion.render("My Discussions", uid));
        }else{
            User user = User.find.ref(session().get("email"));
            return ok(discussion.render("My Discussions", user.id.toString()));
        }
    }

}
