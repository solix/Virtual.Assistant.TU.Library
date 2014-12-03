package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.db.ebean.Model;

/**
 * Project class which has list of members and owners
 */
@Entity
public class Project extends Model {

    @Id
    public Long id;
    public String folder;
    public String title;



    @ManyToMany(cascade = CascadeType.REMOVE)
    public List<User> members = new ArrayList<User>();


//    public Map<User,UserRole> memberroles = new HashMap<User,UserRole>();

    public Project (String folder, String title, User owner){

        this.folder = folder;
        this.title = title;
        members.add(owner);
        UserRole admin=new UserRole("admin" , "owns the project");


    }
    /**
     * Finder to  make queries from database
     */
    public static Model.Finder<Long,Project> find = new Model.Finder(
            Long.class, Project.class
    );

    

}