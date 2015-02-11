package controllers;

import models.*;
import play.Logger;
import play.data.Form;
import play.mvc.*;
import views.html.*;
import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.user.AuthUser;
import com.feth.play.module.pa.user.AuthUserIdentity;
import com.feth.play.module.pa.user.EmailIdentity;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    private static Form<Task> taskForm = Form.form(Task.class);

    public static Result task() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            List<Task> tasks = Task.ordered(user) ;
            return ok(task_new.render("My tasks", user, tasks,taskForm));
        }else
            return Authentication.login();
    }



    public static Result project() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null)
            return ok(project.render("AssistU - Projects", user, null));
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
            return ok(discussion.render("AssisTU - Discussions", user, null));
        else
            return Authentication.login();
    }

    public static Boolean AllowedTitleRegex(String input){
        final String regex = "([a-zA-Z]+)( {1}([a-zA-Z0-9]+))*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    public static Boolean AllowedNameRegex(String input){
        final String regex = "([a-zA-Z]+)( {1}([A-Z][a-zA-Z]+))";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.matches();
    }

}
