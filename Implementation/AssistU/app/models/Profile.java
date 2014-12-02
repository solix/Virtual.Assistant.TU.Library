package models;


import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;
import java.lang.String;
import java.util.*;


@Entity
public class Profile{


    @Id
    public String email;
    public String name;
    public String lastName;
    public String username;
    public String password;

    public Profile(String name,
                  String lastName,
                  String email,
                  String username,
                  String password){
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
    }




}