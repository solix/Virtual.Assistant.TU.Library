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

    @Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
    public long id;
    public String subject;
    @Column(columnDefinition="TEXT")
    public String text;
    public String date;
    @ManyToMany
    public User user;
    @ManyToOne
    public Project project;

    public boolean isParent;
    public boolean isChild;

    public Comment(Long uid, String subject, String text, String date, Long projectID, Boolean isChild){
        this.user = User.find.byId(uid);
        this.project = Project.find.byId(projectID);
        this.subject = subject;
        this.text = text;
        this.date= date;
        this.isParent = !isChild;
        this.isChild = isChild;
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
    public static Comment create(Long uid, String subject, String text, String date, Long pid, Boolean isChild){
        Comment cm = new Comment(uid, subject, text, date, pid, isChild);
        cm.save();
        return cm;
    }
}
