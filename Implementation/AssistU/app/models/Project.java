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
    @Required @MinLength(5)
    public String folder;
    @Required @MinLength(5)
    public String name;
    public Boolean active=false;
    @ManyToMany(cascade= CascadeType.REMOVE)
    public List<User> userlist = new ArrayList<User>();


    /**
     * Constructor
     * @param folder
     * @param name
     */
    public Project (String folder, String name ,User owner ){
        this.folder = folder;
        this.name = name;
        this.userlist.add(owner);

    }
    /**
     * Finder to  make queries from database via Ebeans
     */
    public static Model.Finder<Long,Project> find = new Model.Finder(
            Long.class, Project.class
    );

    /**
     * find involving members
     */

    public static List<Project> findProjectInvolving(String User){
        List<Project> projects = find.where().eq("userlist.email" , user).findList();
        return projects;
    }
    /**
     * creates a new project and saves it to DB
     * @param
     * @return
     */
    public static Project create(String folder, String name, String description , String owner){
        Project project = new Project(folder, name ,User.find.ref(owner));
        project.active=true;
        project.save();
        project.saveManyToManyAssociations("userlist");
        return project;
    }

    /**
     * edits the project TODO: change the name of update method to edit.
     * TODO: project need to be find by id and only then new data will be updated using update method
     * @param folder
     * @param name
     * @param description
     * @return
     */

    public static void update(String folder, String name){
       // this.folder = folder;
        //this.name = name;
        //this.save();
    }

    /**
     * TODO: Project will be find from id and is set to false
     * this method closes the project
     * @return
     */
    public static void archive(){
        //this.active = false;
        //this.save();
        //return this;
    }



}