package models;

/**
 * DocumentFile model to save file upload information
 *
 *
 */
import javax.persistence.*;

import play.data.validation.Constraints;
import play.db.ebean.Model;

import java.io.File;

@Entity
public class DocumentFile extends Model{

//    public static enum FileType {
//
//    }
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Constraints.MaxLength(140)
    public String name;

    //ot persisited file memoization
    @Transient
    public File file;

    public String filepath;

    /**
     * Constructor
     * @param filename
     * @param filepath
     */
    public DocumentFile(String filename, String filepath , File file){
        this.name=filename;
        this.filepath=filepath;
        this.file=file;
    }

    /**
     * Finder to  make queries from database
     */
    public static Model.Finder<Long,DocumentFile> find = new Model.Finder(
            Long.class, DocumentFile.class
    );

    public static DocumentFile create(String name, String filepath,  File file){
        DocumentFile documentFile = new DocumentFile(name,filepath ,file);
        documentFile.save();

        return documentFile;
    }
}
