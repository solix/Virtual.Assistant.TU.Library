package controllers;

import models.*;
import play.mvc.*;

import java.util.List;
import com.feth.play.module.pa.PlayAuthenticate;

/**
 * Created by arnaud on 16-12-14.
 */
public class PersonData extends Controller {

    public static List<Project> findActiveProjects(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    public static List<Project> findPendingInvitations(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("project.active", true).eq("accepted", false).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    public static List<Project> findActiveOwnerProjects(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("role", Role.OWNER).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    public static List<Project> findActiveReviewerProjects(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("role", Role.REVIEWER).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    public static List<Project> findActiveGuestProjects(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("role", Role.GUEST).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    public static List<Project> findArchivedProjects(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("project.active", false).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    public static Project getLastUsedProject(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        Project p = null;
        if(projects.size() > 0)
            p = projects.get(projects.size() -1);
        return p;
    }

    public static Result deleteAccount(){
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            //Delete LinkedAccounts
            List<LinkedAccount> linked_accounts = user.linkedAccounts;
            for (LinkedAccount l_a : linked_accounts) {
                l_a.delete();
            }
            //Delete Roles
            List<Role> roles = Role.find.where().eq("person", user).findList();
            for(Role r : roles){
                r.delete();
            }
            //Delete Tasks
            List<Task> tasks = Task.find.where().eq("person", user).findList();
            for(Task t : tasks){
                t.delete();
            }
            //Delete Events
            List<Event> events = Event.find.where().eq("person", user).findList();
            for(Event e : events){
                e.delete();
            }
            //Delete Comments
            List<Comment> comments = Comment.find.where().eq("person", user).findList();
            for(Comment c : comments){
                c.delete();
            }
            //Delete Documents
            List<DocumentFile> documents = DocumentFile.find.where().eq("person", user).findList();
            for(DocumentFile d : documents){
                d.delete();
            }
            Person.deleteAccount(user.id);
        }
        return Authentication.login();
    }

}
