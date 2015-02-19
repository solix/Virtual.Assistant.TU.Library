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

import controllers.routes;


public class Application extends Controller {


    public static Result reroute(){
        String route = session("callback");
        if (route != null) {
            return redirect(route);
        }
        return Application.index();
    }

    /**
     * index view
     *
     * @return
     */
    public static Result index() {

        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null)
            return ok(index.render("Welcome, " + user.name, user));
        else {
            session().put("callback", routes.Application.index().absoluteURL(request()));
            return Authentication.login();
        }
    }





    public static Result project() {
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            return ok(project.render("AssistU - Projects", user, null));
        }else{
            session().put("callback", routes.Application.project().absoluteURL(request()));
            return Authentication.login();
        }
    }

    /**
     * chat page
     * @return
     */
    public static Result discussion() {

        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if (user != null)
            return ok(discussion.render("AssisTU - Discussions", user, null));
        else {
            session().put("callback", routes.Application.discussion().absoluteURL(request()));
            return Authentication.login();
        }
    }

    /**
     *Task view
     *
     * @return
     */
    private static Form<Task> taskForm = Form.form(Task.class);

    public static Result task() {

        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            List<Task> tasks = Task.ordered(user) ;
            return ok(task.render("My tasks", user, tasks,taskForm));
        }else {
            session().put("callback", routes.Application.task().absoluteURL(request()));
            return Authentication.login();
        }
    }

    /**
     * suggestion page
     * @return
     */
    public static Result suggestions() {

        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null)
            return ok(suggestions.render("Suggestions", user));
        else {
            session().put("callback", routes.Application.suggestions().absoluteURL(request()));
            return Authentication.login();
        }
    }

    public static Boolean allowedTitleRegex(String input){
        final String regex = "([a-zA-Z]+)( {1}([a-zA-Z0-9]+))*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    public static Boolean allowedNameRegex(String input){
        final String regex = "([a-zA-Z]+)(-{1}([a-zA-Z]+))*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.matches();
    }

}
