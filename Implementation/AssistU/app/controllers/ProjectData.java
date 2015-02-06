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
     * TODO: Adapt this function after login is implemented and the current user is known
     * This function creates a new Project initiated by a user that automatically becomes its owner.
     * @return
     */
    public static Result createProject() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Form<Project> filledProjectForm = projectForm.bindFromRequest();
        if(filledProjectForm.hasErrors()) {
            return badRequest(projectNew.render("Something went wrong", filledProjectForm, true, "The input did not fulfill the requirements, please review your information.", user));
        } else {
            Project projectData = filledProjectForm.get();
            Project project = Project.create(projectData.folder, projectData.name, user.id, projectData.description, projectData.template);
            Role role=Role.ownerRole(user.id);
            user.roles.add(role);
            addRoleToDictionary(user.id,project.id,role);
            user.update();
            Event.defaultPlanningArticle(user, project);
            return redirect(routes.Application.project());
        }
    }

    /**
     * TODO: See if there is a way to only pass values that need to be changed (Map?)
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
            return badRequest(projectEdit.render("Something went wrong", p, filledProjectForm, true, "The input did not fulfill the requirements, please review your information.", user));
        } else {

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
     * TODO: Need to add third Role parameter
     * This function catches the Dynamicform from the template with the ID of the User that needs to
     * be added to the project of which the Project ID has been passed
     * @param pid: The project to which the user in the form has to be assigned
     * @return
     */
    public static Result addMemberToProjectAs(Long pid){
        DynamicForm emailform = Form.form().bindFromRequest();
        User user = User.find.where().eq("email", emailform.get("email")).findUnique();
        Project.addMember(pid, user.id);
        if(emailform.get("role").equals("Owner")) {
            Project p=Project.find.byId(pid);
            Role role=Role.ownerRole(user.id);
            user.roles.add(role);
            addRoleToDictionary(user.id, pid, role);
            Event.defaultPlanningArticle(user, p);
        } else if(emailform.get("role").equals("Reviewer")) {
            Role role=Role.reviewerRole(user.id);
            user.roles.add(role);
            addRoleToDictionary(user.id, pid, role);
        }else{
            Role role=Role.guestRole(user.id);
            user.roles.add(role);
            addRoleToDictionary(user.id, pid, role);
        }
        user.update();
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
        removeMemberFromDictionary(uid, pid);
        return redirect(routes.Application.project());
    }

    public static Result getProjectIdsAsJson(Long uid){
        List<Project> projects = Project.find.where().in("users", User.find.byId(uid)).eq("active", "true").orderBy("dateCreated").findList();
        List<TreeMap<String, String>> result = new ArrayList<TreeMap<String, String>>();
        TreeMap<String, String> project;
//        List<Long> result = new ArrayList<Long>();
        for(int i =0; i < projects.size(); i++){
            project = new TreeMap<String, String>();
            project.put("name", projects.get(i).name);
            project.put("projectID", "" + projects.get(i).id);
            result.add(project);
        }
        return ok(toJson(result));
    }


    /**
     * This map plays as a helper to identify user and their roles within a project
     */
   private static Map<Long,HashMap<Long,Role>> projectScope=new HashMap<Long,HashMap<Long,Role>>();

    /**
     *  searches for a role of a specific user in the specific project
     * @param uid
     * @param pid
     * @return
     */
    private static Role roleFinder(long uid,long pid){
        HashMap<Long,Role> roleScope = projectScope.get(pid);
        return roleScope.get(uid);
    }

    /**
     * add a new user and his/her role to the Dictionary
     * @param uid
     * @param pid
     * @param role
     */
    private static void addRoleToDictionary(long uid,long pid, Role role ){
        HashMap<Long,Role> newRoleScope = new HashMap<Long,Role>();
        newRoleScope.put(uid,role);
        projectScope.put(pid,newRoleScope);

    }

    /**
     *
     * @param uid
     * @param pid
     */
    private static void removeMemberFromDictionary(long uid,long pid){
        HashMap<Long,Role> roleScope = projectScope.get(pid);
        roleScope.remove(uid);
    }

}
