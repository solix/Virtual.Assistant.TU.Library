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
import java.util.List;

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

    static Form<User> emptyRegisterForm = Form.form(User.class);

    public static Result register() {
        return ok(register.render(emptyRegisterForm, false));
    }

    public static Result createUser() {
        Form<User> filledRegisterForm = emptyRegisterForm.bindFromRequest();
        if(filledRegisterForm.hasErrors()) {
            return badRequest(register.render(filledRegisterForm, true));
        } else {
            return ok(index.render("Welcome name"));
        }
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
        List<Project> AllProjects = Project.find.all();
        if (AllProjects.size() > 0) {
//            Find the latest active project, for now just the last in the list
            return showProject((AllProjects.get(AllProjects.size()-1)).id);
        } else {
            return ok(project.render(
                    "My Projects",
                    null,
                    Project.find.all(),
                    emptyProjectForm,
                    emptyProjectForm,
                    DocumentFile.find.all()));
        }
    }

    public static Result showProject(Long id) {
        Project ProjectToBeDisplayed = Project.find.ref(id);
        return ok(project.render(
                ProjectToBeDisplayed.name,
                ProjectToBeDisplayed,
                Project.find.all(),
                emptyProjectForm,
                emptyProjectForm.fill(ProjectToBeDisplayed),
                DocumentFile.find.all()));
    }

    static Form<Project> emptyProjectForm = Form.form(Project.class);

    public static Result createProject() {
        Form<Project> filledProjectForm = emptyProjectForm.bindFromRequest();
        if(filledProjectForm.hasErrors()) {
            return badRequest("The form had errors. Need to implement in-style vaildation");
        } else {
            Project projectData = filledProjectForm.get();
            Project.create(projectData.tabname, projectData.name, projectData.description);
            Logger.info("Created Project: " + projectData.name);
            return redirect(routes.Application.project());
        }
    }

    public static Result deleteProject(Long id) {
        Project.find.ref(id).delete();
        Logger.info("Deleted Project " + id);
        return redirect(routes.Application.project());
    }

    public static Result editProject(Long id) {
        Form<Project> filledProjectForm = emptyProjectForm.bindFromRequest();
        if(filledProjectForm.hasErrors()) {
            return badRequest("The form had errors. Need to implement in-style validation");
        } else {
            Project projectData = filledProjectForm.get();
            Project current = Project.find.ref(id);
            Logger.info("\nUpdating Project:\n" + current + "\nto\n" + projectData + "\n");
            current.update(projectData.tabname, projectData.name, projectData.description);
            Logger.info("\nUpdated:\n" + current + "\n");
            return redirect(routes.Application.showProject(id));
        }

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
