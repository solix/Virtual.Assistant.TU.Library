package models;

import java.util.*;
import javax.persistence.*;

import com.fasterxml.jackson.databind.JsonNode;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * ChatMessage model class
 *
 */
@Entity

public class ChatMessage extends Model {

    @Id @GeneratedValue
    public long id;
    @Constraints.Required
    public long senderID;
    public String sender;
    public String text;
    public String date;
    public boolean edited;

    @ManyToOne
    public Project project;

    public ChatMessage (String sender, Long senderID, String text, String date, Long pid){
        this.sender = sender;
        this.senderID = senderID;
        this.project = Project.find.byId(pid);
        this.text = text;
        this.date= date;
        this.edited=false;
    }

    /**
     * Finder to  make queries from database via Ebeans
     */
    public static Model.Finder<Long,ChatMessage> find = new Model.Finder(
            Long.class, ChatMessage.class
    );

    /**
     * creates new chatmessage
     */
    public static ChatMessage create(String sender, Long senderID, String text, String date, Long pid){
        ChatMessage cm = new ChatMessage(sender, senderID, text, date, pid);
        cm.save();
        return cm;
    }
}
