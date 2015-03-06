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

    public static MendeleyDocument createMendeleyDocument(Person person, JsonNode nodeData){
        return MendeleyDocument.create(person.id, nodeData);
    }

    public static Person clearMendeleyData(Person person){
        List<MendeleyDocument> mendeley_documents = MendeleyDocument.find.where().eq("person", person).findList();
        for(MendeleyDocument mendeley_document : mendeley_documents){
            mendeley_document.delete();
        }
        person.update();
        return person;
    }

    public static Person updateMendeleyData(Person person, JsonNode oauth_mendeley_documents){
        for(JsonNode doc : oauth_mendeley_documents) {
            PersonData.createMendeleyDocument(person, doc);
            person.update();
        }
        return person;
    }

    public static Result postMendeleyDocument(String id){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        MendeleyDocument mendeley_documents = MendeleyDocument.find.where().eq("id", id).findUnique();
        try {
            exportDocumentToMendeley(Json.parse(mendeley_documents.nodeData), person.mendeleyToken);
        } catch(IOException e){
            Logger.debug("IOException: " + e.getMessage());
        }
        return Authentication.OAuth("mendeley");
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
            //Clear Mendeley Data
            clearMendeleyData(user);
            Person.deleteAccount(user.id);
        }
        return Authentication.login();
    }


    public static void exportDocumentToMendeley(JsonNode data, String token) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("curl",
                "https://api.mendeley.com/documents",
                "-H", "Authorization: Bearer " + token,
                "-H", "Accept: application/vnd.mendeley-document.1+json",
                "-H", "Content-Type: application/vnd.mendeley-document.1+json",
                "--data-binary", Json.stringify(data));

        Process shell = pb.start();
        InputStream errorStream= shell.getErrorStream();
        InputStream shellIn = shell.getInputStream();

        BufferedInputStream bis = new BufferedInputStream(shellIn);
        BufferedReader br=new BufferedReader(new InputStreamReader(bis));
        String strLine="";
        while ((strLine=br.readLine())!=null) {
            Logger.debug("STRLINE: " + strLine);
        }
        br.close();
    }
}
