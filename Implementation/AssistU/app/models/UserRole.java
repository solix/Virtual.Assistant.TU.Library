package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.*;
import java.util.ArrayList;

/**
 * This class defines the role for each member involving in the project
 */
@Entity
public class UserRole{
   @Id
    public final String name;




    public UserRole(final String name){
        this.name = name;

    }



}