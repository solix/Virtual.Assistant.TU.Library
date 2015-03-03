package models;

import play.data.validation.Constraints;
import play.db.ebean.Model;
import plugins.providers.mendeley.MendeleyAuthUser;

import javax.persistence.*;
import java.util.*;

@Entity
public class MendeleyDocument extends Model {

    @Id
    public String id;
    public String title;
    public String type;
    public List<String> authors = new ArrayList<String>();
    public String year;

    @ManyToMany
    public List<Person> persons;

    public MendeleyDocument(String id, Long uid, String title, String type, List<String> authors_new, String year){
        this.id = id;
        this.title = title;
        this.type = type;
        for(String author : authors_new){
            this.authors.add(author);
        }
        this.year = year;
        this.persons.add(Person.find.byId(uid));
    }

    public static Model.Finder<Long,MendeleyDocument> find = new Model.Finder(
            Long.class, MendeleyDocument.class
    );

    public static MendeleyDocument create(String id, Long uid, String title, String type, List<String> authors_new, String year){
        MendeleyDocument mendeley_document = new MendeleyDocument(id, uid, title, type, authors_new, year);
        mendeley_document.save();
        return mendeley_document;
    }
}