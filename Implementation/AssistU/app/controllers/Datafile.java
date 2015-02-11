package controllers;


import models.*;
import controllers.*;


import play.mvc.*;
import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData.*;
import java.io.File;

import java.io.IOException;
import java.lang.String;
import org.apache.commons.io.FileUtils;
import views.html.discussionFile;
import com.feth.play.module.pa.PlayAuthenticate;

public class Datafile extends Controller {

    /*TODO SOHEIL: Having a new document uploaded to your project should also be notified
    /**
     * POST uploaded document  to the server
     */
    public static Result uploadDocument(long pid) {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Project project=Project.find.byId(pid);
        MultipartFormData body = request().body().asMultipartFormData();
        //play api to get the file
        FilePart document = body.getFile("document");
        if (document != null) {
            String fileName = document.getFilename();
            String contentType = document.getContentType();
            File file = document.getFile();
            try {
                //this creates folder and  will be changed in future to the name of the project
                //String pname=project.folder;

                FileUtils.moveFile(file, new File("/Users/soheil/Desktop/libUpload/"+project.folder, fileName));
            } catch (IOException ioe) {
                System.out.println("Problem operating on filesystem");
            }
            String filepath = document.getFile().toString();
            DocumentFile doc = DocumentFile.create(fileName ,file,file.getPath(),project.id, user.id);


            return redirect(controllers.routes.Application.project());
        } else {

            return badRequest(
                    "PLease provide a correct file"
            );
        }
    }



    /**
     * Download the document file
     */

    public static Result downloadDocument(Long id){

        //this creates folder and  will be changed in future to the name of the project
        //String pname="projectfolder";
        DocumentFile documentFile = DocumentFile.find.byId(id);
        Project project = Project.find.where().in("documentFiles",documentFile).findUnique();
        String path ="/Users/soheil/Desktop/libUpload/"+project.folder;
        return  ok(new File(path,documentFile.name));
    }

    public static Result deleteDocument(Long fid){
        DocumentFile documentFile = DocumentFile.find.byId(fid);
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(documentFile.user.equals(user)){
            documentFile.delete();
        }
        return ProjectData.project(documentFile.project.id);
    }

    public static Result documentDiscussion(Long docid){
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        DocumentFile df = DocumentFile.find.byId(docid);
        Project p =Project.find.byId(df.project.id);
        return ok(discussionFile.render("Discuss " + df.name, user, p, df, false, ""));
    }

    public static Result downloadTemplate(){
        String temple="template.doc";
        String path ="/Users/soheil/Desktop/libtempl/";
        return  ok(new File(path,temple));
    }

    public static Result uploadNewTemplate(long pid) {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        Project project=Project.find.byId(pid);
        MultipartFormData body = request().body().asMultipartFormData();
        //play api to get the file
        FilePart document = body.getFile("document");
        if (document != null) {
            String fileName = document.getFilename();
            String contentType = document.getContentType();
            File file = document.getFile();
            try {
                //this creates folder and  will be changed in future to the name of the project
                //String pname=project.folder;

                FileUtils.moveFile(file, new File("/Users/soheil/Desktop/libtempl/Owntemplates/"+project.folder, fileName));
            } catch (IOException ioe) {
                System.out.println("Problem operating on filesystem");
            }
            String filepath = document.getFile().toString();
            DocumentFile doc = DocumentFile.create(fileName ,file,file.getPath(),project.id, user.id);
            doc.owntemplate=true;
            project.template="Own";
            project.update();
            doc.update();


            return redirect(controllers.routes.Application.project());
        } else {

            return badRequest(
                    "PLease provide a correct file"
            );
        }
    }

    public static Result downloadOwnTemplate(Long id){


        DocumentFile documentFile = DocumentFile.find.byId(id);
        Project project = Project.find.where().in("template","Own").in("documentFiles",documentFile).findUnique();
        String path ="/Users/soheil/Desktop/libtempl/Owntemplates/"+project.folder;

        return  ok(new File(path,documentFile.name));


    }

}