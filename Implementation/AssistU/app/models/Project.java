package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

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
    public List<DocumentFile> documentFiles= new ArrayList<DocumentFile>();



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
     * Finder to  make queries from database via Ebeans
     */
    public static Model.Finder<Long,Project> find = new Model.Finder(
            Long.class, Project.class
    );

    /**
     * find involving members
     */

    public static List<Project> findProjectInvolving(String user){
        List<Project> projects = find.where().eq("userlist.email" , user).findList();
        return projects;
    }
    /**
     * creates a new project and saves it to DB
     * @param
     * @return
     */
    public static Project create(String folder, String name,  String owner ,String description){
        Project project = new Project(folder, name ,User.find.ref(owner),description);
        project.active=true;
        DocumentFile documentFile=new DocumentFile(null,null,null);
        documentFile.project=project;
        project.save();
        documentFile.save();
        return project;
    }

    /**
     * edits the project TODO: change the name of update method to edit.
     * TODO: project need to be find by id and only then new data will be updated using update method

     * @return
     */

    public static void edit(Long pid){
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