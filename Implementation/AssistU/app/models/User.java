package models;

import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;
import java.lang.String;
import java.util.*;

@Entity
public class User extends Model{
    /**
     * list of user attributes
     */

    @Id
    public String uid;
    Person userProfile;
    /**
     * bookkeeping for user activity
     */
    @Temporal(TemporalType.DATE)
    public Date createdAt;
    @Temporal(TemporalType.DATE)
    public Date lastLogin;

    public User(
            String uid,
            Person userProfile,
            Date createdAT,
            Date lastLogin){

        this.uid = uid;
        this.userProfile=userProfile;
        this.createdAt = createdAT;
        this.lastLogin = lastLogin;

    }

}
