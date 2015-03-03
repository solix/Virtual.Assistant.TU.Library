package functional_tests;

import models.Person;
import models.Project;
import models.TokenAction;
import org.junit.*;
import static org.junit.Assert.*;

import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;

import static play.test.Helpers.*;
import static play.test.Helpers.start;

/**
 * Created by arnaud on 4-12-14.
 */
public class PersonTest extends WithApplication {

    /**
     * This test tests the deleting a user functionality
     */
    @Test
    public void delete_user_test() {
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
                newprojectform.put("name", "");
                newprojectform.put("description", "Test Project");
                newprojectform.put("template", "TU Delft - Dissertation");
                Result createprojectsuccess = routeAndCall(fakeRequest(POST, "/project/create").withFormUrlEncodedBody(newprojectform).withCookies(playSession1));

                //DELETE USER
                Result deleteuser = routeAndCall(fakeRequest(GET, "/deleteaccount").withCookies(playSession1));
                Person user = Person.find.where().eq("email", "arnaud@hambenne.com").findUnique();
                assertNull(user);
                Project project = Project.find.where().eq("folder", "BEP").findUnique();
                assertNull(project);
            }
        });
    }
}