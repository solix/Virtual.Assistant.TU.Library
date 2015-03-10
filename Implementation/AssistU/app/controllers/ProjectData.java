package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import models.*;
import play.Logger;
import play.data.*;
import play.mvc.*;
import views.html.*;
import plugins.com.feth.play.module.pa.PlayAuthenticate;

import java.lang.String;
import java.util.*;

import static play.libs.Json.toJson;

import controllers.routes;


public class ProjectData extends Controller {

    /**
     * This function render the page for a specific project
     * @param pid: project ID
     * @return Result
     */
    public static Result project(Long pid) {
        session().put("callback", routes.ProjectData.project(pid).absoluteURL(request()));
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            Project p = Project.find.byId(pid);
            return ok(project.render("AssistU - Projects", user, p));
        } else {
            session().put("callback", routes.ProjectData.project(pid).absoluteURL(request()));
            return Authentication.login();
        }
    }

    static Form<Project> projectForm = Form.form(Project.class);

    /**
     * Renders the page for the creation of a new project
     * @return Result
     */
    public static Result createProjectPage() {
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null)
            return ok(projectNew.render("Create a new Project", projectForm, false, "", "", user));
        else {
            session().put("callback", routes.ProjectData.createProjectPage().absoluteURL(request()));
            return Authentication.login();
        }
    }

    /**
     * Renders the page for editing a project
     * @param pid: project ID for the project to be edited
     * @return Result
     */
    public static Result editProjectPage(Long pid) {
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Project p = Project.find.byId(pid);
        if(user != null)
            return ok(projectEdit.render("Edit Project " + p.name, p, projectForm.fill(p), false, "", "", user));
        else {
            session().put("callback", routes.ProjectData.editProjectPage(pid).absoluteURL(request()));
            return Authentication.login();
        }
    }

    /**
     * This function creates a new Project initiated by a user that automatically becomes its owner.
     * @return Result
     */
    public static Result createProject() {
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Form<Project> filledProjectForm = projectForm.bindFromRequest();
        if (user != null) {
            if (filledProjectForm.hasErrors()) {
                return badRequest(projectNew.render("Something went wrong", filledProjectForm, true, "danger",
                        "The input did not fulfill the requirements, please review your information", user));
            } else if (!(Application.allowedTitleRegex(filledProjectForm.get().folder)
                    && Application.allowedTitleRegex(filledProjectForm.get().name))) {
                return badRequest(projectNew.render("Something went wrong", filledProjectForm, true, "danger",
                        "The input did not have the allowed format, please review your information", user));
            } else {
                Project projectData = filledProjectForm.get();
                Project p = Project.create(projectData.folder, projectData.name, projectData.description, projectData.template);
                Project.inviteOwner(p.id, user.id);
                Role r = Role.find.where().eq("project", p).eq("person", user).findUnique();
                r.accepted = true;
                r.dateJoined = new Date();
                r.update();
                if (!p.template.equals("None")) {
                    Event.defaultPlanningArticle(user, p);
                    p.planning = true;
                    p.save();
                }
                Emailer.sendNotifyEmail("[assistU] New project "+p.name+ "Created",user,views.html.email.projectCreated.render(user,p));





                return redirect(routes.ProjectData.project(p.id));
            }

        }
        //User did not have a session
        session().put("callback", routes.ProjectData.createProject().absoluteURL(request()));
        return Authentication.login();
    }

    /**
     * This function edits a project with values catched from a form.
     * @param pid: The Project ID of the Project that needs it details edited
     * @return Result
     */
    public static Result editProject(Long pid) {
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if (user != null) {
            if (ProjectData.findAllOwners(pid).contains(user)) {
                Project p = Project.find.byId(pid);
                Form<Project> filledProjectForm = projectForm.bindFromRequest();
                if (filledProjectForm.hasErrors()) {
                    Logger.debug(filledProjectForm.errors().toString());
                    return badRequest(projectEdit.render("Something went wrong", p, filledProjectForm, true, "danger",
                            "The input did not fulfill the requirements, please review your information", user));
                } else if (!(Application.allowedTitleRegex(filledProjectForm.get().folder)
                        && Application.allowedTitleRegex(filledProjectForm.get().name))) {
                    return badRequest(projectNew.render("Something went wrong", filledProjectForm, true, "danger",
                            "The input did not have the allowed format, please review your information", user));
                } else {
                    String template = filledProjectForm.get().template;
                    if (!template.equals("None")) {
                        Event.defaultPlanningArticle(user, p);
                        p.planning = true;
                        p.save();
                    }
                    Project.edit(pid, filledProjectForm.get().folder, filledProjectForm.get().name, filledProjectForm.get().description,template);
                }
            }
            return redirect(routes.ProjectData.project(pid));
        } else {
            //User did not have a session
            session().put("callback", routes.ProjectData.editProject(pid).absoluteURL(request()));
            return Authentication.login();
        }
    }

    /**
     * This function refers to the model's archive function. The unused uid is to check whether the person is an
     * owner of the project at hand (future implementation)
     * @param uid: The User ID of the person requesting for archiving
     * @param pid: The Project ID of the project that is up for archiving
     * @return Result
     */
    public static Result archiveProject(Long pid) {
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            if (ProjectData.findAllOwners(pid).contains(user) && ProjectData.findAllOwners(pid).size() == 1) {
                Project.archive(pid);
            }
            return redirect(routes.Application.project());
        } else {
            //User did not have a session
            session().put("callback", routes.ProjectData.archiveProject(pid).absoluteURL(request()));
            return Authentication.login();
        }
    }

    /**
     * This function catches the Dynamicform from the template with the ID of the User that needs to
     * be added to the project of which the Project ID has been passed
     * @param pid: The project to which the user in the form has to be assigned
     * @return Result
     */
    public static Result inviteMemberToProjectAs(Long pid) {
        DynamicForm emailform = Form.form().bindFromRequest();
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if (user != null){
            //See if the user requesting to invite someone is an Owner
            if (ProjectData.findAllOwners(pid).contains(user)) {
                Person user_invited = Person.find.where().eq("email", emailform.get("email")).findUnique();
                Project p = Project.find.byId(pid);
                //See if the user that needs to be invited exists in the system
                if (user_invited != null) {
                    //There can not be a role relation between the invited user and the project,
                    // as it would be the user is already a member
                    if (Role.find.where().eq("project",  p).eq("person", user_invited).findUnique() == null) {
                        //Pattern match the correct role for invitation
                        if (emailform.get("role").equals("Owner")) {
                            Project.inviteOwner(p.id, user_invited.id);

                            p.planning = true;
                            p.save();
                        } else if (emailform.get("role").equals("Reviewer")) {
                            Project.inviteReviewer(p.id, user_invited.id);
                        } else {
                            Project.inviteGuest(p.id, user_invited.id);
                        }
                    }
                } else {
                    flash("error", "You did not provide a valid email address");
                    return redirect(routes.Application.project());
                }
            }
            return redirect(routes.Application.project());
        } else {
            //User did not have a session
            session().put("callback", routes.ProjectData.inviteMemberToProjectAs(pid).absoluteURL(request()));
            return Authentication.login();
        }
    }

    /**
     * This function confirms the invitation to a project from the user currently logged in.
     * @param pid: project ID to be joined
     * @return Result
     */
    public static Result hasAccepted(Long pid){
        Project p = Project.find.byId(pid);
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if( user != null) {
            Role r = Role.find.where().eq("person", user).eq("project", p).findUnique();
            //See if there actually exists a (pending) role between the accepting user and project
            if (r != null) {
                r.accepted = true;
                if(r.role.equals(Role.OWNER))
                Event.defaultPlanningArticle(user, p);
                r.dateJoined = new Date();
                r.save();
                List<Person> owners=findAllOwners(p.id);
                owners.stream().forEach((u) -> {
                    if(!u.equals(user))
                    Emailer.sendNotifyEmail("[Assistu] "+ user.name + " has joined the project" , u ,views.html.email.project_joined.render(u,user,p) );
                });
                Emailer.sendNotifyEmail("[Assistu] "+ user.name + " you have just joined the project" , user ,views.html.email.project_joined_2.render(user,p) );

            }
            return redirect(routes.ProjectData.project(p.id));
        } else {
            //User did not have a session
            session().put("callback", routes.ProjectData.hasAccepted(pid).absoluteURL(request()));
            return Authentication.login();
        }
    }

    /**
     * This function declines the invitation to a project from the user currently logged in.
     * @param pid: project ID to be declined
     * @return Result
     */
    public static Result hasDeclined(Long pid){
        Project p = Project.find.byId(pid);
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            Role r = Role.find.where().eq("person", user).eq("project", p).findUnique();
            if (r != null) {
                r.delete();
            }
            List<Person> owners=findAllOwners(p.id);
            owners.stream().forEach((u) -> {
                if(!u.equals(user))
                    Emailer.sendNotifyEmail("[Assistu] "+ user.name + " has declined to join the project" , u ,views.html.email.declined.render(u,user,p) );
            });
            return redirect(routes.Application.project());
        } else {
            //User did not have a session
            session().put("callback", routes.ProjectData.hasDeclined(pid).absoluteURL(request()));

            return Authentication.login();
        }

    }

    /**
     * This function removes a user from a project. This function doubles
     * as removing someone and leaving yourself. In the latter case your
     * own User ID is passed.
     * @param uid: The ID of the User that is being removed
     * @param pid: The ID of the Project in which the User had to be removed from
     * @return Result
     */
    public static Result removeMemberFromProject(Long uid, Long pid){
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Person other = Person.find.byId(uid);
        if(user != null) {
            if (ProjectData.findAllOwners(pid).contains(user) && !ProjectData.findAllOwners(pid).contains(other)) {
                Project.removeMemberFrom(pid, uid);
            }
            return redirect(routes.Application.project());
        } else {
            //User did not have a session
            session().put("callback", routes.ProjectData.removeMemberFromProject(uid, pid).absoluteURL(request()));
            return Authentication.login();
        }
    }

    /**
     * This function removes the user logged in from a project
     * @param pid: project ID of project the user wants to leave
     * @return Result
     */
    public static Result leaveProject(Long pid){
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            Project.removeMemberFrom(pid, user.id);
            return redirect(routes.Application.project());
        } else {
            //User did not have a session
            session().put("callback", routes.ProjectData.leaveProject(pid).absoluteURL(request()));
            return Authentication.login();
        }
    }

    /**
     * Returns the project name and ID pairs as JSON
     * @return Result
     */
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

    /**
     * Returns the owner projects IDs as JSON
     * @return Result
     */
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

    /**
     * Returns the reviewer projects IDs as JSON
     * @return Result
     */
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

    /**
     * Returns the guest project IDs as JSON
     * @return Result
     */
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

    /**
     * Return the name and ID of the last created project
     * @return Result
     */
    public static Result getLastAccessedProjectIdAsJson(){
        Project p = PersonData.getLastUsedProject();
        HashMap<String, String> project = new HashMap<String, String>();
        if(p != null) {
            project.put("name", p.name);
            project.put("projectID", "" + p.id);
        }
        return ok(toJson(project));
    }

    /**
     * Finds all the users in a project
     * @param pid: the ID of the project
     * @return List<Person>
     */
    public static List<Person> findAllAffiliatedUsers(Long pid){
        Project p = Project.find.byId(pid);
        List<Role> roles = Role.find.where().eq("project", p).findList();
        List<Person> persons = new ArrayList<Person>();
        for(Role role: roles){
            persons.add(role.person);
        }
        return persons;
    }

    /**
     * This function returns all the owners of a project
     * @param pid: project ID
     * @return List<Person>
     */
    public static List<Person> findAllOwners(Long pid){
        Project p = Project.find.byId(pid);
        List<Role> roles = Role.find.where().eq("project", p).eq("role", Role.OWNER).findList();
        List<Person> persons = new ArrayList<Person>();
        for(Role role: roles){
            persons.add(role.person);
        }
        return persons;
    }

    /**
     * This function returns all the reviewers of a project
     * @param pid: project ID
     * @return List<Person>
     */
    public static List<Person> findAllReviewers(Long pid){
        Project p = Project.find.byId(pid);
        List<Role> roles = Role.find.where().eq("project", p).eq("role", Role.REVIEWER).findList();
        List<Person> persons = new ArrayList<Person>();
        for(Role role: roles){
            persons.add(role.person);
        }
        return persons;
    }

    /**
     * This function returns all the guests of a project
     * @param pid: project ID
     * @return List<Person>
     */
    public static List<Person> findAllGuests(Long pid){
        Project p = Project.find.byId(pid);
        List<Role> roles = Role.find.where().eq("project", p).eq("role", Role.GUEST).findList();
        List<Person> persons = new ArrayList<Person>();
        for(Role role: roles){
            persons.add(role.person);
        }
        return persons;
    }

    public static List<MendeleyDocument> findAllMendeleyDocuments(Long pid){
        List<Person> members = ProjectData.findAllOwners(pid);
        List<MendeleyDocument> mendeley_docs = MendeleyDocument.find.where().in("person", members).orderBy("title").setDistinct(true).findList();
        Map<String, MendeleyDocument> temp = new HashMap<String, MendeleyDocument>();
        for(MendeleyDocument mendeley_doc : mendeley_docs){
            temp.put(mendeley_doc.title, mendeley_doc);
        }
        List<MendeleyDocument> result = new ArrayList<MendeleyDocument>();
        for(String title : temp.keySet()){
            result.add(temp.get(title));
        }
        return result;
    }

}
