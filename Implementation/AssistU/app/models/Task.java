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
    public Person person;


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
    public static Task createTask(Task t, Person person){
        t.person = person;
        t.save();
        return t;
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
    public static List<Task> allUndoneTask(Person person){
        return Task.find.where().in("person",person).eq("done",false).orderBy("dueDate asc").findList();
    }

    /**
     * list all undone tasks
     * @param person
     * @return
     */
    public static List<Task> alltask(Person person){
        return Task.find.where().in("person", person).findList();
    }

    /**
     * order the task in ascending order
     * @return
     */
    public static List<Task> ordered(Person person){
        List<Task> tasks= Task.find.where().in("person", person).orderBy("dueDate asc").findList();
        return tasks;
    }
}
