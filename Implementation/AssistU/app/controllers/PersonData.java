package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;
import plugins.com.feth.play.module.pa.PlayAuthenticate;
import plugins.providers.mendeley.MendeleyAuthUser;

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

    public static MendeleyDocument createMendeleyDocument(String id, String title, String type, List<String> authors_new, String year){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        MendeleyDocument mendeley_document = MendeleyDocument.create(id, person.id, title, type, authors_new, year);
        return mendeley_document;
    }

    public static Person clearMendeleyData(Person person){
        for(MendeleyDocument mendeley_document : person.mendeleydocuments){
            if(mendeley_document.persons.size() == 1){
                person.mendeleydocuments.remove(mendeley_document);
                mendeley_document.persons.remove(person);
                mendeley_document.delete();
            }else{
                mendeley_document.persons.remove(person);
                mendeley_document.update();
            }
        }
        person.update();
        return person;
    }

    public static Person updateMendeleyData(Person person, JsonNode oauth_mendeley_documents){
        for(JsonNode doc : oauth_mendeley_documents){
            MendeleyDocument mendeley_doc = MendeleyDocument.find.where().eq("id", doc.get("id").asText()).findUnique();
            if(mendeley_doc == null){
                List<String> authors = new ArrayList<String>();
                for(JsonNode author : doc.get("authors")){
                    authors.add(author.get("last_name").asText());
                }
                mendeley_doc = PersonData.createMendeleyDocument(
                        doc.get("id").asText(),
                        doc.get("title").asText(),
                        doc.get("type").asText(),
                        authors,
                        doc.get("year").asText());
            }
            if(!mendeley_doc.persons.contains(person)) {
                person.mendeleydocuments.add(mendeley_doc);
                mendeley_doc.update();
            }
            person.update();
        }
        return person;
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
