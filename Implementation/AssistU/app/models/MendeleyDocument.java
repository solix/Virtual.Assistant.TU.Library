package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.Logger;
import play.api.libs.json.Json;
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
    public String doctype;
    public String authors;
    public String year;
    @Constraints.Required @Column(columnDefinition="TEXT")
    public String nodeData;

    @ManyToOne
    public Person person;

    public MendeleyDocument(Long uid, JsonNode nodeData){
        Logger.debug("CREATING MENDELEY DOCUMENT: " + nodeData.toString());
        this.nodeData = nodeData.toString();
        this.id = nodeData.get("id").asText();
        this.title = nodeData.get("title").asText();
        this.doctype = nodeData.get("type").asText();
        if(nodeData.has("authors")) {
            String authors = "";
            JsonNode authorsNode = nodeData.get("authors");
            for (JsonNode author : authorsNode) {
                authors = authors + author.get("last_name").asText();
                if (!authorsNode.get(authorsNode.size() - 1).equals(author)) {
                    authors = authors + ", ";
                }
            }
            this.authors = authors;
        }
        if(nodeData.has("year")) {
            this.year = nodeData.get("year").asText();
        }
        this.person = Person.find.byId(uid);
//        Logger.debug("NODEDATA: " + Json.parse(this.nodeData).toString());
    }

    public static Model.Finder<Long,MendeleyDocument> find = new Model.Finder(
            Long.class, MendeleyDocument.class
    );

    public static MendeleyDocument create(Long uid, JsonNode nodeData){
        MendeleyDocument mendeley_document = new MendeleyDocument(uid, nodeData);
//        Person person = Person.find.byId(uid);
//        person.mendeleydocuments.add(mendeley_document);
//        person.update();
        mendeley_document.save();
        return mendeley_document;
    }
}