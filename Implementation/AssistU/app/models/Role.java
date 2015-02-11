package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import javax.validation.Constraint;
import java.util.*;

/**
 * Created by soheil on 02/02/15.
 */
@Entity
public class Role extends Model {

    @Id
    public Long rid;
    @Constraints.Required
    public String role;

    public Date dateInvited;

    public Date dateJoined;

    public Boolean accepted = false;

    @ManyToOne
    public User user;
    @ManyToOne
    public Project project;

    final public static String OWNER = "Owner";
    final public static String GUEST = "Guest";
    final public static String REVIEWER = "Reviewer";

    public Role(Long pid, Long uid, String role){
        this.role=role;
        this.user = User.find.byId(uid);
        this.project = Project.find.byId(pid);
        this.dateInvited = new Date();
    }
    
    public static Model.Finder<Long,Role> find = new Model.Finder(
            Long.class, Role.class
    );

    /**
     * creates a owner role
     * @param
     */
    public static Role createOwnerRole(Long pid, Long uid){
        Role ownerRole = new Role(pid, uid, OWNER);
        ownerRole.save();
        return ownerRole;
    }

    /**
     * creates a reviewer role
     */
    public static Role createReviewerRole(Long pid, Long uid){
        Role reviewerRole = new Role(pid, uid, REVIEWER);
        reviewerRole.save();
        return reviewerRole;
    }

    /**
     * creates a guest role
     */
    public static Role createGuestRole(Long pid, Long uid){
        Role guestRole = new Role(pid, uid, GUEST);
        guestRole.save();
        return guestRole;
    }
}