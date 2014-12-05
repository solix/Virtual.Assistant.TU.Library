package models;

/**
 * DocumentFile model to save file upload information
 *
 *
 */
import javax.persistence.*;

import play.db.ebean.Model;

@Entity
public class DocumentFile extends Model{
    @Id @GeneratedValue
    public Long id;

    public String name;
     public String filepath;

    public DocumentFile(String name, String filepath){
        this.name=name;
        this.filepath=filepath;
    }

    /**
     * Finder to  make queries from database
     */
    public static Model.Finder<Long,DocumentFile> find = new Model.Finder(
            Long.class, DocumentFile.class
    );

    public static DocumentFile create(String name, String filepath){
        DocumentFile documentFile = new DocumentFile(name,filepath);
        documentFile.save();

        return documentFile;
    }
}
