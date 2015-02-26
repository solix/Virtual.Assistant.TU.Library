package functional_tests;

import models.Person;
import models.Project;
import models.TokenAction;
import org.junit.*;
import static org.junit.Assert.*;

import play.Logger;
import play.api.libs.Files;
import play.api.mvc.*;
import play.libs.Scala;
import play.mvc.*;
import play.mvc.Result;
import play.test.WithApplication;

import java.io.File;
import java.util.*;

import static play.test.Helpers.*;
import static play.test.Helpers.start;

/**
 * Created by arnaud on 4-12-14.
 */
public class DocumentTest extends WithApplication {

    @Test
    public void upload_test() {
        running(fakeApplication(new play.GlobalSettings()), new Runnable() {
            public void run() {
                //SIGNUP USER 1
                Map<String, String> signupform = new HashMap<String, String>();
                signupform.put("first_name", "Arnaud");
                signupform.put("last_name", "Hambenne");
                signupform.put("email", "arnaud@hambenne.com");
                signupform.put("password", "lalala");
                signupform.put("repeatPassword", "lalala");
                routeAndCall(fakeRequest(POST, "/signingup").withFormUrlEncodedBody(signupform));
                String token = TokenAction.find.where().eq("targetPerson", Person.find.where().eq("email", "arnaud@hambenne.com").findUnique()).findUnique().token;
                routeAndCall(fakeRequest(GET, "/accounts/verify/" + token));
                Map<String, String> loginform = new HashMap<String, String>();
                loginform.put("email", "arnaud@hambenne.com");
                loginform.put("password", "lalala");
                Result dologin1 = routeAndCall(fakeRequest(POST, "/logingin").withFormUrlEncodedBody(loginform));

                // RECOVER COOKIE FROM LOGIN RESULT
                final Http.Cookie playSession1 = play.test.Helpers.cookie("PLAY_SESSION", dologin1);

                //CREATE A PROJECT
                Map<String, String> newprojectform = new HashMap<String, String>();
                newprojectform.put("folder", "BEP");
                newprojectform.put("name", "Bachelor Eind Project");
                newprojectform.put("description", "Test Project");
                newprojectform.put("template", "TU Delft - Dissertation");
                routeAndCall(fakeRequest(POST, "/project/create").withFormUrlEncodedBody(newprojectform).withCookies(playSession1));

                Project project = Project.find.where().eq("folder", "BEP").findUnique();

                //UPLOAD A FILE
//                File file = new File("public/images/favicon.png");
//                Map<String, File> upload = new HashMap<String, File>();
//                upload.put("document", file);



            }
        });
    }
}