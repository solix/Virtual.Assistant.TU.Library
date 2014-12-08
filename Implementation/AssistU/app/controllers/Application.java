package controllers;


import models.DocumentFile;
import models.*;

import play.Logger;
import play.data.*;
import play.mvc.*;
import views.html.*;
import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData.*;
import java.io.File;
import java.lang.String;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;


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
        return ok(project.render(
                "My Projects",
                Project.find.all(),
                projectForm,
                DocumentFile.find.all()));
    }

    static Form<Project> projectForm = Form.form(Project.class);

    public static Result createNewProject() {
        Form<Project> filled=projectForm.bindFromRequest();
        if(filled.hasErrors()) {
            return badRequest("The form had errors. Need to implement in-style vaildation");
        } else {
            Project projectfilled = filled.get();
            Project.create(projectfilled.tabname, projectfilled.name, projectfilled.description);
            Logger.info("Created Project: " + projectfilled.name);
            return redirect(routes.Application.project());
        }
    }

    public static Result deleteProject(Long id) {
        Project.find.ref(id).delete();
        Logger.info("Deleted Project " + id);
        return redirect(routes.Application.project());
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
