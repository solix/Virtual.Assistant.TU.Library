package controllers;

import models.*;
import play.data.*;
import play.mvc.*;
import java.lang.String;

/**
 * Created by arnaud on 13-12-14.
 */
public class ProjectData extends Controller {

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

    public static Result archiveProject(String uid, Long pid) {
        Project.archive(pid);
        return redirect(routes.Application.project());
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
    public static Result removeMemberFromProject(String uid, Long pid){
        Project.removeMemberFrom(pid, uid);
        return redirect(routes.Application.project());
    }
}
