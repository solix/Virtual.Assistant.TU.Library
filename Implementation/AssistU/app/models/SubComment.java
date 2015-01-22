package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;

/**
 * SubComment model class
 *
 */
@Entity

public class SubComment extends Model {

    @Id @GeneratedValue
    public long id;
    @Constraints.Required
    public long senderID;
    @Column(columnDefinition="TEXT")
    public String text;
    public String date;
    public boolean edited;

    @ManyToMany
    public Comment parentComment;

    public SubComment(Long senderID, String text, String date, Long cid){
        this.senderID = senderID;
        this.parentComment = Comment.find.byId(cid);
        this.text = text;
        this.date= date;
        this.edited=false;
    }

    /**
     * Finder to  make queries from database via Ebeans
     */
    public static Finder<Long, SubComment> find = new Finder(
            Long.class, SubComment.class
    );

    /**
     * Adds new SubComment to existing Comment
     */
    public static SubComment create(Long senderID, String text, String date, Long cid){
        SubComment cm = new SubComment(senderID, text, date, cid);
        cm.save();
        return cm;
    }
}
