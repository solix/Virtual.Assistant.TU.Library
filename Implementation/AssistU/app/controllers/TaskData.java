package controllers;

import models.*;
import play.db.ebean.Model;
import play.mvc.*;
import views.html.*;
import play.data.Form;

import java.util.List;

import static play.libs.Json.toJson;

/**
 * This controller will handle user input task list
 */
public class TaskData extends Controller {

private static final Form<Task> tForm = Form.form(Task.class);

    public static Result addTask(){
        Form<Task> taskForm = tForm.bindFromRequest();
        if(taskForm.hasErrors()) {
            flash("error", "Please correct the form below.");
            return badRequest(views.html.task.render("My tasks", null, Task.find.all(),taskForm)
            ); }
        Task task= taskForm.get();
        task.save();
        flash("success", String.format("Successfully added task %s", task));
        return redirect(controllers.routes.Application.task());
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
