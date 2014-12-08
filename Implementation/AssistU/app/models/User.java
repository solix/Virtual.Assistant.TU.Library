package models;

import javax.persistence.*;

import play.data.validation.Constraints;
import play.db.ebean.*;
import com.avaje.ebean.*;
import play.data.validation.Constraints.*;

@Entity
public class User extends Model {

//    @Required
//    public String username;
    @Id
    @Required
    @Email
    public String email;
    @Required
    public String password;
//    public Profile profile;
    public String titles;
    public String firstname;
    public String lastname;
    public String institution;
    public String contactemail;
    public String avatar;

    public User(String email, String username, String password,
                String titles, String firstname, String lastname,
                String institution, String contactemail) {
        this.email = email;
//        this.username = username;
        this.password = password;
//        this.profile = new Profile(titles, firstName, lastName, institution, contactEmail, avatar);
        this.titles = titles;
        this.firstname = firstname;
        this.lastname = lastname;
        this.institution = institution;
        this.contactemail = contactemail;
//        ADD AVATAR GENERATOR
    }

    public static Finder<String,User> find = new Finder<String,User>(
            String.class, User.class
    );

    public static User authenticate(String email,String password){
        return find.where().eq("email" , email).eq(
                "password",password).findUnique();
    }

    public User create(String email, String username, String password,
                String titles, String firstname, String lastname,
                String institution, String contactemail,
                String avatar) {
        User user = new User(email, username, password,
                titles, firstname, lastname,
                institution, contactemail);
        user.save();
        return user;
    }
}