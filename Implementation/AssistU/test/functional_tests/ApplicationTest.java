package functional_tests;

import models.Person;
import models.TokenAction;
import org.junit.*;
import static org.junit.Assert.*;

import play.Logger;
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
public class ApplicationTest extends WithApplication {

    @Test
    public void reroute_test() {
        running(fakeApplication(new play.GlobalSettings()), new Runnable() {
                    public void run() {
                        //SIGNUP USER
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

                        //VERIFY USER
                        String token = TokenAction.find.where().eq("targetPerson", Person.find.where().eq("email", "arnaud@hambenne.com").findUnique()).findUnique().token;
                        Result verifiedpage = routeAndCall(fakeRequest(GET, "/accounts/verify/" + token));
                        assertTrue(status(verifiedpage) == OK);
                        user = Person.find.where().eq("email", "arnaud@hambenne.com").findUnique();
                        assertEquals(true, user.emailValidated);

                        //LOGIN USER
                        Map<String, String> loginform = new HashMap<String, String>();
                        loginform.put("email", "arnaud@hambenne.com");
                        loginform.put("password", "lalala");
                        Result dologin = routeAndCall(fakeRequest(POST, "/logingin").withFormUrlEncodedBody(loginform));

                        //RECOVER COOKIE FROM LOGIN RESULT
                        final Http.Cookie playSession = play.test.Helpers.cookie("PLAY_SESSION", dologin);

                        //REROUTE WITHOUT SESSION
                        Result reroutenosession = route(fakeRequest("GET", "/redirect"));
                        assertTrue(status(reroutenosession) == OK);

                        //REROUTE WITH SESSION
                        Result reroute = route(fakeRequest("GET", "/redirect").withCookies(playSession));
                        assertTrue(status(reroute) == OK);
                    }
                }
        );
    }

    @Test
    public void session_test() {
        running(fakeApplication(new play.GlobalSettings()), new Runnable() {
                    public void run() {
                        //SIGNUP USER
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

                        //VERIFY USER
                        String token = TokenAction.find.where().eq("targetPerson", Person.find.where().eq("email", "arnaud@hambenne.com").findUnique()).findUnique().token;
                        Result verifiedpage = routeAndCall(fakeRequest(GET, "/accounts/verify/" + token));
                        assertTrue(status(verifiedpage) == OK);
                        user = Person.find.where().eq("email", "arnaud@hambenne.com").findUnique();
                        assertEquals(true, user.emailValidated);

                        //LOGIN USER
                        Map<String, String> loginform = new HashMap<String, String>();
                        loginform.put("email", "arnaud@hambenne.com");
                        loginform.put("password", "lalala");
                        Result dologin = routeAndCall(fakeRequest(POST, "/logingin").withFormUrlEncodedBody(loginform));

                        //RECOVER COOKIE FROM LOGIN RESULT
                        final Http.Cookie playSession = play.test.Helpers.cookie("PLAY_SESSION", dologin);

                        //MAIN PAGE WITHOUT SESSION
                        Result indexpagenosession = route(fakeRequest("GET", "/"));
                        assertTrue(status(indexpagenosession) == OK);

                        //MAIN PAGE WITH SESSION
                        Result indexpage = route(fakeRequest("GET", "/").withCookies(playSession));
                        assertTrue(status(indexpage) == OK);

                        //PROJECT PAGE WITHOUT SESSION
                        Result projectpagenosession = route(fakeRequest("GET", "/projects"));
                        assertTrue(status(projectpagenosession) == OK);

                        //PROJECT PAGE WITH SESSION
                        Result projectpage = route(fakeRequest("GET", "/projects").withCookies(playSession));
                        assertTrue(status(projectpage) == OK);

                        //CALENDAR PAGE WITHOUT SESSION
                        Result calendarpagenosession = route(fakeRequest("GET", "/calendar"));
                        assertTrue(status(calendarpagenosession) == OK);

                        //CALENDAR PAGE WITH SESSION
                        Result calendarpage = route(fakeRequest("GET", "/calendar").withCookies(playSession));
                        assertTrue(status(calendarpage) == OK);

                        //DISCUSSION PAGE WITHOUT SESSION
                        Result discussionpagenosession = route(fakeRequest("GET", "/discussions"));
                        assertTrue(status(discussionpagenosession) == OK);

                        //DISCUSSION PAGE WITH SESSION
                        Result discussionpage = route(fakeRequest("GET", "/discussions").withCookies(playSession));
                        assertTrue(status(discussionpage) == OK);

                        //TASKS PAGE WITHOUT SESSION
                        Result taskspagenosession = route(fakeRequest("GET", "/tasks"));
                        assertTrue(status(taskspagenosession) == OK);

                        //TASKS PAGE WITH SESSION
                        Result taskspage = route(fakeRequest("GET", "/tasks").withCookies(playSession));
                        assertTrue(status(taskspage) == OK);

                        //SUGGESTIONS PAGE WITHOUT SESSION
                        Result suggestionspagenosession = route(fakeRequest("GET", "/suggestions"));
                        assertTrue(status(suggestionspagenosession) == OK);

                        //SUGGESTIONS PAGE WITH SESSION
                        Result suggestionspage = route(fakeRequest("GET", "/suggestions").withCookies(playSession));
                        assertTrue(status(suggestionspage) == OK);
                    }
                }
        );
    }
}