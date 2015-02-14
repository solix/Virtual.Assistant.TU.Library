package controllers;

import models.*;
import play.Logger;
import play.data.Form;
import play.mvc.*;
import views.html.*;

import java.util.List;
import com.feth.play.module.pa.PlayAuthenticate;

/**
 * Created by arnaud on 16-12-14.
 */
public class UserData  extends Controller {

    public static List<Project> findActiveProjects(){
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("user", user).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    public static List<Project> findPendingInvitations(){
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("user", user).eq("project.active", true).eq("accepted", false).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    public static List<Project> findActiveOwnerProjects(){
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("user", user).eq("role", Role.OWNER).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    public static List<Project> findActiveReviewerProjects(){
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("user", user).eq("role", Role.REVIEWER).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    public static List<Project> findActiveGuestProjects(){
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("user", user).eq("role", Role.GUEST).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    public static List<Project> findArchivedProjects(){
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("user", user).eq("project.active", false).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    public static Project getLastUsedProject(){
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("user", user).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("lastAccessed").findList();
        Project p = null;
        if(projects.size() > 0)
            p = projects.get(projects.size() -1);
        return p;
    }

    public static Result deleteAccount(){
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            //Delete LinkedAccounts
            List<LinkedAccount> linked_accounts = user.linkedAccounts;
            for (LinkedAccount l_a : linked_accounts) {
                l_a.delete();
            }
            //Delete Roles
            List<Role> roles = Role.find.where().eq("user", user).findList();
            for(Role r : roles){
                r.delete();
            }
            //Delete Tasks
            List<Task> tasks = Task.find.where().eq("user", user).findList();
            for(Task t : tasks){
                t.delete();
            }
            //Delete Events
            List<Event> events = Event.find.where().eq("user", user).findList();
            for(Event e : events){
                e.delete();
            }
            //Delete Comments
            List<Comment> comments = Comment.find.where().eq("user", user).findList();
            for(Comment c : comments){
                c.delete();
            }
            //Delete Documents
            List<DocumentFile> documents = DocumentFile.find.where().eq("user", user).findList();
            for(DocumentFile d : documents){
                d.delete();
            }
            User.deleteAccount(user.id);
        }
        return Authentication.login();
    }

}
