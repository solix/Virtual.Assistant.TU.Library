package models;

import java.util.*;
import javax.persistence.*;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

/**
 * Task model class
 *
 */
@Entity

public class Task extends Model {

    @Id @GeneratedValue
    public long id;
    @Constraints.Required
    public String name;
    @Formats.DateTime(pattern="dd-MM-yyyy")
    public Date dueDate;

    public boolean done=false;

    @ManyToOne
    public User user;


    /**
     * Finder to  make queries from database via Ebeans
     */
    public static Model.Finder<Long,Task> find = new Model.Finder(
            Long.class, Task.class
    );

    /**
     * creates new task
     * @param t
     */
    public static void createTask(Task t){
        t.save();
    }

    /**
     * deletes a task
     * @param id
     */
    public static void deleteTask(Long id){
        Task.find.ref(id).delete();
    }

    /**
     * list all the task
     * @return
     */
    public static List<Task> alltask(){
        return Task.find.all();
    }

    /**
     * order the task in ascending order
     * @return
     */
    public static List<Task> ordered(){
        List<Task> tasks= Task.find.where().orderBy("dueDate asc").findList();
        return tasks;
    }
}
