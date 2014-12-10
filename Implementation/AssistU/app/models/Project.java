package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import play.data.validation.Constraints.*;
import play.db.ebean.Model;

/**
 * Project class which has list of members and owners
 */
@Entity
public class Project extends Model {

    @Id @GeneratedValue
    public Long id;

    @Required
    @MinLength(5)
    public String folder;

    @Required
    @MinLength(5)
    public String name;

    public String description;

    public Boolean active;


//    @ManyToMany(cascade = CascadeType.REMOVE)
//    public List<User> owners = new ArrayList<User>();

//    @ManyToMany(cascade = CascadeType.REMOVE)
//    public List<User> participants = new ArrayList<User>();


//    public Map<User,UserRole> memberroles = new HashMap<User,UserRole>();

    public Project (String folder, String name, String description){
        this.folder = folder;
        this.name = name;
        this.description = description;
//        owners.add(new User("test", new Profile("Arnaud", "Hambenne", "AH", "pass"), true));
//        UserRole admin=new UserRole("admin" , "owns the project");
//        this.memberroles.put(owner,admin);
        this.active = true;
    }
    /**
     * Finder to  make queries from database
     */
    public static Model.Finder<Long,Project> find = new Model.Finder(
            Long.class, Project.class
    );


    /**
     * creates a new project and saves it to DB
     * @param
     * @return
     */
    public static Project create(String folder, String name, String description){
        Project project = new Project(folder, name, description);
        project.save();

        return project;
    }

    public Project update(String folder, String name, String description){
        this.folder = folder;
        this.name = name;
        this.description = description;
        this.save();
        return this;
    }

    public Project archive(){
        this.active = false;
        this.save();
        return this;
    }

    public String toString() {
        return "Project: " + this.id + "\n"
                + "Name: " + this.name;
    }

}