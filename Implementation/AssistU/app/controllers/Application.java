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
      //  if(authUser != null) {
            Long id = User.find.where().eq("socialId", authUser.getId()).eq("socialKey", authUser.getProvider()).findUnique().id;
//            AuthUserIdentity authIdentity = (AuthUserIdentity)authUser;
//            EmailIdentity emailIdentity = (EmailIdentity)authIdentity;
            return ok(project.render("My Projects", id));
       // }
//        else{
//           // User user = User.find.ref(session().get("id"));
//            return ok(project.render("My Projects", user.id));
//        }
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
