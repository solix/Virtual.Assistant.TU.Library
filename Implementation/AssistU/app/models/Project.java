package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import play.Logger;
import play.data.validation.Constraints.*;
import play.db.ebean.Model;

/**
 * Project model class
 */
@Entity
public class Project extends Model {

    @Id
    public Long id;
    public String name;
    public String folder;
    public String description;
    public Boolean active=false;
    @ManyToMany(mappedBy = "projects")
    public List<User> users = new ArrayList<User>();
    @OneToMany(mappedBy = "project")
    public List<DocumentFile> documentFiles = new ArrayList<DocumentFile>();



    /**
     * Constructor
     * @param folder
     * @param name
     */
    public Project (String folder, String name, User owner,String description){

        this.folder = folder;
        this.name = name;
        this.description=description;
        this.users.add(owner);
    }

    /**
     * find involving members
     */

    public static List<Project> findProjectInvolving(String user){
        List<Project> projects = find.where().eq("users.email" , user).findList();
        return projects;
    }
    /**
     * Finder to  make queries from database via Ebeans
     */
    public static Model.Finder<Long,Project> find = new Model.Finder(
            Long.class, Project.class
    );

    /**
     * creates a new project and saves it to DB
     * @param
     * @return
     */
    public static Project create(String folder, String name,  String owner ,String description){
        Logger.debug("CREATE: " + folder + ", " + name + ", " + owner + ", " + description);
        Project project = new Project(folder, name ,User.find.ref(owner),description);
        project.active=true;
        project.save();
        return project;
    }

    /**
     * edits the project
     * TODO: project need to be find by id and only then new data will be updated using update method (need to check hashmap support)
     * @return
     */
    public static void edit(Long pid, String folder, String name){
        Project p = Project.find.byId(pid);
        p.folder = folder;
        p.name = name;
        p.update();
    }

    /**
     * TODO: Project will be find from id and is set to false
     * this method closes the project
     * @return
     */
    public static void archive(Long pid){
        Project p = Project.find.byId(pid);
        p.active = false;
        p.update();
//        p.saveManyToManyAssociations("userlist");
    }

    /**
     * TODO: Set role as third parameter? no,better to set role as seperate function
     * This method invites another user to a project by its user id
     */
    public static void addMemberAs(Long pid, String uid){
        Project p = Project.find.ref(pid);
        p.users.add(User.find.byId(uid));
        p.update();
        p.saveManyToManyAssociations("users");
    }

    /**
     * TODO: Add roles accordingly
     * this method removes a member from the project
     */
    public static void removeMemberFrom(Long pid, String uid){
        Project p = Project.find.byId(pid);
        p.users.remove(User.find.byId(uid));
        p.update();
//        p.saveManyToManyAssociations("users");
    }

}