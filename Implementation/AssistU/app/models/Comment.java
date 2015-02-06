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

    @Id
    public long cid;
    @Constraints.Required
    public String subject;
    @Constraints.Required @Column(columnDefinition="TEXT")
    public String content;

    public String date;
    @ManyToOne
    public User user;
    @ManyToOne
    public Project project;

    public boolean isChild =false;

    public Comment(Long uid, String subject, String text, String date, Long pid, Boolean isChild){
        this.subject = subject;
        this.content = text;
        this.date= date;
        this.user = User.find.byId(uid);
        this.project = Project.find.byId(pid);
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
        Project.updateLastAccessed(pid);
        cm.save();
        return cm;
    }
}
