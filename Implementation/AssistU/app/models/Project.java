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

    @Id @GeneratedValue
    public Long id;
    public String folder;
    public String title;

//    public int version;

    @ManyToMany(cascade = CascadeType.REMOVE)
    public List<User> owners = new ArrayList<User>();

    @ManyToMany(cascade = CascadeType.REMOVE)
    public List<User> participants = new ArrayList<User>();


//    public Map<User,UserRole> memberroles = new HashMap<User,UserRole>();

    public Project (String folder, String title){

        this.folder = folder;
        this.title = title;
//        members.add(owner);
        UserRole admin=new UserRole("admin" , "owns the project");
    }
    /**
     * Finder to  make queries from database
     */
    public static Model.Finder<Long,Project> find = new Model.Finder(
            Long.class, Project.class
    );

    /**
     * creates a new project and saves it to DB
     * @param folder
     * @return
     */
    public static Project create(String folder, String title){
        Project project = new Project(folder, title);
        project.save();

        return project;

    }
}