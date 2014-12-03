package models;

import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import javax.persistence.*;
import play.db.ebean.*;

/**
 * Project class which has list of members and owners
 */
@Entity
public class Project extends Model {

    @Id
    public Long id;
    public String folder;
    public int version;

    @ManyToMany(cascade = CascadeType.REMOVE)
    public List<User> members = new ArrayList<User>();

    public Map<User,UserRole> memberroles = new HashMap<User,UserRole>();

    public Project (String folder, User owner){
        this.folder = folder;
        members.add(owner);
        UserRole admin=new UserRole("admin" , "owns the project");
        this.memberroles.put(owner,admin);

    }
    /**
     * Finder to  make queries from database
     */
    public static Model.Finder<Long,Project> find = new Model.Finder(
            Long.class, Project.class
    );

    

}