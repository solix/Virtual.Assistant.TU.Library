package controllers;


import models.*;


import play.data.DynamicForm;
import play.mvc.*;
import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.io.IOException;
import java.lang.String;
import org.apache.commons.io.FileUtils;
import views.html.discussionFile;
import plugins.com.feth.play.module.pa.PlayAuthenticate;

public class DocumentData extends Controller {

    /**
     * Uploads a file to the amazon S3 file storage
     *
     * @return
     */
    public static Result upload(long pid) {
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if (user != null) {
            Project project = Project.find.byId(pid);

            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart uploadFilePart = body.getFile("upload");
            if (uploadFilePart != null) {
                S3File s3File = new S3File();
                s3File.name = uploadFilePart.getFilename();
                s3File.file = uploadFilePart.getFile();
                s3File.project=project;
                s3File.person=user;
                s3File.save();
                return redirect(controllers.routes.Application.project());
            } else {
                return badRequest("File upload error");
            }
        }
        //User did not have a session
        session().put("callback", routes.DocumentData.upload(pid).absoluteURL(request()));
        return Authentication.login();
    }
    /**
     * Deletes the file from file list inside the project
     * @param fid
     * @return
     */

    public static Result deleteDocument(UUID fid) {
        S3File documentFile = S3File.find.byId(fid);
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if (user != null) {
            if (documentFile.person.equals(user)) {
                documentFile.delete();
            }
            return ProjectData.project(documentFile.project.id);
        } else {
            //User did not have a session
            session().put("callback", routes.DocumentData.deleteDocument(fid).absoluteURL(request()));
            return Authentication.login();
        }
    }

    /**

     * Interlinks each file to  discussion
     * @param docid
     * @return
     */
    public static Result documentDiscussion(UUID docid) {
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if (user != null) {
            S3File df = S3File.find.byId(docid);
            Project p = Project.find.byId(df.project.id);
            DynamicForm message = new DynamicForm();
            return ok(discussionFile.render("Discuss " + df.name, user, p, df, message, false, "", ""));
        } else {
            //User did not have a session
            session().put("callback", routes.DocumentData.documentDiscussion(docid).absoluteURL(request()));
            return Authentication.login();
        }
    }


    /**
     * user uploads his own template to the project
     * @param pid
     * @return
     */
    public static Result uploadNewTemplate(Long pid) {
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if (user != null) {
            Project project = Project.find.byId(pid);
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart uploadFilePart = body.getFile("upload");
            if (uploadFilePart != null) {
                S3File s3File = new S3File();
                s3File.name = uploadFilePart.getFilename();
                s3File.file = uploadFilePart.getFile();
                s3File.project=project;
                s3File.person=user;
                s3File.save();
                    s3File.owntemplate = true;
                project.template = "Own";
                project.update();
                s3File.update();

                return redirect(controllers.routes.Application.project());
            } else {

                return badRequest(
                        "PLease provide a correct file"
                );
            }
        } else {
            //User did not have a session
            session().put("callback", routes.DocumentData.uploadNewTemplate(pid).absoluteURL(request()));
            return Authentication.login();
        }
    }









}