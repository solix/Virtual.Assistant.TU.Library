package models;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import play.Logger;
import play.db.ebean.Model;
import plugins.S3Plugin;

import javax.persistence.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

@Entity
public class S3File extends Model {

    @Id
    public UUID id;

    private String bucket;

    public String name;

    @Transient
    public File file;


    public boolean owntemplate=false;

    @Version
    public Long version=0L;

    @ManyToOne
    public Project project;

    @ManyToOne
    public Person person;

    public static Model.Finder<UUID,S3File> find = new Model.Finder(
            UUID.class, S3File.class
    );

    public URL getUrl() throws MalformedURLException {
        return new URL("https://s3.amazonaws.com/" + bucket + "/" + getActualFileName());
    }

    private String getActualFileName() {
        return id + "/" + name;
    }

    @Override
    public void save() {
        if (S3Plugin.amazonS3 == null) {
            Logger.error("Could not save because amazonS3 was null");
            throw new RuntimeException("Could not save");
        }
        else {
            this.bucket = S3Plugin.s3Bucket;

            super.save(); // assigns an id

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, getActualFileName(), file);
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead); // public for all
            S3Plugin.amazonS3.putObject(putObjectRequest); // upload file
        }
    }

    @Override
    public void delete() {
        if (S3Plugin.amazonS3 == null) {
            Logger.error("Could not delete because amazonS3 was null");
            throw new RuntimeException("Could not delete");
        }
        else {
            S3Plugin.amazonS3.deleteObject(bucket, getActualFileName());
            super.delete();
        }
    }


    public static URL downloadOwnTemplate(Long pid) throws java.net.MalformedURLException{

        Project project = Project.find.byId(pid);
        S3File s3files= S3File.find.where().eq("project", project).eq("owntemplate", true).findUnique();
        return s3files.getUrl();
    }


    public static URL downloadarticleTemplate() throws java.net.MalformedURLException{

       URL url = new URL("https://s3.amazonaws.com/com.assistu.projects3/articleTemplate/template.doc");
        return url;
    }


}