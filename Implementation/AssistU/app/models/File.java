package models;

import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
play.db.jpa.Blob;

@Entity
public class File extends Model{

    @Id
    public long version;


    public String name;
    @Required
    public blob file;

    public File(String name){
        this.name = name;
    }

}