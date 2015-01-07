package models;

import javax.persistence.*;

import play.data.validation.Constraints;
import play.db.ebean.*;
import com.avaje.ebean.*;
import play.data.validation.Constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User extends Model {


    @Id @GeneratedValue
    public Long id;
    public String email;
    public String name;
    public String first_name;
    public String last_name;
    public String password;

//  Extra attributes for social users
    public String socialId;
    public String socialKey;

    @ManyToMany
    List<UserRole> userroles = new ArrayList<UserRole>();

    @ManyToMany
    List<Project> projects = new ArrayList<Project>();

    @OneToMany(mappedBy = "user")
    public List<Task> tasks= new ArrayList<Task>();

    public User(String name, String email, String password, String socialId, String socialKey) {
        this.name=name;
        this.email = email;
        this.password = password;
        this.socialId = socialId;
        this.socialKey = socialKey;
    }

    public static Model.Finder<String,User> find = new Model.Finder(
            String.class, User.class
    );

    /**
     * autheticates  user
     * @param email
     * @param password
     * @return
     */
    public static User authenticate(String email, String password){
        return find.where().eq("email", email).eq("password", password).findUnique();
    }

    public static User create(String name, String email, String password, String socialId, String socialKey) {
        User user = new User(name, email, password, socialId, socialKey);
        user.save();
        return user;
    }
}