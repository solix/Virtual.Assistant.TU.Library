package controllers;


import models.Project;

import play.data.*;
import play.mvc.*;
import views.html.*;


public class Application extends Controller {

    static Form<Project> projectForm = Form.form(Project.class);

    public static Result createNewProject() {
        Form<Project> filled=projectForm.bindFromRequest();

            Project projectfilled = filled.get();
            //project.save();
            Project thesis = Project.create(projectfilled.folder);
            return redirect(routes.Application.project());

    }


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
    public static Result task() {

        return ok(task.render("your tasks"));
    }

    /**
     * login page
     *
     * @return
     */
    public static Result login() {
        return ok(login.render());
    }

    /**
     * Calendar page
     * @return
     */
    public static Result calendar() {

        return ok(calendar.render("My Calendar"));
    }

    /**
     * project page
     * @return
     */

    public static Result project() {


        return ok(project.render("My Projects", Project.find.all(),projectForm));
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
