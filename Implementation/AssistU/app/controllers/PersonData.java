package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import plugins.com.feth.play.module.pa.PlayAuthenticate;
import plugins.providers.mendeley.MendeleyAuthProvider;
import plugins.providers.mendeley.MendeleyAuthUser;
import scala.util.parsing.json.JSONObject;



/**
 * Created by arnaud on 16-12-14.
 */
public class PersonData extends Controller {

    /**
     * This function returns the list of the active projects from the user that is signed in.
     * @return List<Project>
     */
    public static List<Project> findActiveProjects(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    /**
     * This function returns the list of the projects to which the user that is signed in has pending invitations.
     * @return List<Project>
     */
    public static List<Project> findPendingInvitations(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("project.active", true).eq("accepted", false).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    /**
     * This function returns the list of the active projects from the user that is signed in where he is an owner in.
     * @return List<Project>
     */
    public static List<Project> findActiveOwnerProjects(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("role", Role.OWNER).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    /**
     * This function returns the list of the active projects from the user that is signed in where he is a reviewer in.
     * @return List<Project>
     */
    public static List<Project> findActiveReviewerProjects(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("role", Role.REVIEWER).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    /**
     * This function returns the list of the active projects from the user that is signed in where he is a guest in.
     * @return List<Project>
     */
    public static List<Project> findActiveGuestProjects(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("role", Role.GUEST).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    /**
     * This function returns the list of the archived projects from the user that is signed in.
     * @return List<Project>
     */
    public static List<Project> findArchivedProjects(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("project.active", false).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        return projects;
    }

    /**
     * This function returns the last used projects from the user that is signed in.
     * @return Project
     */
    public static Project getLastUsedProject(){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Role> roles = Role.find.where().eq("person", person).eq("project.active", true).eq("accepted", true).findList();
        List<Project> projects = Project.find.where().in("roles", roles).orderBy("dateCreated").findList();
        Project p = null;
        if(projects.size() > 0)
            p = projects.get(projects.size() -1);
        return p;
    }




    /**
     * TODO: HAS ISSUES ON CLOUD
     * This function deletes the user and all his relationships to other objects within the application
     * @return Result
     */
//    public static Result deleteAccount(){
//        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
//        if(user != null) {
//            //Delete LinkedAccounts
//            List<LinkedAccount> linked_accounts = user.linkedAccounts;
//            for (LinkedAccount l_a : linked_accounts) {
//                l_a.delete();
//            }
//            //Delete Roles
//            List<Role> roles = Role.find.where().eq("person", user).findList();
//            for(Role r : roles){
//                r.delete();
//            }
//            //Delete Tasks
//            List<Task> tasks = Task.find.where().eq("person", user).findList();
//            for(Task t : tasks){
//                t.delete();
//            }
//            //Delete Events
//            List<Event> events = Event.find.where().eq("person", user).findList();
//            for(Event e : events){
//                e.delete();
//            }
//            //Delete Comments
//            List<Comment> comments = Comment.find.where().eq("person", user).findList();
//            for(Comment c : comments){
//                c.delete();
//            }
//            //Delete Documents
//            List<S3File> documents = S3File.find.where().eq("person", user).findList();
//            for(S3File d : documents){
//                d.delete();
//            }
//            //Clear Mendeley Data
//            clearMendeleyData(user);
//            Person.deleteAccount(user.id);
//        }
//        return Authentication.login();
//    }



}
