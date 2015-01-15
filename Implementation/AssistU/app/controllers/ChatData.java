package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.*;
import play.Logger;
import play.libs.EventSource;
import play.mvc.Controller;
import play.mvc.Result;
import scala.util.parsing.json.JSON;
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
    Logger.debug("" + message);
    String sender = message.get("sender").asText();
    Long senderID = message.get("senderID").asLong();
    Long pid = message.get("projectID").asLong();
    String text = message.get("text").asText();
    String date = message.get("date").asText();
    ChatMessage cm = ChatMessage.create(sender, senderID, text, date, pid);
    Project.addChatMessage(cm, message.get("projectID").asLong());
    sendEvent(message);
    return ok();
  }

  public static Result getOldMessages(Long pid){
    List<ChatMessage> cml = ChatMessage.find.where().eq("project", Project.find.byId(pid)).findList();
    List<TreeMap<String, String>> messages = new ArrayList<TreeMap<String, String>>();
    TreeMap<String, String> message;
    for(int i =0; i < cml.size(); i++){
      ChatMessage cm = cml.get(i);
      message = new TreeMap<String, String>();
      message.put("text", cm.text);
      message.put("sender", cm.sender);
      message.put("senderID", "" + cm.senderID);
      message.put("date", cm.date);
      message.put("projectID", "" + cm.project.id);
      Logger.debug("Message as Json: " + toJson(message.toString()));
      messages.add(message);
    }
    Logger.debug("Messages as Json: " + toJson(messages));
//        Logger.debug("Messages as Json: " + toJson(cml));
//        return toJson(p.chatMessages);
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
