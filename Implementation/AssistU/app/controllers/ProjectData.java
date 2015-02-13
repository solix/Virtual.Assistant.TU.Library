package controllers;

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
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null) {
            Project.updateLastAccessed(pid);
            Project p = Project.find.byId(pid);
            return ok(project.render("AssistU - Projects", person, p));
        }else
            return Authentication.login();
    }

    static Form<Project> projectForm = Form.form(Project.class);

    public static Result createProjectPage() {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null)
            return ok(projectNew.render("Create a new Project", projectForm, false, "", "", person));
        else
            return Authentication.login();
    }

    public static Result editProjectPage(Long pid) {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Project p = Project.find.byId(pid);
        if(person != null)
            return ok(projectEdit.render("Edit Project " + p.name, p, projectForm, false, "", "", person));
        else
            return Authentication.login();
    }

    /**
     * This function creates a new Project initiated by a user that automatically becomes its owner.
     * @return
     */
    public static Result createProject() {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Form<Project> filledProjectForm = projectForm.bindFromRequest();
        if(filledProjectForm.hasErrors()) {
            return badRequest(projectNew.render("Something went wrong", filledProjectForm, true, "danger",
                    "The input did not fulfill the requirements, please review your information", person));
        } else if (!(Application.allowedTitleRegex(filledProjectForm.get().folder)
                && Application.allowedTitleRegex(filledProjectForm.get().name))) {
            return badRequest(projectNew.render("Something went wrong", filledProjectForm, true, "danger",
                    "The input did not have the allowed format, please review your information", person));
        } else {
            Project projectData = filledProjectForm.get();
            Project p = Project.create(projectData.folder, projectData.name, projectData.description, projectData.template);
            Project.inviteOwner(p.id, person.id);
            Role r = Role.find.where().eq("project",p).eq("person", person).findUnique();
            r.accepted=true;
            r.dateJoined=new Date();
            r.update();
            if(!p.template.equals("None")){
                Event.defaultPlanningArticle(person, p);
                p.planning=true;
                p.save();}
            return redirect(routes.ProjectData.project(p.id));
        }
    }

    /**
     * This function edits a project with values catched from a form.
     * @param pid: The Project ID of the Project that needs it details edited
     * @return
     */
    public static Result editProject(Long pid) {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(ProjectData.findAllOwners(pid).contains(person)) {
            Project p = Project.find.byId(pid);
            Form<Project> filledProjectForm = projectForm.bindFromRequest();
            if (filledProjectForm.hasErrors()) {
                Logger.debug(filledProjectForm.errors().toString());
                return badRequest(projectEdit.render("Something went wrong", p, filledProjectForm, true, "danger",
                        "The input did not fulfill the requirements, please review your information", person));
            } else if (!(Application.allowedTitleRegex(filledProjectForm.get().folder)
                    && Application.allowedTitleRegex(filledProjectForm.get().name))) {
                return badRequest(projectNew.render("Something went wrong", filledProjectForm, true, "danger",
                        "The input did not have the allowed format, please review your information", person));
            } else {
                Project.edit(pid, filledProjectForm.get().folder, filledProjectForm.get().name, filledProjectForm.get().description);
            }
        }
        return redirect(routes.ProjectData.project(pid));
    }

    /**
     * This function refers to the model's archive function. The unused uid is to check whether the person is an
     * owner of the project at hand (future implementation)
     * @param uid: The User ID of the person requesting for archiving
     * @param pid: The Project ID of the project that is up for archiving
     * @return
     */
    public static Result archiveProject(Long pid) {
        Person u = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(ProjectData.findAllOwners(pid).contains(u) && ProjectData.findAllOwners(pid).size() == 1) {
            Project.archive(pid);
        }
        return redirect(routes.Application.project());
    }

    /*TODO SOHEIL: When a member gets invited, send him a message
    /**
     * This function catches the Dynamicform from the template with the ID of the User that needs to
     * be added to the project of which the Project ID has been passed
     * @param pid: The project to which the user in the form has to be assigned
     * @return
     */
    public static Result inviteMemberToProjectAs(Long pid){
        DynamicForm emailform = Form.form().bindFromRequest();
        Person u = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        //See if the user requesting to invite someone is an Owner
        if(ProjectData.findAllOwners(pid).contains(u)) {
            Person person = Person.find.where().eq("email", emailform.get("email")).findUnique();
            //See if the user that needs to be invited exists in the system
            if(person != null) {
                Project p = Project.find.byId(pid);
                //There can not be a role relation between the invited user and the project,
                // as it would be the user is already a member
                if (Role.find.where().eq("project", p).eq("person", person).findUnique() == null) {
                    //Pattern match the correct role for invitation
                    if (emailform.get("role").equals("Owner")) {
                        p.inviteOwner(p.id, person.id);
                        Event.defaultPlanningArticle(person, p);
                        p.planning = true;
                        //            p.update();
                        p.save();
                    } else if (emailform.get("role").equals("Reviewer")) {
                        p.inviteReviewer(p.id, person.id);
                    } else {
                        p.inviteGuest(p.id, person.id);
                    }
                }
            }
        }
        return redirect(routes.Application.project());
    }

    /*TODO SOHEIL: This function can send a message to all owners that a new user has joined the project
    TODO: (I think guest and reviewer should not be bothered with this, you can use findAllOwners function for recipients)
     */
    public static Result hasAccepted(Long pid){
        Project p = Project.find.byId(pid);
        Person u = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Role r = Role.find.where().eq("person", u).eq("project", p).findUnique();
        //See if there actually exists a (pending) role between the accepting user and project
        if(r != null){
            r.accepted=true;
            r.dateJoined = new Date();
            r.save();
        }
        return redirect(routes.ProjectData.project(p.id));
    }

    public static Result hasDeclined(Long pid){
        Project p = Project.find.byId(pid);
        Person u = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Role r = Role.find.where().eq("person", u).eq("project", p).findUnique();
        if(r != null) {
            r.delete();
        }
        return redirect(routes.Application.project());
    }

    /**
     * This function removes a user from a project. This function doubles
     * as removing someone and leaving yourself. In the latter case your
     * own User ID is passed.
     * @param uid: The ID of the User that is being removed
     * @param pid: The ID of the Project in which the User had to be removed from
     * @return
     */
    public static Result removeMemberFromProject(Long uid, Long pid){
        Person u = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(ProjectData.findAllOwners(pid).contains(u)) {
            Project.removeMemberFrom(pid, uid);
        }
        return redirect(routes.Application.project());
    }

    public static Result leaveProject(Long pid){
        Person u = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Project.removeMemberFrom(pid, u.id);
        return redirect(routes.Application.project());
    }

    public static Result getProjectIdsAsJson(){
        List<Project> projects = PersonData.findActiveProjects();
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

    public static Result getOwnerIdsAsJson(){
        List<Project> projects = PersonData.findActiveProjects();
        List<Long> result = new ArrayList<Long>();
        for(int i =0 ; i < projects.size(); i++){
            List<Person> projectowners = ProjectData.findAllOwners(projects.get(i).id);
            for(int j = 0; j < projectowners.size(); j++){
                if(!result.contains(projectowners.get(j).id)){
                    result.add(projectowners.get(j).id);
                }
            }
        }
        return ok(toJson(result));
    }

    public static Result getReviewerIdsAsJson(){
        List<Project> projects = PersonData.findActiveProjects();
        List<Long> result = new ArrayList<Long>();
        for(int i =0 ; i < projects.size(); i++){
            List<Person> projectreviewers = ProjectData.findAllReviewers(projects.get(i).id);
            for(int j = 0; j < projectreviewers.size(); j++){
                if(!result.contains(projectreviewers.get(j).id)){
                    result.add(projectreviewers.get(j).id);
                }
            }
        }
        return ok(toJson(result));
    }

    public static Result getGuestIdsAsJson(){
        List<Project> projects = PersonData.findActiveProjects();
        List<Long> result = new ArrayList<Long>();
        for(int i =0 ; i < projects.size(); i++){
            List<Person> projectguests = ProjectData.findAllReviewers(projects.get(i).id);
            for(int j = 0; j < projectguests.size(); j++){
                if(!result.contains(projectguests.get(j).id)){
                    result.add(projectguests.get(j).id);
                }
            }
        }
        return ok(toJson(result));
    }

    public static Result getLastAccessedProjectIdAsJson(){
        Project p = PersonData.getLastUsedProject();
        HashMap<String, String> project = new HashMap<String, String>();
        if(p != null) {
            project.put("name", p.name);
            project.put("projectID", "" + p.id);
        }
//        Logger.debug("LAST ACCESSED AS JSON: " + toJson(project));
        return ok(toJson(project));
    }

    public static List<Person> findAllAffiliatedUsers(Long pid){
        Project p = Project.find.byId(pid);
        List<Role> roles = Role.find.where().eq("project", p).findList();
        List<Person> persons = new ArrayList<Person>();
        for(Role role: roles){
            persons.add(role.person);
        }
        return persons;
    }

    public static List<Person> findAllOwners(Long pid){
        Project p = Project.find.byId(pid);
        List<Role> roles = Role.find.where().eq("project", p).eq("role", Role.OWNER).findList();
        List<Person> persons = new ArrayList<Person>();
        for(Role role: roles){
            persons.add(role.person);
        }
        return persons;
    }

    public static List<Person> findAllReviewers(Long pid){
        Project p = Project.find.byId(pid);
        List<Role> roles = Role.find.where().eq("project", p).eq("role", Role.REVIEWER).findList();
        List<Person> persons = new ArrayList<Person>();
        for(Role role: roles){
            persons.add(role.person);
        }
        return persons;
    }

}
