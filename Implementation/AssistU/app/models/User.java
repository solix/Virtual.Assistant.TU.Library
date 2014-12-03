package models;

import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;

import java.lang.Boolean;
import java.lang.String;
import java.util.*;

@Entity
public class User extends Model{
    /**
     * list of user attributes
     */

    @Id
    public String email;

    Profile userProfile;

    public boolean enabled;


    /**
     * bookkeeping for user activity
     */
//    @Temporal(TemporalType.DATE)
//    public Date createdAt;
//    @Temporal(TemporalType.DATE)
//    public Date lastLogin;


    public User(
            String email,
            Profile userProfile,
            boolean enabled){

        this.email = email;
        this.userProfile = userProfile;
//        this.createdAt = createdAT;
//        this.lastLogin = lastLogin;
//        this.enabled = enabled;

    }

    /**
     * Finder to  make queries from database
     */

    public static Finder<String,User> find= new Finder<String, User>(
            String.class, User.class
    );

}
