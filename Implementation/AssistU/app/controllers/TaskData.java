package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import models.*;
import play.mvc.*;
import play.data.Form;

import java.util.List;

import static play.libs.Json.toJson;

/**
 * This controller will handle user input task list
 */
public class TaskData extends Controller {

private static final Form<Task> tForm = Form.form(Task.class);


    /**
     * adds a new task and saves it to DB
     * @return
     */
    public static Result addTask(){
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            Form<Task> taskForm = tForm.bindFromRequest();
            if (taskForm.hasErrors()) {
                flash("error", "Please correct the form below.");
                return badRequest(views.html.task_new.render("My tasks", user, Task.alltask(user), taskForm)
                );
            }
            Task taskdata = taskForm.get();
            Task task = Task.createTask(taskdata, user);
            task.save();
            flash("success", String.format("Successfully added task %s", task));
            return redirect(controllers.routes.Application.task());
        } else {
            //User did not have a session
            session().put("callback", routes.TaskData.addTask().absoluteURL(request()));
            return Authentication.login();
        }
    }

    public static Result getTasks(){
        List<Task> tasks= Task.find.all();

        return ok(toJson(tasks));
    }

    public static Result deleteTask(long id){

        Task t = Task.find.byId(id);
        t.delete();

        return redirect(controllers.routes.Application.task());

    }

    public static Result doneTask(long id){

        Task t = Task.find.byId(id);
        t.done=true;
        t.save();

        return redirect(controllers.routes.Application.task());

    }

    public static Result undoTask(long id){

        Task t = Task.find.byId(id);
        t.done=false;
        t.save();

        return redirect(controllers.routes.Application.task());

    }



}
