package controllers;

import models.*;
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
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null)
            return ok(index.render("Welcome, " + person.name, person));
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
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null) {
            List<Task> tasks = Task.ordered(person) ;
            return ok(task_new.render("My tasks", person, tasks,taskForm));
        }else
            return Authentication.login();
    }



    public static Result project() {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null)
            return ok(project.render("AssistU - Projects", person, null));
        else
            return Authentication.login();
    }

    /**
     * suggestion page
     * @return
     */
    public static Result suggestions() {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null)
            return ok(suggestions.render("Suggestions", person));
        else
            return Authentication.login();
    }

    /**
     * chat page
     * @return
     */
    public static Result discussion() {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null)
            return ok(discussion.render("AssisTU - Discussions", person, null));
        else
            return Authentication.login();
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
