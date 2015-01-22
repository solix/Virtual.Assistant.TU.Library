package models;

import javax.persistence.*;

import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * ChatMessage model class
 *
 */
@Entity

public class Comment extends Model {

    @Id @GeneratedValue
    public long id;
    @Constraints.Required
    public long senderID;
    public String subject;
    public String sender;
    @Column(columnDefinition="TEXT")
    public String text;
    public String date;
    public boolean edited;

    @ManyToOne
    public Project project;

    public Comment(Long senderID, String sender, String subject, String text, String date, Long pid){
        this.subject = subject;
        this.senderID = senderID;
        this.sender = sender;
        this.project = Project.find.byId(pid);
        this.text = text;
        this.date= date;
        this.edited=false;
    }

    /**
     * Finder to  make queries from database via Ebeans
     */
    public static Model.Finder<Long, Comment> find = new Model.Finder(
            Long.class, Comment.class
    );

    /**
     * creates new chatmessage
     */
    public static Comment create(Long senderID, String sender, String subject, String text, String date, Long pid){
        Comment cm = new Comment(senderID, sender, subject, text, date, pid);
        cm.save();
        return cm;
    }
}
