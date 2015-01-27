package controllers;

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

public class ChatData extends Controller {

  /** Keeps track of all connected browsers per room **/
  private static Map<String, List<EventSource>> socketsPerProject = new HashMap<String, List<EventSource>>();

  /**
   * Controller action for POSTing chat messages
   */
  public static Result postMessage() {
    JsonNode message = request().body().asJson();
    Map<String, String> postMessage = new TreeMap<String, String>();
    postMessage.put("subject", message.get("subject").asText());
    postMessage.put("senderID", "" + message.get("senderID").asLong());
    postMessage.put("sender", message.get("sender").asText());
    postMessage.put("projectID", "" + message.get("projectID").asLong());
    postMessage.put("text", message.get("text").asText());
    postMessage.put("date", message.get("date").asText());
    if(message.get("parentID").asInt() == -1) {
      Logger.debug("parentID was " + message.get("parentID").asInt());
        Comment cm = Comment.create(message.get("senderID").asLong(), message.get("sender").asText(),
                message.get("subject").asText(), message.get("text").asText(), message.get("date").asText(),
                message.get("projectID").asLong());
        Project.addComment(cm, message.get("projectID").asLong());
        postMessage.put("commentID", "" + cm.id);
    }
    Logger.debug("" + Json.toJson(postMessage));
    sendEvent(Json.toJson(postMessage));
    return ok();
  }

  public static Result getOldMessages(Long pid){
    List<Comment> cml = Comment.find.where().eq("project", Project.find.byId(pid)).findList();
    List<TreeMap<String, String>> messages = new ArrayList<TreeMap<String, String>>();
    TreeMap<String, String> message;
    for(int i =0; i < cml.size(); i++){
      Comment cm = cml.get(i);
      message = new TreeMap<String, String>();
      message.put("commentID", "" + cm.id);
      message.put("text", cm.text);
      message.put("subject", cm.subject);
      message.put("senderID", "" + cm.senderID);
      message.put("sender", "" + cm.sender);
      message.put("date", cm.date);
      message.put("projectID", "" + cm.project.id);
//      Logger.debug("Message as Json: " + toJson(message.toString()));
      messages.add(message);
    }
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
