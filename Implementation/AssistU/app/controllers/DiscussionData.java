package controllers;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import models.*;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.EventSource;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

import java.util.*;

import com.feth.play.module.pa.PlayAuthenticate;
import views.html.discussion;
import views.html.discussionFile;
import views.html.project;

public class DiscussionData extends Controller {

    /*TODO SOHEIL: Not sure we should notify on every message, or let them build up and send a summary at some point.*/
    public static Result discussion(Long pid) {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            Project.updateLastAccessed(pid);
            Project p = Project.find.byId(pid);
            return ok(discussion.render("AssistU - Projects", user, p));
        }else
            return Authentication.login();
    }

    /** Keeps track of all connected browsers per room **/
    private static Map<String, List<EventSource>> socketsPerProject = new HashMap<String, List<EventSource>>();

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
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(!message.get("content").asText().equals("")) {
            if (!message.get("isChild").asBoolean()) {
                Logger.debug("It's a main comment");
                message.put("subject", formatSubject(message.get("subject").asText(), Long.parseLong(message.get("projectID").asText())));
            }
            message.put("uid", user.id);
            Comment comment = Comment.create(
                    user.id,
                    message.get("subject").asText(),
                    message.get("content").asText(),
                    message.get("date").asText(),
                    message.get("projectID").asLong(),
                    message.get("isChild").asBoolean(),
                    false,
                    "");
            message.put("cid", comment.cid);
            message.put("username", comment.user.name);
            message.put("role", Role.find.where().eq("user", user).eq("project", Project.find.byId(message.get("projectID").asLong())).findUnique().role);
            Logger.debug("New Comment: " + Json.stringify(message));
            sendEvent(message);
        }
        return ok();
    }

    /**
    * Controller action for POSTing external chat messages created in project template
    */
    public static Result postExternalMessage() {
        DynamicForm message = Form.form().bindFromRequest();
        ObjectNode result = new ObjectMapper().createObjectNode();
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Project p = Project.find.byId(Long.parseLong(message.get("projectID")));
        DocumentFile doc = DocumentFile.find.byId(Long.parseLong(message.get("attachment")));
        if(message.get("content").equals("") || message.get("subject").equals("")) {
            return badRequest(discussionFile.render("An error has occured.", user, p,
                    DocumentFile.find.byId(Long.parseLong(message.get("documentID"))), true,
                    "Your message or subject was empty"));
        } else {
            result.put("uid", user.id);
            result.put("subject", formatSubject(message.get("subject"), p.id));
            result.put("content", message.get("content"));
            result.put("date", (new Date()).toString());
            result.put("projectID", p.id);
            result.put("isChild", false);
            result.put("hasAttachment", true);
            result.put("attachment", doc.name);
            Comment comment = Comment.create(
                    user.id,
                    result.get("subject").asText(),
                    result.get("content").asText(),
                    result.get("date").asText(),
                    result.get("projectID").asLong(),
                    result.get("isChild").asBoolean(),
                    result.get("hasAttachment").asBoolean(),
                    result.get("attachment").asText());
            result.put("cid", "" + comment.cid);
            result.put("role", Role.find.where().eq("user", user).eq("project", p).findUnique().role);
            result.put("username", user.name);
            Logger.debug("New Comment: " + Json.stringify(result));
            sendEvent(result);
            return DiscussionData.discussion(p.id);
        }
    }

    public static Result deleteMessage() {
        ObjectNode message = (ObjectNode)request().body().asJson();
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Comment comment = Comment.find.byId(Long.parseLong(message.get("cid").asText()));
        Role role = Role.find.where().eq("project", comment.project).eq("user", user).findUnique();
        Role comment_role = Role.find.where().eq("project", comment.project).eq("user", comment.user).findUnique();
        List<Comment> comments = Comment.find.where().eq("subject", comment.subject).findList();
        if(role != null && role.role.equals(Role.OWNER) && comment_role.equals(Role.GUEST)){
            for(int i = 0; i < comments.size(); i++){
                comments.get(i).delete();
            }
            comment.delete();
        } else if(comment.user.equals(user)){
            for(int i = 0; i < comments.size(); i++){
                comments.get(i).delete();
            }
            comment.delete();
        }
        return ok();
    }

    public static Result getComments() {
//        List<Project> ownerProjects = UserData.findActiveOwnerProjects();
//        List<Project> reviewerProjects = UserData.findActiveReviewerProjects();
//        List<Project> guestProjects = UserData.findActiveGuestProjects();
        List<Project> projects = UserData.findActiveProjects();
//        projects.addAll(reviewerProjects);

        List<ObjectNode> comments = new ArrayList<ObjectNode>();
        ObjectNode comment;
        for(int i = 0; i < projects.size(); i++) {
            Project p = projects.get(i);
            List<Comment> cml = Comment.find.where().eq("project", p).eq("isChild", false).findList();
            for(int j = 0; j < cml.size(); j++) {
                Comment cm = cml.get(j);
                comment = new ObjectMapper().createObjectNode();
                User user = User.find.byId(cm.user.id);
                comment.put("uid", "" + user.id);
                comment.put("username", user.name);
                comment.put("role", Role.find.where().eq("user", cm.user).eq("project", p).findUnique().role);
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
//      Logger.debug("Old Comments: " + Json.stringify(toJson(comments)));
        return ok(toJson(comments));
    }

    public static Result getSubComments() {
        List<Project> ownerProjects = UserData.findActiveOwnerProjects();
        List<Project> reviewerProjects = UserData.findActiveReviewerProjects();
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
                User user = User.find.byId(cm.user.id);
                comment.put("uid", "" + user.id);
                comment.put("username", user.name);
                comment.put("role", Role.find.where().eq("user", cm.user).eq("project", p).findUnique().role);
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
//      Logger.debug("Old SubComments: " + Json.stringify(toJson(comments)));
        return ok(toJson(comments));
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
