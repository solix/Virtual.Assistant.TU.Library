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
    @MinLength(3)
    public String name;
    @MinLength(3)
    public String folder;
    public String description;
    @Required
    public String template;
    public boolean planning;
    public Boolean active=false;
    @Formats.DateTime(pattern = "dd.MM.yyyy HH:mm")
    public Date dateCreated;
    @Formats.DateTime(pattern = "dd.MM.yyyy HH:mm")
    public Date lastAccessed;
    @OneToMany(mappedBy = "project")
    public List<Role> roles = new ArrayList<Role>();
    @OneToMany(mappedBy = "project")
    public List<DocumentFile> documentFiles = new ArrayList<DocumentFile>();
    @OneToMany(mappedBy = "project")
    public List<Comment> comments = new ArrayList<Comment>();

    /**
     * Constructor
     * @param folder
     * @param name
     */
    public Project (String folder, String name, String description, String template){
        this.folder = folder;
        this.name = name;
        this.description=description;
        this.template=template;
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
    public static Project create(String folder, String name, String description, String template){
        Project project = new Project(folder, name, description, template);
        project.active=true;
        project.dateCreated = new Date();
        project.lastAccessed = new Date();
        project.planning=false;
        project.save();
        return project;
    }

    /**
     * edits the project
     * TODO: project need to be find by id and only then new data will be updated using update method (need to check hashmap support)
     * @return
     */
    public static void edit(Long pid, String folder, String name, String description){
        Project p = Project.find.byId(pid);
        p.folder = folder;
        p.name = name;
        p.description = description;
        p.lastAccessed = new Date();
        p.update();
    }

    /**
     * this method closes the project
     * @return
     */
    public static void archive(Long pid){
        Project p = Project.find.byId(pid);
        p.active = false;
        p.lastAccessed = new Date();
        p.update();
    }


    /**
     * This method invites another user to a project by its user id
     */
    public static void addOwner(Long pid, Long uid){
        Project p = Project.find.ref(pid);
        User u = User.find.byId(uid);
        Role r = Role.createOwnerRole(pid, uid);
        p.roles.add(r);
        u.roles.add(r);
        p.lastAccessed = new Date();
        p.update();
        u.update();
    }

    public static void addGuest(Long pid, Long uid){
        Project p = Project.find.ref(pid);
        User u = User.find.byId(uid);
        Role r = Role.createGuestRole(pid, uid);
        p.roles.add(r);
        u.roles.add(r);
        p.lastAccessed = new Date();
        p.update();
        u.update();
    }

    public static void addReviewer(Long pid, Long uid){
        Project p = Project.find.ref(pid);
        User u = User.find.byId(uid);
        Role r = Role.createReviewerRole(pid, uid);
        p.roles.add(r);
        u.roles.add(r);
        p.lastAccessed = new Date();
        p.update();
        u.update();
    }

    /**
     * this method removes a member from the project
     */
    public static void removeMemberFrom(Long pid, Long uid){
        Project p = Project.find.byId(pid);
        User u = User.find.byId(uid);
        Role r = Role.find.where().eq("project", p).eq("user", u).findUnique();
        p.roles.remove(r);
        u.roles.remove(r);
        p.lastAccessed = new Date();
        p.update();
        u.update();
        r.delete();
    }

    public static void updateLastAccessed(Long pid){
        Project p = Project.find.byId(pid);
        p.lastAccessed = new Date();
        p.update();
    }
}