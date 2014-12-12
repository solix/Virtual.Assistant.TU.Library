package controllers;



import com.avaje.ebean.Ebean;
import models.*;
//import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
//import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.*;
import play.Logger;
import play.data.*;
import play.mvc.*;
import views.html.*;
import java.lang.String;
import java.util.List;
import java.util.Set;


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
        User user= User.find.where().eq("email", "alex@gmail.com").findUnique();
        List<Project> projectList = Project.find.where().eq("users.email", user.email).eq("active", "true").findList();
        List<DocumentFile> documentList = DocumentFile.find.where().in("project.id", Project.find.where().eq("users.email", user.email).eq("active", "true").findIds()).findList();
        return ok(project.render("My Projects",
                user.email,
                projectList,
                documentList
               ));

    }

    static Form<Project> projectForm = Form.form(Project.class);

    public static Result createProject() {
        User user= User.find.where().eq("email", "alex@gmail.com").findUnique();
        Form<Project> filledProjectForm = projectForm.bindFromRequest();
        if(filledProjectForm.hasErrors()) {
            return badRequest("The form had errors. Need to implement in-style vaildation");
        } else {
            Project projectData = filledProjectForm.get();
            Project.create(projectData.folder, projectData.name, user.email , "dummy must be implemenetd");
            return redirect(routes.Application.project());
        }
//        return TODO;
    }

    public static Result archiveProject(String uid, Long pid) {
        Project toArchive = Project.find.ref(pid);
        Logger.info("In archive");
        if (toArchive.users.contains(User.find.byId(uid)) && toArchive.users.size() == 1){
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
     *
     * @param pid
     * @return
     */
    public static Result addMemberToProjectAs(Long pid){
        DynamicForm emailform = Form.form().bindFromRequest();
        Project.addMemberAs(pid, emailform.get("email"));
        return redirect(routes.Application.project());
    }

    /**
     * TODO: Only owners should be able to remove non-owners
     * @param uid
     * @param pid
     * @return
     */
    public static Result removeMemberFromProject(Long pid, String uid){
        Project.removeMemberFrom(pid, uid);
        return project();
    }

    /**
     * This method removes a user from the project's userlist USE REMOVEMEMBER INSTEAD
     */
//    public static Result leaveProject(String uid, Long pid){
////        Project.find.byId(pid).removeMember(uid);
//        return project();
//    }

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
