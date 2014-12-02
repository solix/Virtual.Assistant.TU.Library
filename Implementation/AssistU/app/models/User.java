package models;

import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;

import java.lang.Override;
import java.lang.String;

@Entity
public class User extends Model{
    /**
     * list of user attributes
     */

    @Id
    public String uid;
    public String name;
    public String lastName;
    public String email;
    public String userName;
    public String password;
    /**
     * bookkeeping for user activity
     */
    @Temporal(TemporalType.DATE)
    public Date createdAt;
    @Temporal(TemporalType.DATE)
    public Date updatedAt;
    @Temporal(TemporalType.DATE)
    public Date lastLogin;

    public User(
            String uid,
            String name,
            String lastName,
            String email,
            String userName,
            String password,
            Date createdAT,
            Date updatedAt,
            Date ){

    }

}
