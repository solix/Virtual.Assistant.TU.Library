package models;

import play.db.ebean.Model;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.*;
import java.util.ArrayList;

/**
 * This class defines the role for each member involving in the project
 */
@Entity
public class UserRole{
   @Id @GeneratedValue
   public long id;

    public String tag;
    public String description;

    @ManyToMany(mappedBy = "userroles")
    List<User> users = new ArrayList<User>();

    /**
     * Finder to  make queries from database via Ebeans
     */
    public static Model.Finder<Long,UserRole> find = new Model.Finder(
            Long.class, UserRole.class
    );


@Override
    public String toString(){
    return this.tag;
}



}