package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import models.*;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.EventSource;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

import java.beans.Expression;
import java.util.*;

import plugins.com.feth.play.module.pa.PlayAuthenticate;
import views.html.discussion;
import views.html.discussionFile;

import controllers.routes;

public class DiscussionData extends Controller {

    /**
     * This function loads the discussion page view of a specific project
     * @param pid: the project ID
     * @return Result
     */
    public static Result discussion(Long pid) {
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            Project p = Project.find.byId(pid);
            return ok(discussion.render("AssistU - Discussions", user, p));
        }else {
            //User did not have a session
            session().put("callback", routes.DiscussionData.discussion(pid).absoluteURL(request()));
            return Authentication.login();
        }
    }

    public static Result seen(Long cid){
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null) {
            Comment c = Comment.find.byId(cid);
            c.seenBy.add(person);
            c.save();
            return discussion(c.project.id);
        }else {
            //User did not have a session
            session().put("callback", routes.DiscussionData.seen(cid).absoluteURL(request()));
            return Authentication.login();
        }
    }

    /** Keeps track of all connected browsers per room **/
    private static Map<String, List<EventSource>> socketsPerProject = new HashMap<String, List<EventSource>>();

    /**
     * This function recursively formats the subject of a discussion in the sense that it
     * adds 'copy' to the string each time it can find a message within the same project with the
     * same subject. Subject is how sub-comments get mapped to the right parent, so you
     * can't have the same subject for two different messages.
     * @param subject: The subject string
     * @param pid: The project it belongs to
     * @return String
     */
    private static String formatSubject(String subject, Long pid){
        List<Comment> cml = Comment.find.where().eq("project", Project.find.byId(pid))
                                                .eq("subject", subject)
                                                .eq("isChild", false).findList();
        if(cml.size() == 0){
            return subject;
        }else{
            return formatSubject(subject + " (copy)", pid);
        }
    }

    /**
    * Controller action for POSTing chat messages created in discussion page
    */
    public static Result postMessage() {
        ObjectNode message = (ObjectNode)request().body().asJson();
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null) {
            if (!message.get("content").asText().equals("")) {
                if (!message.get("isChild").asBoolean()) {
                    message.put("subject", formatSubject(message.get("subject").asText(), Long.parseLong(message.get("projectID").asText())));
                }
                message.put("uid", person.id);
                Comment comment = Comment.create(
                        person.id,
                        message.get("subject").asText(),
                        message.get("content").asText(),
                        message.get("date").asText(),
                        message.get("projectID").asLong(),
                        message.get("isChild").asBoolean(),
                        false,
                        "");
                message.put("cid", comment.cid);
                message.put("username", comment.person.name);
                message.put("role", Role.find.where().eq("person", person).eq("project", Project.find.byId(message.get("projectID").asLong())).findUnique().role);
                long pid = message.get("projectID").asLong();
                Project p= Project.find.byId(pid);
                List<Person> owners=ProjectData.findAllOwners(pid);
                owners.stream().forEach((u) -> {
                    if (!u.equals(person))
                        Emailer.sendNotifyEmail("[Assistu] you got a new comment", u, views.html.email.discussion.new_comment.render(u, person, p, message.get("content").asText()));
                });
                sendEvent(message);
            }

        }
        return ok();
    }

    /**
    * Controller action for POSTing external chat messages created in project template
    */
    public static Result postExternalMessage() {
        DynamicForm message = Form.form().bindFromRequest();
        ObjectNode result = new ObjectMapper().createObjectNode();
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Project p = Project.find.byId(Long.parseLong(message.get("projectID")));
        if(person != null){
            S3File doc = S3File.find.byId(java.util.UUID.fromString(message.get("attachment")));
            if(message.get("content").equals("") || message.get("subject").equals("")) {
                return badRequest(discussionFile.render("An error has occured.", person, p,
                        S3File.find.byId(java.util.UUID.fromString(message.get("attachment"))), message, true, "danger",
                        "Your message or subject was empty"));
            } else {
                result.put("uid", person.id);
                result.put("subject", formatSubject(message.get("subject"), p.id));
                result.put("content", message.get("content"));
                result.put("date", (new Date()).toString());
                result.put("projectID", p.id);
                result.put("isChild", false);
                result.put("hasAttachment", true);
                result.put("attachment", doc.name);
                Comment comment = Comment.create(
                        person.id,
                        result.get("subject").asText(),
                        result.get("content").asText(),
                        result.get("date").asText(),
                        result.get("projectID").asLong(),
                        result.get("isChild").asBoolean(),
                        result.get("hasAttachment").asBoolean(),
                        result.get("attachment").asText());
                result.put("cid", "" + comment.cid);
                result.put("role", Role.find.where().eq("person", person).eq("project", p).findUnique().role);
                result.put("username", person.name);

                List<Person> owners=ProjectData.findAllOwners(p.id);
                owners.stream().forEach((u) -> {
                    if (!u.equals(person))
                        Emailer.sendNotifyEmail("[Assistu] you got a new comment", u, views.html.email.discussion.new_comment.render(u, person, p, result.get("content").asText()));
                });
                sendEvent(result);

            }
        }
        return redirect(routes.DiscussionData.discussion(p.id));
    }

    /**
     * Deleting a message
     * @return Result
     */
    public static Result deleteMessage() {
        ObjectNode message = (ObjectNode)request().body().asJson();
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            Comment comment = Comment.find.byId(Long.parseLong(message.get("cid").asText()));
            Role role = Role.find.where().eq("project", comment.project).eq("person", user).findUnique();
            Role comment_role = Role.find.where().eq("project", comment.project).eq("person", comment.person).findUnique();
            List<Comment> comments = Comment.find.where().eq("subject", comment.subject).findList();
            if (role != null && role.role.equals(Role.OWNER) && comment_role.role.equals(Role.GUEST)) {
                for (int i = 0; i < comments.size(); i++) {
                    comments.get(i).delete();
                }
                comment.delete();
            } else if (comment.person.equals(user)) {
                for (int i = 0; i < comments.size(); i++) {
                    comments.get(i).delete();
                }
                comment.delete();
            }
        }
        return ok();
    }

    /**
     * Loading old comments as JSON nodes for when the user renders a discussion that already has
     * comments in it.
     * @return JSON
     */
    public static Result getComments() {
        List<Project> projects = PersonData.findActiveProjects();
        List<ObjectNode> comments = new ArrayList<ObjectNode>();
        ObjectNode comment;
        for(int i = 0; i < projects.size(); i++) {
            Project p = projects.get(i);
            List<Comment> cml = Comment.find.where().eq("project", p).eq("isChild", false).findList();
            for(int j = 0; j < cml.size(); j++) {
                Comment cm = cml.get(j);
                comment = new ObjectMapper().createObjectNode();
                Person person = Person.find.byId(cm.person.id);
                comment.put("uid", "" + person.id);
                comment.put("username", person.name);
                comment.put("role", Role.find.where().eq("person", cm.person).eq("project", p).findUnique().role);
                comment.put("subject", cm.subject);
                comment.put("content", cm.content);
                comment.put("date", cm.date);
                comment.put("projectID", "" + cm.project.id);
                comment.put("isChild", "" + cm.isChild);
                comment.put("cid", "" + cm.cid);
                comment.put("hasAttachment", cm.hasAttachment);
                comment.put("attachment", cm.attachment);
                comments.add(comment);
            }
        }
        return ok(toJson(comments));
    }

    /**
     * Loading old sub-comments as JSON nodes for when the user renders a discussion that already has
     * sub-comments in it.
     * @return JSON
     */
    public static Result getSubComments() {
        List<Project> ownerProjects = PersonData.findActiveOwnerProjects();
        List<Project> reviewerProjects = PersonData.findActiveReviewerProjects();
        List<Project> projects = new ArrayList<Project>(ownerProjects);
        projects.addAll(reviewerProjects);

        List<ObjectNode> comments = new ArrayList<ObjectNode>();
        ObjectNode comment;
        for (int i = 0; i < projects.size(); i++) {
            Project p = projects.get(i);
            List<Comment> cml = Comment.find.where().eq("project", p).eq("isChild", true).findList();
            for (int j = 0; j < cml.size(); j++) {
                Comment cm = cml.get(j);
                comment = new ObjectMapper().createObjectNode();
                Person person = Person.find.byId(cm.person.id);
                comment.put("uid", "" + person.id);
                comment.put("username", person.name);
                comment.put("role", Role.find.where().eq("person", cm.person).eq("project", p).findUnique().role);
                comment.put("subject", cm.subject);
                comment.put("content", cm.content);
                comment.put("date", cm.date);
                comment.put("projectID", "" + cm.project.id);
                comment.put("isChild", "" + cm.isChild);
                comment.put("cid", "" + cm.cid);
                comment.put("hasAttachment", cm.hasAttachment);
                comment.put("attachment", cm.attachment);
                comments.add(comment);
            }
        }
        return ok(toJson(comments));
    }

    /**
     * list all the comments
     * @return
     */
    public static List<Comment> allNewComments(Person person){
        List<Project> projects = PersonData.findActiveProjects();
        List<Comment> seenComments = Comment.find.where().in("project", projects).ne("person", person).in("seenBy", person).orderBy("date asc").findList();
        List<Comment> allComments = Comment.find.where().in("project", projects).ne("person", person).orderBy("date asc").findList();
        allComments.removeAll(seenComments);
        return allComments;
    }

    /**
    * Send event to all channels (browsers) which are connected to the room
    */
    public static void sendEvent(JsonNode msg) {
        String project  = msg.findPath("projectID").textValue();
        if(socketsPerProject.containsKey(project)) {
            socketsPerProject.get(project).stream().forEach(es -> es.send(EventSource.Event.event(msg)));
        }
    }

    /**
    * Establish the SSE HTTP 1.1 connection.
    * The new EventSource socket is stored in the socketsPerRoom Map
    * to keep track of which browser is in which room.
    *
    * onDisconnected removes the browser from the socketsPerRoom Map if the
    * browser window has been exited.
    * @return
    */
    public static Result chatFeed(String project) {
        String remoteAddress = request().remoteAddress();
        Logger.info(remoteAddress + " - SSE conntected");

        return ok(new EventSource() {
        @Override
        public void onConnected() {
            EventSource currentSocket = this;

            this.onDisconnected(() -> {
                Logger.info(remoteAddress + " - SSE disconntected");
                socketsPerProject.compute(project, (key, value) -> {
                    if(value.contains(currentSocket))
                        value.remove(currentSocket);
                    return value;
                });
            });

            // Add socket to room
            socketsPerProject.compute(project, (key, value) -> {
                if(value == null)
                    return new ArrayList<EventSource>() {{ add(currentSocket); }};
                else
                    value.add(currentSocket); return value;
            });
        }
    });
  }
}
