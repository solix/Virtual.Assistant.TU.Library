package models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.*;

import javax.persistence.*;

import play.Logger;
import play.data.format.Formats;
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
    @Formats.DateTime(pattern = "dd.MM.yyyy HH:mm")
    public Date dateCreated ;
    @ManyToMany(mappedBy = "projects")
    public List<User> users = new ArrayList<User>();
    @OneToMany(mappedBy = "project")
    public List<DocumentFile> documentFiles = new ArrayList<DocumentFile>();
//    @OneToMany(mappedBy = "project")
//    public List<Comment> comments = new ArrayList<Comment>();

    public Map<Long, Role> relations = new HashMap<Long, Role>();

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
    public static Project create(String folder, String name,  Long owner ,String description){
        Project project = new Project(folder, name ,User.find.ref(owner),description);
        project.setOwner(owner);
        project.active=true;
        project.dateCreated =new Date();
        Logger.debug("Project with the name of ("+project.name+ ") has been created on: " + project.dateCreated);
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
    public static void addMemberAs(Long pid, Long uid){
        Project p = Project.find.ref(pid);
        p.users.add(User.find.byId(uid));
        p.update();
        //p.saveManyToManyAssociations("users");
    }

    /**
     * TODO: Add roles accordingly
     * this method removes a member from the project
     */
    public static void removeMemberFrom(Long pid, Long uid){
        Project p = Project.find.byId(pid);
        p.users.remove(User.find.byId(uid));
        p.update();
//        p.saveManyToManyAssociations("users");
    }

    public void setOwner(Long uid){
        if(Role.find.where().eq("role", "Owner").findRowCount() == 0) {
            Role.ownerRole();
        }
        this.relations.put(uid,Role.find.where().eq("role", "Owner").findUnique());
    }

    public void setGuest(Long uid){
        if(Role.find.where().eq("role", "Guest").findRowCount() == 0) {
            Role.ownerRole();
        }
        this.relations.put(uid,Role.find.where().eq("role", "Guest").findUnique());
    }

    public void setReviewer(Long uid){
        if(Role.find.where().eq("role", "Reviewer").findRowCount() == 0) {
            Role.ownerRole();
        }
        this.relations.put(uid,Role.find.where().eq("role", "Reviewer").findUnique());
    }

}