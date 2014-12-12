package controllers;



import models.*;
import play.data.*;
import play.mvc.*;
import views.html.*;
import java.lang.String;
import java.util.List;


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
        return TODO;
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
        return ok(project.render("My Projects", "TODO", Project.find.all(), DocumentFile.find.all()));
    }

    static Form<Project> projectForm = Form.form(Project.class);

    public static Result createProject(String uid) {
//        User.find.ref(uid);
        Form<Project> filledProjectForm = projectForm.bindFromRequest();
        if(filledProjectForm.hasErrors()) {
            return badRequest("The form had errors. Need to implement in-style vaildation");
        } else {
            Project projectData = filledProjectForm.get();
            Project.create(projectData.folder, projectData.name, uid , "dummy must be implemenetd");
//            Logger.info("Created Project: " + projectData.name);
            return redirect(routes.Application.project());
        }
//        return TODO;
    }

    public static Result archiveProject() {
       // Project.find.ref(id).archive();
       // return redirect(routes.Application.project());
        return TODO;
    }

    public static Result editProject(Long pid) {
        Form<Project> filledProjectForm = projectForm.bindFromRequest();
        if(filledProjectForm.hasErrors()) {
            return badRequest("The form had errors. Need to implement in-style validation");
        } else {
//            Project projectData = filledProjectForm.get();
//            Project current = Project.find.ref(pid);
//            current.update(projectData.folder, projectData.name);
//            return redirect(routes.Application.project());
        return TODO;
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
