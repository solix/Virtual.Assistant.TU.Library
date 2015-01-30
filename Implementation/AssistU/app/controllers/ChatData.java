package controllers;

import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import models.*;
import play.Logger;
import play.libs.EventSource;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

import static play.libs.Json.toJson;

import java.util.*;

//import javax.json;

public class ChatData extends Controller {

  /** Keeps track of all connected browsers per room **/
  private static Map<String, List<EventSource>> socketsPerProject = new HashMap<String, List<EventSource>>();

  /**
   * Controller action for POSTing chat messages
   */
  public static Result postMessage() {
    ObjectNode message = (ObjectNode)request().body().asJson();
    Logger.debug(Json.stringify(message));
    Comment comment = Comment.create(
            message.get("uid").asLong(),
            message.get("subject").asText(),
            message.get("content").asText(),
            message.get("date").asText(),
            message.get("pid").get("projectID").asLong(),
            message.get("isChild").asBoolean());
    message.put("cid", comment.cid);
    Logger.debug("New Comment: " + Json.stringify(message));
    sendEvent(message);
    return ok();
  }

  public static Result getOldMessages(Long pid){
    Logger.debug("Retrieving old messages from project: " + pid);
    List<Comment> cml = Comment.find.where().eq("project", Project.find.byId(pid)).eq("isChild", false).findList();
    Logger.debug("Number of comments: " + cml.size());
    List<HashMap<String, String>> messages = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> message;
    for(int i =0; i < cml.size(); i++){
      Comment cm = cml.get(i);
      message = new HashMap<String, String>();
      message.put("uid", "" + cm.user.id);
      message.put("subject", cm.subject);
      message.put("content", cm.content);
      message.put("date", cm.date);
      message.put("pid", "" + cm.project.id);
      message.put("isChild", "" + cm.isChild);
      message.put("cid", "" + cm.cid);
      List<Comment> scml = Comment.find.where().eq("project", Project.find.byId(pid)).eq("isChild", true).eq("subject", cm.subject).findList();
      List<HashMap<String, String>> submessages = new ArrayList<HashMap<String, String>>();
      HashMap<String, String> submessage;
      for(int j =0; j < scml.size(); j++) {
        Comment scm = scml.get(j);
        submessage = new HashMap<String, String>();
        submessage.put("uid", "" + scm.user.id);
        submessage.put("subject", scm.subject);
        submessage.put("content", scm.content);
        submessage.put("date", scm.date);
        submessage.put("pid", "" + scm.project.id);
        submessage.put("isChild", "" + scm.isChild);
        submessage.put("cid", "" + scm.cid);
        submessages.add(submessage);
      }
      messages.add(message);
    }
    Logger.debug("Old messages: " + Json.stringify(toJson(messages)));
    return ok(toJson(messages));
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
