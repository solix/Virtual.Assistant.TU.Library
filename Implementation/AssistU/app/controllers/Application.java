package controllers;


import play.mvc.*;

import views.html.*;
import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData.*;
import java.io.File;

public class Application extends Controller {
    /**
     * index view
     *
     * @return
     */
    public static Result index() {
        return ok(index.render("welcome name"));
    }

    /**
     *Task view
     *
     * @return
     */
    public static Result task() {

        return ok(task.render("your tasks"));
    }

    /**
     * login page
     *
     * @return
     */
    public static Result login() {
        return ok(login.render());
    }

    /**
     * Calendar page
     * @return
     */
    public static Result calendar() {

        return ok(calendar.render("My Calendar"));
    }

    /**
     * project page
     * @return
     */

    public static Result project() {

        return ok(project.render("My Projects"));
    }

    /**
     * suggestion page
     * @return
     */
    public static Result suggestions() {

        return TODO;
    }

    /**
     * chat page
     * @return
     */
    public static Result discussion() {

        return ok(discussion.render("gap"));
    }
      /**
      * POST uploaded document  to the server
      */
    public static Result uploadDocument() {
      MultipartFormData body = request().body().asMultipartFormData();
      FilePart document = body.getFile("document");
      if (document != null) {
        String fileName = document.getFilename();
        String contentType = document.getContentType(); 
        File file = document.getFile();
        return ok("File uploaded");
      } else {
        flash("error", "Missing file");
        return redirect(routes.Application.index());    
      }
}
}
