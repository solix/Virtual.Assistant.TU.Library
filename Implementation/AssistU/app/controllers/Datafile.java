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
import java.lang.String;




public class Datafile extends Controller {

    /**
     * POST uploaded document  to the server
     */
    public static Result uploadDocument() {

        MultipartFormData body = request().body().asMultipartFormData();
        //play api tp get the file
        FilePart document = body.getFile("document");
        if (document != null) {
            String fileName = document.getFilename();
            String contentType = document.getContentType();
            File file = document.getFile();
            String filepath = document.getFile().toString();
            DocumentFile doc = DocumentFile.create(fileName ,filepath,file);
            return redirect(routes.Application.project());
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

        DocumentFile documentFile = DocumentFile.find.byId(id);

            return  ok(new File(documentFile.filepath));


    }
}