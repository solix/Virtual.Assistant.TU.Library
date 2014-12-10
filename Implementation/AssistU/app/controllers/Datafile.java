package controllers;


import models.*;

import play.data.*;
import play.mvc.*;
import views.html.*;
import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.String;
import org.apache.commons.io.FileUtils;





public class Datafile extends Controller {

    /**
     * POST uploaded document  to the server
     */
    public static Result uploadDocument() {

        MultipartFormData body = request().body().asMultipartFormData();
        //play api to get the file
        FilePart document = body.getFile("document");
        if (document != null) {
            String fileName = document.getFilename();
            String contentType = document.getContentType();
            File file = document.getFile();
            try {
                //this creates folder and  will be changed in future to the name of the project
                String pname="projectfolder";

                FileUtils.moveFile(file, new File("/home/"+pname, fileName));
            } catch (IOException ioe) {
                System.out.println("Problem operating on filesystem");
            }
            String filepath = document.getFile().toString();
            DocumentFile doc = DocumentFile.create(fileName ,filepath,file);
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
        String pname="projectfolder";
        DocumentFile documentFile = DocumentFile.find.byId(id);
        String path ="/home/"+pname;

        return  ok(new File(path,documentFile.name));


    }
}