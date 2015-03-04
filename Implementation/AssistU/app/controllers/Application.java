package controllers;

import models.*;
import play.data.Form;
import views.html.*;
import plugins.com.feth.play.module.pa.PlayAuthenticate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;



public class Application extends Controller {



    /**
     * The reroute function checks if there is a link stored within the session
     * for the key 'callback'. When a user tries to perform an action or access
     * a page without having a valid session (i.e. he is not logged in), the url
     * gets saved in the session under this key. The login function then calls this
     * function to find out what user tried to access before.
     * @return Result
     */
    public static Result reroute(){
        String route = session("callback");
        if (route != null) {
            return redirect(route);
        }
        return Application.index();
    }

    /**
     * This function either shows the index page, or redirects the user to the login
     * page if he is not logged in.
     * @return Result
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

    /**
     * This function either shows the project page, or redirects the user to the login
     * page if he is not logged in.
     * @return Result
     */
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
     * This function either shows the discussion page, or redirects the user to the login
     * page if he is not logged in.
     * @return Result
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

    private static Form<Task> taskForm = Form.form(Task.class);

    /**
     * This function either shows the task page, or redirects the user to the login
     * page if he is not logged in.
     * @return Result
     */
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
     * This function either shows the suggestions page, or redirects the user to the login
     * page if he is not logged in.
     * @return Result
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

    /**
     * This function validates text intended for titles based on a static regex
     * @param input the input string that needs validating
     * @return a boolean that indicates a match
     */
    public static Boolean allowedTitleRegex(String input){
        final String regex = "([a-zA-Z0-9]+)( {1}([a-zA-Z0-9]+))*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.matches();
    }

    /**
     * This function validates text intended for names based on a static regex
     * @param input the input string that needs validating
     * @return a boolean that indicates a match
     */
    public static Boolean allowedNameRegex(String input){
        final String regex = "([a-zA-Z]+)(-{1}([a-zA-Z]+))*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        return m.matches();
    }


}
