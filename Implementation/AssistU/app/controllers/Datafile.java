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





public class Datafile extends Controller {

    /**
     * POST uploaded document  to the server
     */
    public static Result uploadDocument(long pid) {
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
            DocumentFile doc = DocumentFile.create(fileName ,file,file.getPath(),project.id);


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

    public static Result downloadTemplate(){


        String temple="template.doc";
        String path ="/Users/soheil/Desktop/libtempl/";

        return  ok(new File(path,temple));


    }
}