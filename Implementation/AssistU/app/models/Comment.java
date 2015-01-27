package models;

import javax.persistence.*;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import java.util.ArrayList;
import java.util.List;

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
    public long parentID;
    public String subject;
    public String sender;
    @Column(columnDefinition="TEXT")
    public String text;
    public String date;
    public boolean edited;

    @ManyToOne
    public Project project;

    @OneToMany
    public List<SubComment> subcomments = new ArrayList<SubComment>();

    public Comment(Long senderID, String sender, String subject, String text, String date, Long projectID, Long parentID){
        this.subject = subject;
        this.senderID = senderID;
        this.sender = sender;
        this.project = Project.find.byId(projectID);
        this.text = text;
        this.date= date;
        this.edited=false;
        this.parentID=parentID;
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
    public static Comment create(Long senderID, String sender, String subject, String text, String date, Long projectID){
        Comment cm = new Comment(senderID, sender, subject, text, date, projectID, -1L);
        cm.save();
        return cm;
    }

    public static Comment createChild(Long senderID, String sender, String subject, String text, String date, Long projectID, Long parentID){
        Comment cm = new Comment(senderID, sender, subject, text, date, projectID, parentID);
        cm.save();
        return cm;
    }

    public void addSubComment(SubComment scm){
        this.subcomments.add(scm);
    }
}
