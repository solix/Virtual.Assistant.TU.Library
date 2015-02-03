package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.Logger;
import play.data.*;
import play.mvc.*;
import java.lang.String;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import static play.libs.Json.toJson;

/**
 * Created by arnaud on 13-12-14.
 */
public class ProjectData extends Controller {

    static Form<Project> projectForm = Form.form(Project.class);

    /**
     * TODO: Adapt this function after login is implemented and the current user is known
     * This function creates a new Project initiated by a user that automatically becomes its owner.
     * @return
     */
    public static Result createProject(Long uid) {
        User user = User.find.ref(uid);
        Form<Project> filledProjectForm = projectForm.bindFromRequest();
        if(filledProjectForm.hasErrors()) {
            return badRequest("The form had errors. Need to implement in-style validation");
        } else {
            Project projectData = filledProjectForm.get();
            Project project = Project.create(projectData.folder, projectData.name, user.id , "Description");
            user.roles.add(Role.ownerRole(uid));
            user.update();
            defaultPlanningArticle(project);
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
        Form<Project> filledProjectForm = projectForm.bindFromRequest();
        if(filledProjectForm.hasErrors()) {
            return badRequest("The form had errors. Need to implement in-style validation");
        } else {
            Project.edit(pid, filledProjectForm.get().folder, filledProjectForm.get().name);
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
            user.roles.add(Role.ownerRole(user.id));
        } else if(emailform.get("role").equals("Reviewer")) {
            user.roles.add(Role.reviewerRole(user.id));
        }else{
            user.roles.add(Role.guestRole(user.id));
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
        return redirect(routes.Application.project());
    }

    public static Result getProjectIdsAsJson(Long uid){
        List<Project> projects = Project.find.where().in("users", User.find.byId(uid)).eq("active", "true").findList();
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
     * Creates Default planning for articles owners of the project
     *
     */
    public static void defaultPlanningArticle(Project p){
        Date startDate = p.dateCreated;
        Event event1=Event.createArticleEvent("Getting Started", startDate, 0);
        event1.endsSameDay=true;
        event1.update();
        Event event2=Event.createArticleEvent("Keypoints", Event.movedate(event1.end), 1);
        Event event3=Event.createArticleEvent("Publication Strategy", Event.movedate(event2.end), 2);
        Event event4=Event.createArticleEvent("Introduction", Event.movedate(event3.end), 7);
        Event event5=Event.createArticleEvent("Materials & Methods", Event.movedate(event4.end), 4);
        Event event6=Event.createArticleEvent("Results & Discussion", Event.movedate(event5.end), 2);
        Event event7=Event.createArticleEvent("Abstract, keywords & Title ", Event.movedate(event6.end), 1);
        Event event8=Event.createArticleEvent("References and Acknowledgment",Event.movedate( event7.end), 0);
        event8.endsSameDay=true;
        event8.update();
        Event event9=Event.createArticleEvent("Layout & Styles", Event.movedate(event8.end), 0);
        event8.endsSameDay=true;
        event8.update();
    }



}
