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

    @Id @GeneratedValue
    public Long id;
    public String folder;

//    public int version;

    @ManyToMany(cascade = CascadeType.REMOVE)
    public List<User> owners = new ArrayList<User>();

    @ManyToMany(cascade = CascadeType.REMOVE)
    public List<User> participants = new ArrayList<User>();

//    public Map<User,UserRole> memberroles = new HashMap<User,UserRole>();

    public Project (String folder){
        this.folder = folder;
//        owners.add(new User("test", new Profile("Arnaud", "Hambenne", "AH", "pass"), true));
//        UserRole admin=new UserRole("admin" , "owns the project");
//        this.memberroles.put(owner,admin);

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
    public static Project create(String folder){
        Project project = new Project(folder);
        project.save();

        return project;

    }

}