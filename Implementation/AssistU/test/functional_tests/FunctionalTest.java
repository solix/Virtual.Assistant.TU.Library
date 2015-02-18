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

                //SIGNUP USER 1
                Map<String, String> signupform = new HashMap<String, String>();
                signupform.put("first_name", "Arnaud");
                signupform.put("last_name", "Hambenne");
                signupform.put("email", "arnaud@hambenne.com");
                signupform.put("password", "lalala");
                signupform.put("repeatPassword", "lalala");
                Result submission = routeAndCall(fakeRequest(POST, "/signingup").withFormUrlEncodedBody(signupform));
                Person user = Person.find.where().eq("email", "arnaud@hambenne.com").findUnique();
                assertEquals("arnaud@hambenne.com", user.email);
                assertEquals(false, user.emailValidated);
                String token = TokenAction.find.where().eq("targetPerson", Person.find.where().eq("email", "arnaud@hambenne.com").findUnique()).findUnique().token;
                Result verifiedpage = routeAndCall(fakeRequest(GET, "/accounts/verify/" + token));
                assertTrue(status(verifiedpage) == OK);
                user = Person.find.where().eq("email", "arnaud@hambenne.com").findUnique();
                assertEquals(true, user.emailValidated);
                Map<String, String> loginform = new HashMap<String, String>();
                loginform.put("email", "arnaud@hambenne.com");
                loginform.put("password", "lalala");
                Result dologin1 = routeAndCall(fakeRequest(POST, "/logingin").withFormUrlEncodedBody(loginform));

                // RECOVER COOKIE FROM LOGIN RESULT
                final Http.Cookie playSession1 = play.test.Helpers.cookie("PLAY_SESSION", dologin1);

                //SIGNUP USER 2
                signupform.put("first_name", "Soheil");
                signupform.put("last_name", "Jahanshahi");
                signupform.put("email", "soheil@jahanshahi.com");
                signupform.put("password", "lalala");
                signupform.put("repeatPassword", "lalala");
                routeAndCall(fakeRequest(POST, "/signingup").withFormUrlEncodedBody(signupform));
                Person user2 = Person.find.where().eq("email", "soheil@jahanshahi.com").findUnique();
                assertEquals("soheil@jahanshahi.com", user2.email);
                assertEquals(false, user2.emailValidated);
                token = TokenAction.find.where().eq("targetPerson", Person.find.where().eq("email", "soheil@jahanshahi.com").findUnique()).findUnique().token;
                verifiedpage = routeAndCall(fakeRequest(GET, "/accounts/verify/" + token));
                assertTrue(status(verifiedpage) == OK);
                user2 = Person.find.where().eq("email", "soheil@jahanshahi.com").findUnique();
                assertEquals(true, user2.emailValidated);
                loginform = new HashMap<String, String>();
                loginform.put("email", "soheil@jahanshahi.com");
                loginform.put("password", "lalala");
                Result dologin2 = routeAndCall(fakeRequest(POST, "/logingin").withFormUrlEncodedBody(loginform));

                // RECOVER COOKIE FROM LOGIN RESULT
                final Http.Cookie playSession2 = play.test.Helpers.cookie("PLAY_SESSION", dologin2);

                Result indexpagenosession = route(fakeRequest("GET", "/"));
                assertTrue(status(indexpagenosession) == OK);
                Result indexpage = route(fakeRequest("GET", "/").withCookies(playSession1));
                assertTrue(status(indexpage) == OK);



                Result taskpage = route(fakeRequest("GET", "/tasks").withCookies(playSession1));
                assertTrue(status(taskpage) == OK);

                //PROJECTS
                Result projectpagenosession = route(fakeRequest("GET", "/projects"));
                assertTrue(status(projectpagenosession) == OK);
                Result projectpage = route(fakeRequest("GET", "/projects").withCookies(playSession1));
                assertTrue(status(projectpage) == OK);

                //CREATE A PROJECT
                Result createprojectpagenosession = route(fakeRequest("GET", "/project/new"));
                assertTrue(status(createprojectpagenosession) == OK);
                Result createprojectpage = route(fakeRequest("GET", "/project/new").withCookies(playSession1));
                assertTrue(status(createprojectpage) == OK);
                Map<String, String> newprojectform = new HashMap<String, String>();
                newprojectform.put("folder", "BEP");
                newprojectform.put("name", "");
                newprojectform.put("description", "Test Project");
                newprojectform.put("template", "TU Delft - Dissertation");
                Result createprojectnosession = routeAndCall(fakeRequest(POST, "/project/create").withFormUrlEncodedBody(newprojectform));
                assertTrue(status(createprojectnosession) == OK);
                Result createprojectfail1 = routeAndCall(fakeRequest(POST, "/project/create").withFormUrlEncodedBody(newprojectform).withCookies(playSession1));
                assertTrue(status(createprojectfail1) == BAD_REQUEST);
                newprojectform.put("name", "xx");
                Result createprojectfail2 = routeAndCall(fakeRequest(POST, "/project/create").withFormUrlEncodedBody(newprojectform).withCookies(playSession1));
                assertTrue(status(createprojectfail2) == BAD_REQUEST);
                newprojectform.put("name", "Bachelor Eind Project");
                Result createprojectsuccess = routeAndCall(fakeRequest(POST, "/project/create").withFormUrlEncodedBody(newprojectform).withCookies(playSession1));
                assertTrue(status(createprojectsuccess) == 303);

                Project project = Project.find.where().eq("folder", "BEP").findUnique();
                assertTrue(project.name.equals("Bachelor Eind Project"));

                //LOAD NEW PROJECT
                Result specificprojectpagenosession = route(fakeRequest("GET", "/project/" + project.id));
                assertTrue(status(specificprojectpagenosession) == OK);
                Result specificprojectpage = route(fakeRequest("GET", "/project/" + project.id).withCookies(playSession1));
                assertTrue(status(specificprojectpage) == OK);

                //EDIT PROJECT
                Result editprojectpagenosession = route(fakeRequest("GET", "/project/" + project.id + "/edit"));
                assertTrue(status(editprojectpagenosession) == OK);
                Result editprojectpage = route(fakeRequest("GET", "/project/" + project.id + "/edit").withCookies(playSession1));
                assertTrue(status(editprojectpage) == OK);
                newprojectform.put("name", "");
                Result editprojectsubmitnosession = routeAndCall(fakeRequest(GET, "/project/" + project.id + "/editing").withFormUrlEncodedBody(newprojectform));
                assertTrue(status(editprojectsubmitnosession) == OK);
                Result editprojectsubmitfail1 = routeAndCall(fakeRequest(GET, "/project/" + project.id + "/editing").withFormUrlEncodedBody(newprojectform).withCookies(playSession1));
                assertTrue(status(editprojectsubmitfail1) == BAD_REQUEST);
                newprojectform.put("name", "xx");
                Result editprojectsubmitfail2 = routeAndCall(fakeRequest(GET, "/project/" + project.id + "/editing").withFormUrlEncodedBody(newprojectform).withCookies(playSession1));
                assertTrue(status(editprojectsubmitfail2) == BAD_REQUEST);
                newprojectform.put("name", "Bachelor Project");
                Result editprojectsubmitsuccess = routeAndCall(fakeRequest(GET, "/project/" + project.id + "/editing").withFormUrlEncodedBody(newprojectform).withCookies(playSession1));
                assertTrue(status(editprojectsubmitsuccess) == 303);
                project = Project.find.where().eq("folder", "BEP").findUnique();
                assertTrue(project.name.equals("Bachelor Project"));

                //INVITE SOMEONE AS GUEST
                Map<String, String> invitememberform = new HashMap<String, String>();
                invitememberform.put("email", "soheil@jahanshahi.com");
                invitememberform.put("role", "Guest");
                Result addmembersuccess = routeAndCall(fakeRequest(GET, "/project/" + project.id + "/addmember").withFormUrlEncodedBody(invitememberform).withCookies(playSession1));
                assertTrue(status(addmembersuccess) == 303);

                //HE DECLINES
                Result declineinvivation = routeAndCall(fakeRequest(GET, "/project/" + project.id + "/declineinvitation").withFormUrlEncodedBody(invitememberform).withCookies(playSession2));
                assertTrue(status(declineinvivation) == 303);

                //INVITE SOMEONE AS REVIEWER
                invitememberform.put("role", "Reviewer");
                addmembersuccess = routeAndCall(fakeRequest(GET, "/project/" + project.id + "/addmember").withFormUrlEncodedBody(invitememberform).withCookies(playSession1));
                assertTrue(status(addmembersuccess) == 303);

                //HE ACCEPTS
                Result acceptinvivation = routeAndCall(fakeRequest(GET, "/project/" + project.id + "/acceptinvitation").withFormUrlEncodedBody(invitememberform).withCookies(playSession2));
                assertTrue(status(acceptinvivation) == 303);

                //REMOVE HIM
                Result removemember = routeAndCall(fakeRequest(GET, "/project/" + project.id + "/removemember/" + user2.id).withFormUrlEncodedBody(invitememberform).withCookies(playSession2));
                assertTrue(status(removemember) == 303);

                //INVITE SOMEONE AS OWNER
                invitememberform.put("role", "Owner");
                addmembersuccess = routeAndCall(fakeRequest(GET, "/project/" + project.id + "/addmember").withFormUrlEncodedBody(invitememberform).withCookies(playSession1));
                assertTrue(status(addmembersuccess) == 303);

                //HE ACCEPTS
                acceptinvivation = routeAndCall(fakeRequest(GET, "/project/" + project.id + "/acceptinvitation").withFormUrlEncodedBody(invitememberform).withCookies(playSession2));
                assertTrue(status(acceptinvivation) == 303);

                //HE LEAVES
                Result leaveproject = routeAndCall(fakeRequest(GET, "/project/" + project.id + "/leave").withFormUrlEncodedBody(invitememberform).withCookies(playSession2));
                assertTrue(status(leaveproject) == 303);

                //CALENDAR
                Result calendarpage = route(fakeRequest("GET", "/calendar").withCookies(playSession1));
                assertTrue(status(calendarpage) == OK);

                //DISCUSSIONS
                Result discussionpage = route(fakeRequest("GET", "/discussions").withCookies(playSession1));
                assertTrue(status(discussionpage) == OK);
            }
        });
    }
}
