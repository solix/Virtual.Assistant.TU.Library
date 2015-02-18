package functional_tests;

import controllers.Account;
import controllers.Application;
import controllers.Signup;
import models.*;
import org.junit.*;
import static org.junit.Assert.*;

import play.Logger;
import play.api.GlobalSettings;
import play.api.test.FakeApplication;
import play.mvc.*;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;

import static play.test.Helpers.*;
import static play.test.Helpers.start;
import controllers.routes;

/**
 * Created by arnaud on 4-12-14.
 */
public class FunctionalTest extends WithApplication {

    @Test
    public void test() {
        running(fakeApplication(new play.GlobalSettings()), new Runnable() {
            public void run() {
                Result initialpage = route(fakeRequest("GET", "/signup"));
                assertTrue(status(initialpage) == OK);
                Map<String, String> signupform = new HashMap<String, String>();
                signupform.put("first_name", "Arnaud");
                signupform.put("last_name", "Hambenne");
                signupform.put("email", "arnaud@hambenne.com");
                signupform.put("password", "lalala");
                signupform.put("repeatPassword", "lalala");
                Result submission = routeAndCall(fakeRequest(POST, "/signingup").withFormUrlEncodedBody(signupform));
                User user = User.find.where().eq("email", "arnaud@hambenne.com").findUnique();
                assertEquals("arnaud@hambenne.com", user.email);
                assertEquals(false, user.emailValidated);
                String token = TokenAction.find.where().eq("targetUser", User.find.where().eq("email", "arnaud@hambenne.com").findUnique()).findUnique().token;
                Result verifiedpage = routeAndCall(fakeRequest(GET, "/accounts/verify/" + token));
                assertTrue(status(verifiedpage) == OK);
                user = User.find.where().eq("email", "arnaud@hambenne.com").findUnique();
                assertEquals(true, user.emailValidated);
                Map<String, String> loginform = new HashMap<String, String>();
                loginform.put("email", "arnaud@hambenne.com");
                loginform.put("password", "lalala");
                Result dologin = routeAndCall(fakeRequest(POST, "/logingin").withFormUrlEncodedBody(loginform));

                // RECOVER COOKIE FROM LOGIN RESULT
                final Http.Cookie playSession = play.test.Helpers.cookie("PLAY_SESSION", dologin);

                Result indexpagenosession = route(fakeRequest("GET", "/"));
                assertTrue(status(indexpagenosession) == OK);
                Result indexpage = route(fakeRequest("GET", "/").withCookies(playSession));
                assertTrue(status(indexpage) == OK);



                Result taskpage = route(fakeRequest("GET", "/tasks").withCookies(playSession));
                assertTrue(status(taskpage) == OK);

                //PROJECTS
                Result projectpagenosession = route(fakeRequest("GET", "/project"));
                assertTrue(status(projectpagenosession) == OK);
                Result projectpage = route(fakeRequest("GET", "/project").withCookies(playSession));
                assertTrue(status(projectpage) == OK);

                //CREATE A PROJECT
                Result createprojectpagenosession = route(fakeRequest("GET", "/project/new"));
                assertTrue(status(createprojectpagenosession) == OK);
                Result createprojectpage = route(fakeRequest("GET", "/project/new").withCookies(playSession));
                assertTrue(status(createprojectpage) == OK);
                Map<String, String> newprojectform = new HashMap<String, String>();
                newprojectform.put("folder", "BEP");
                newprojectform.put("name", "");
                newprojectform.put("description", "Test Project");
                newprojectform.put("template", "TU Delft - Dissertation");
                Result createprojectnosession = routeAndCall(fakeRequest(POST, "/project/create").withFormUrlEncodedBody(newprojectform));
                assertTrue(status(createprojectnosession) == OK);
                Result createprojectfail1 = routeAndCall(fakeRequest(POST, "/project/create").withFormUrlEncodedBody(newprojectform).withCookies(playSession));
                assertTrue(status(createprojectfail1) == BAD_REQUEST);
                newprojectform.put("name", "xx");
                Result createprojectfail2 = routeAndCall(fakeRequest(POST, "/project/create").withFormUrlEncodedBody(newprojectform).withCookies(playSession));
                assertTrue(status(createprojectfail2) == BAD_REQUEST);
                newprojectform.put("name", "Bachelor Eind Project");
                Result createprojectsuccess = routeAndCall(fakeRequest(POST, "/project/create").withFormUrlEncodedBody(newprojectform).withCookies(playSession));
//                assertTrue(status(createprojectsuccess) == OK);

                Project project = Project.find.where().eq("folder", "BEP").findUnique();
                assertTrue(project.name.equals("Bachelor Eind Project"));

                //LOAD NEW PROJECT
                Result specificprojectpage = route(fakeRequest("GET", "/project/" + project.id).withCookies(playSession));
                assertTrue(status(specificprojectpage) == OK);

                //EDIT PROJECT
                Result editprojectpage = route(fakeRequest("GET", "/project/" + project.id + "/edit").withCookies(playSession));
                assertTrue(status(editprojectpage) == OK);
                newprojectform.put("name", "Bachelor Project");
                Result editprojectsubmit = routeAndCall(fakeRequest(GET, "/project/" + project.id + "/editing").withFormUrlEncodedBody(newprojectform).withCookies(playSession));
                project = Project.find.where().eq("folder", "BEP").findUnique();
                assertTrue(project.name.equals("Bachelor Project"));

                //CALENDAR
                Result calendarpage = route(fakeRequest("GET", "/calendar").withCookies(playSession));
                assertTrue(status(calendarpage) == OK);

                //DISCUSSIONS
                Result discussionpage = route(fakeRequest("GET", "/discussions").withCookies(playSession));
                assertTrue(status(discussionpage) == OK);
            }
        });
    }
}