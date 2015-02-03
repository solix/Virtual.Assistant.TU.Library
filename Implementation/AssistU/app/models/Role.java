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
    @ManyToOne
    User user;



    public static Model.Finder<Long,Role> find = new Model.Finder(
            Long.class, Role.class
    );

    /**
     * creates a owner role which is immutable
     * @param
     */
    public static Role ownerRole(long uid ){
        final String o = "Owner";
         Role ownerRole = new Role(o);
        ownerRole.user=User.find.byId(uid);
        ownerRole.save();
            return ownerRole;

    }

    /**
     * creates a reviewer role which is immutable
     */
    public Role reviewerRole(long uid){
        final String r = "Reviewer";
        Role reviewerRole = new Role(r);
        reviewerRole.user=User.find.byId(uid);
        reviewerRole.save();
        return reviewerRole;
    }

    /**
     * creates a guest role which is immutable
     */
    public Role guestRole(long uid ){
        final String g = "Guest";
        Role guestRole = new Role(g);
        guestRole.user=User.find.byId(uid);
        guestRole.save();
        return guestRole;
    }
}