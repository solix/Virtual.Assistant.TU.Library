package controllers;



import models.*;
import play.Logger;
import play.data.*;
import play.mvc.*;
import views.html.*;
import java.lang.String;
import java.util.List;


public class Application extends Controller {

    /**
     * TEMPORARY USER CREATION
     */
    public static User getUser(String uid){
        User user = User.find.byId(uid);
        if(user == null){
            User newUser = new User(uid, "sushi");
            newUser.save();
            return newUser;
        }else{
            return user;
        }
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
        return ok(project.render("My Projects", "arnaud@assistu.nl", Project.find.where().eq("active", "true").findList(), DocumentFile.find.all()));
    }

    static Form<Project> projectForm = Form.form(Project.class);

    public static Result createProject(String uid) {
//        User.find.ref(uid);
        Form<Project> filledProjectForm = projectForm.bindFromRequest();
        if(filledProjectForm.hasErrors()) {
            return badRequest("The form had errors. Need to implement in-style vaildation");
        } else {
            Project projectData = filledProjectForm.get();
            Project.create(projectData.folder, projectData.name, uid);
//            Logger.info("Created Project: " + projectData.name);
            return redirect(routes.Application.project());
        }
//        return TODO;
    }

    public static Result archiveProject(String uid, Long pid) {
        Project toArchive = Project.find.ref(pid);
        Logger.info("In archive");
        if (toArchive.userlist.contains(User.find.byId(uid)) && toArchive.userlist.size() == 1){
            toArchive.archive(pid);
        } else {
            flash("failure", "You are not the single owner of this project");
        }
       return redirect(routes.Application.project());
    }

    public static Result editProject(Long pid) {
        Form<Project> filledProjectForm = projectForm.bindFromRequest();
        if(filledProjectForm.hasErrors()) {
            return badRequest("The form had errors. Need to implement in-style validation");
        } else {
            Project.edit(pid, filledProjectForm.get().folder, filledProjectForm.get().name);
            return redirect(routes.Application.project());

        }

    }

    /**
     * TODO: Need to add third Role parameter
     * @param uid
     * @param pid
     * @return
     */
    public static Result addMemberToProjectAs(String uid, Long pid){
        Project.find.byId(pid).addMemberAs(uid);
        return project();
    }

    /**
     * This method removes a user from the project's userlist
     */
    public static Result leaveProject(String uid, Long pid){
        Project.find.byId(pid).removeMember(uid);
        return project();
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
