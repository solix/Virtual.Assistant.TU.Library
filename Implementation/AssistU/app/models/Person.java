package models;


import javax.persistence.*;
import play.db.ebean.*;
import com.avaje.ebean.*;
import java.lang.String;
import java.util.*;


@Entity
public class Person{


    public String name;
    public String lastName;
    @Id
    public String email;
    public String username;
    public String password;

    public Person(String name,
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