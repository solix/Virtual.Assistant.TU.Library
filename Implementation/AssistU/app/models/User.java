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


    @Id
    public String email;
    public String name;
    public String password;

    @ManyToMany
    List<UserRole> userroles=new ArrayList<UserRole>();

    @ManyToMany
    List<Project> projects = new ArrayList<Project>();

    @OneToMany(mappedBy = "user")
    public List<Task> tasks= new ArrayList<Task>();

    public User(String name, String email,  String password) {
        this.name=name;
        this.email = email;
        this.password = password;

    }

    public static Model.Finder<String,User> find = new Model.Finder(
            String.class, User.class
    );

    public static User authenticate(String email,String password){
        return find.where().eq("email" , email).eq(
                "password",password).findUnique();
    }

    public User create(String name,String email,  String password) {
        User user = new User(name,email,  password);
        user.save();
        return user;
    }
}