package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.*;

/**
 * Created by soheil on 02/02/15.
 */
@Entity
public class Role extends Model {

@Id
public long rid;
public String role;

public Role(String role){
    this.role=role;
}
@ManyToMany
List<User> users= new ArrayList<User>();

    public static Model.Finder<Long,Role> find = new Model.Finder(
            Long.class, Role.class
    );

    /**
     * creates a owner role which is immutable
      * @param
     */
public void ownerRole( ){

    final String o = "Owner";
    Role ownerRole = new Role(o);
    ownerRole.save();
}

    /**
     * creates a reviewer role which is immutable
     */
    public void reviewerRole(){

        final String r = "Reviewer";
        Role reviewerRole = new Role(r);
        reviewerRole.save();
    }

    /**
     * creates a guest role which is immutable
     */
    public void guestRole( ){
        final String g = "Guest";
        Role guestRole = new Role(g);
        guestRole.save();
    }
}