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



    public static Result addTask(){
        Form<Task> taskForm = Form.form(Task.class).bindFromRequest();
        Task task= taskForm.get();
        task.save();
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

        return redirect(controllers.routes.Application.task());

    }



}
