package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.Logger;
import play.data.*;
import play.mvc.*;
import views.html.*;
import com.feth.play.module.pa.PlayAuthenticate;

import java.lang.String;
import java.util.*;

import static play.libs.Json.toJson;

/**
 * Created by arnaud on 13-12-14.
 */
public class ProjectData extends Controller {

    public static Result project(Long pid) {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            Project.updateLastAccessed(pid);
            Project p = Project.find.byId(pid);
            return ok(project.render("AssistU - Projects", user, p));
        }else
            return Authentication.login();
    }

    static Form<Project> projectForm = Form.form(Project.class);

    public static Result createProjectPage() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null)
            return ok(projectNew.render("Create a new Project", projectForm, false, "", user));
        else
            return Authentication.login();
    }

    public static Result editProjectPage(Long pid) {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Project p = Project.find.byId(pid);
        if(user != null)
            return ok(projectEdit.render("Edit Project " + p.name, p, projectForm, false, "", user));
        else
            return Authentication.login();
    }

    /**
     * This function creates a new Project initiated by a user that automatically becomes its owner.
     * @return
     */
    public static Result createProject() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Form<Project> filledProjectForm = projectForm.bindFromRequest();
        if(filledProjectForm.hasErrors()) {
            return badRequest(projectNew.render("Something went wrong", filledProjectForm, true, "The input did not fulfill the requirements, please review your information", user));
        } else if (!(Application.AllowedTitleRegex(filledProjectForm.get().folder)
                && Application.AllowedTitleRegex(filledProjectForm.get().name))) {
            return badRequest(projectNew.render("Something went wrong", filledProjectForm, true,
                    "The input did not have the allowed format, please review your information", user));
        } else {
            Project projectData = filledProjectForm.get();
            Project p = Project.create(projectData.folder, projectData.name, projectData.description, projectData.template);
            p.addOwner(p.id, user.id);
            if(!p.template.equals("None")){
                Event.defaultPlanningArticle(user, p);
                p.planning=true;
                p.save();
            }
            Emailer.sendNotifyEmail("[assistU] New project "+p.name+ "Created",user,views.html.email.projectCreated.render(user,p));
            return redirect(routes.Application.project());
        }
    }

    /**
     * This function edits a project with values catched from a form.
     * @param pid: The Project ID of the Project that needs it details edited
     * @return
     */
    public static Result editProject(Long pid) {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Project p = Project.find.byId(pid);
        Form<Project> filledProjectForm = projectForm.bindFromRequest();
        if(filledProjectForm.hasErrors()) {
            Logger.debug(filledProjectForm.errors().toString());
            return badRequest(projectEdit.render("Something went wrong", p, filledProjectForm, true, "The input did not fulfill the requirements, please review your information", user));
        } else if (!(Application.AllowedTitleRegex(filledProjectForm.get().folder)
                && Application.AllowedTitleRegex(filledProjectForm.get().name))) {
            return badRequest(projectNew.render("Something went wrong", filledProjectForm, true,
                    "The input did not have the allowed format, please review your information", user));
        }  else {
            Project.edit(pid, filledProjectForm.get().folder, filledProjectForm.get().name, filledProjectForm.get().description);
            return redirect(routes.Application.project());
        }

    }

    /**
     * This function refers to the model's archive function. The unused uid is to check whether the person is an
     * owner of the project at hand (future implementation)
     * @param uid: The User ID of the person requesting for archiving
     * @param pid: The Project ID of the project that is up for archiving
     * @return
     */
    public static Result archiveProject(Long uid, Long pid) {
        Project.archive(pid);
        return redirect(routes.Application.project());
    }

    /**
     * This function catches the Dynamicform from the template with the ID of the User that needs to
     * be added to the project of which the Project ID has been passed
     * @param pid: The project to which the user in the form has to be assigned
     * @return
     */
    public static Result addMemberToProjectAs(Long pid){
        DynamicForm emailform = Form.form().bindFromRequest();
        User user = User.find.where().eq("email", emailform.get("email")).findUnique();
        Project p=Project.find.byId(pid);
        if(emailform.get("role").equals("Owner")) {
            p.addOwner(p.id, user.id);
            Event.defaultPlanningArticle(user, p);
            p.planning=true;
            p.update();
        } else if(emailform.get("role").equals("Reviewer")) {
            p.addReviewer(p.id, user.id);
        }else{
            p.addGuest(p.id, user.id);
        }

        return redirect(routes.Application.project());
    }

    /**
     * TODO: Only owners should be able to remove non-owners
     * This function removes a user from a project. This function doubles
     * as removing someone and leaving yourself. In the latter case your
     * own User ID is passed.
     * @param uid: The ID of the User that is being removed
     * @param pid: The ID of the Project in which the User had to be removed from
     * @return
     */
    public static Result removeMemberFromProject(Long uid, Long pid){
        Project.removeMemberFrom(pid, uid);
        return redirect(routes.Application.project());
    }

    public static Result getProjectIdsAsJson(){
        List<Project> projects = UserData.findActiveProjects();
        List<TreeMap<String, String>> result = new ArrayList<TreeMap<String, String>>();
        TreeMap<String, String> project;
        for(int i =0; i < projects.size(); i++){
            project = new TreeMap<String, String>();
            project.put("name", projects.get(i).name);
            project.put("projectID", "" + projects.get(i).id);
            result.add(project);
        }
        return ok(toJson(result));
    }

    public static Result getLastAccessedProjectIdAsJson(){
        Project p = UserData.getLastUsedProject();
        HashMap<String, String> project = new HashMap<String, String>();
        if(p != null) {
            project.put("name", p.name);
            project.put("projectID", "" + p.id);
        }
//        Logger.debug("LAST ACCESSED AS JSON: " + toJson(project));
        return ok(toJson(project));
    }

}
