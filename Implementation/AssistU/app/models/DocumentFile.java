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
    /**
     * TODO: create enum file for types pdf,doc,docx,tx,etc.
     */
//    public static enum FileType {
//
//    }
    @Id @GeneratedValue
    public Long id;

    @Constraints.MaxLength(140)
    public String name;



    //ot persisited file memoization
    @Transient
    public File file;

    public String filepath;

    @Version
    public Long version=0L;

    @ManyToOne
    public Project project;

    /**
     * Constructor
     * @param name
     */
    public DocumentFile(String name, File file ,String filepath, long pid){
        this.name=name;
        this.filepath=filepath;
        this.file=file;
        this.project=Project.find.ref(pid);

    }

    /**
     * Finder to  make queries from database
     */
    public static Model.Finder<Long,DocumentFile> find = new Model.Finder(
            Long.class, DocumentFile.class
    );

    public static DocumentFile create(String name, File file  ,String filepath,long pid){
        Project project = Project.find.ref(pid);
        DocumentFile documentFile = new DocumentFile(name,file,filepath,project.id);
        documentFile.save();

        return documentFile;
    }
}
