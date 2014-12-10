package models;

import javax.persistence.*;

import play.data.validation.Constraints;
import play.db.ebean.*;
import com.avaje.ebean.*;
import play.data.validation.Constraints.*;

@Entity
public class User extends Model {


    @Id
    public String email;
    public String password;


    public User(String email,  String password) {
        this.email = email;
        this.password = password;

    }

    public static Finder<String,User> find = new Finder<String,User>(
            String.class, User.class
    );

    public static User authenticate(String email,String password){
        return find.where().eq("email" , email).eq(
                "password",password).findUnique();
    }

    public User create(String email,  String password) {
        User user = new User(email,  password);
        user.save();
        return user;
    }
}