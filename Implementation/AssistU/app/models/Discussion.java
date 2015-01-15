package models;

import java.util.*;
import javax.persistence.*;

import com.fasterxml.jackson.databind.JsonNode;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Task model class
 *
 */
@Entity

public class Discussion extends Model {

    @Id @GeneratedValue
    public long id;
//    @Constraints.Required
//    public String name;

//    public Long initiatorID;

    public boolean open=false;

//    @OneToOne(mappedBy = "project")
//    public Project project;

//    @ManyToMany
    public List<JsonNode> messages = new ArrayList<JsonNode>();

    public Discussion (){
//        this.name = name;
//        this.initiatorID = initiator;
        this.open=true;
    }

    /**
     * Finder to  make queries from database via Ebeans
     */
    public static Model.Finder<Long,Discussion> find = new Model.Finder(
            Long.class, Discussion.class
    );

    /**
     * creates new discussion
     */
    public static void create(String name, Long uid){
        Discussion discussion = new Discussion();
        discussion.save();
    }

    public static void addMessage(JsonNode m, Long pid){
//        Discussion d = Project.find.ref(pid).discussion;
//        d.messages.add(m);
//        d.save();
    }

    /**
     * deletes a discussion
     * @param id
     */
//    public static void close(Long did){
//        Discussion discussion = Discussion.find.ref(did);
//        discussion.open=false;
//        discussion.save();
//    }


}
