package functional_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import org.junit.*;
import static org.junit.Assert.*;

import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static play.test.Helpers.*;
import static play.test.Helpers.start;

/**
 * Created by arnaud on 4-12-14.
 */
public class CalendarTest extends WithApplication {

    @Test
    public void calendar_test() {
        running(fakeApplication(new play.GlobalSettings()), new Runnable() {
                    public void run() {
                        //SIGNUP USER
                        Map<String, String> signupform = new HashMap<String, String>();
                        signupform.put("first_name", "Arnaud");
                        signupform.put("last_name", "Hambenne");
                        signupform.put("email", "arnaud@hambenne.com");
                        signupform.put("password", "lalala");
                        signupform.put("repeatPassword", "lalala");
                        routeAndCall(fakeRequest(POST, "/signingup").withFormUrlEncodedBody(signupform));

                        //VERIFY USER
                        String token = TokenAction.find.where().eq("targetPerson", Person.find.where().eq("email", "arnaud@hambenne.com").findUnique()).findUnique().token;
                        routeAndCall(fakeRequest(GET, "/accounts/verify/" + token));

                        //LOGIN USER
                        Map<String, String> loginform = new HashMap<String, String>();
                        loginform.put("email", "arnaud@hambenne.com");
                        loginform.put("password", "lalala");
                        Result dologin = routeAndCall(fakeRequest(POST, "/logingin").withFormUrlEncodedBody(loginform));

                        //RECOVER COOKIE FROM LOGIN RESULT
                        final Http.Cookie playSession = play.test.Helpers.cookie("PLAY_SESSION", dologin);

                        //CREATE A PROJECT
                        Map<String, String> newprojectform = new HashMap<String, String>();
                        newprojectform.put("folder", "BEP");
                        newprojectform.put("name", "Bachelor Eind Project");
                        newprojectform.put("description", "Test Project");
                        newprojectform.put("template", "TU Delft - Dissertation");
                        routeAndCall(fakeRequest(POST, "/project/create").withFormUrlEncodedBody(newprojectform).withCookies(playSession));

                        //LOAD CALENDAR
                        Result loadcalendarnosession = routeAndCall(fakeRequest(GET, "/calendar"));
                        assertTrue(status(loadcalendarnosession) == OK);
                        Result loadcalendar = routeAndCall(fakeRequest(GET, "/calendar").withCookies(playSession));
                        assertTrue(status(loadcalendar) == OK);

                        //CREATE NEW EVENT
                        Map<String, String> event = new HashMap<String, String>();
                        event.put("title", "testevent");
                        event.put("description", "testdescription");
                        event.put("allDay", "" + false);
                        event.put("start", "01.01.2000 00:00");
                        event.put("end", "01.01-2000 04:00");
                        Result addeventfail = routeAndCall(fakeRequest(POST, "/event").withFormUrlEncodedBody(event).withCookies(playSession));
                        assertTrue(status(addeventfail) == BAD_REQUEST);
                        event.put("end", "01.01.2000 04:00");
                        Result addeventnosession = routeAndCall(fakeRequest(POST, "/event").withFormUrlEncodedBody(event));
                        assertTrue(status(addeventnosession) == OK);
                        Result addevent = routeAndCall(fakeRequest(POST, "/event").withFormUrlEncodedBody(event).withCookies(playSession));
                        assertTrue(status(addevent) == 303);

                        //LIST EVENTS FOR USER
                        Person user = Person.find.where().eq("email", "arnaud@hambenne.com").findUnique();
                        Result listevents = routeAndCall(fakeRequest(GET, "/" + user.id + "/events").withFormUrlEncodedBody(event).withCookies(playSession));
                        assertTrue(status(listevents) == OK);

                        //EDIT EVENT
                        Event e = Event.find.where().eq("title", "testevent").findUnique();
                        event.put("end", "01.01.2000 02:00");
                        Result editeventnosession = routeAndCall(fakeRequest(GET, "/event/" + e.id + "/edit").withFormUrlEncodedBody(event));
                        assertTrue(status(editeventnosession) == OK);
                        Result editevent = routeAndCall(fakeRequest(GET, "/event/" + e.id + "/edit").withFormUrlEncodedBody(event).withCookies(playSession));
                        assertTrue(status(editevent) == OK);

                        //UPDATE EVENT
                        event.put("end", "01.01-2000 02:00");
                        Result updateeventnosession = routeAndCall(fakeRequest(POST, "/event/" + e.id + "/update").withFormUrlEncodedBody(event));
                        assertTrue(status(updateeventnosession) == OK);
                        Result updateeventfail = routeAndCall(fakeRequest(POST, "/event/" + e.id + "/update").withFormUrlEncodedBody(event).withCookies(playSession));
                        assertTrue(status(updateeventfail) == BAD_REQUEST);
                        event.put("end", "01.01.2000 02:00");
                        Result updateevent = routeAndCall(fakeRequest(POST, "/event/" + e.id + "/update").withFormUrlEncodedBody(event).withCookies(playSession));
                        assertTrue(status(updateevent) == 303);

                        //DELETE EVENT
                        e = Event.find.byId(e.id);
                        Result deleteeventnosession = routeAndCall(fakeRequest(GET, "/event/" + e.id + "/delete").withFormUrlEncodedBody(event));
                        assertTrue(status(deleteeventnosession) == OK);
                        Result deleteeventfail = routeAndCall(fakeRequest(GET, "/event/" + e.id + "/delete").withFormUrlEncodedBody(event).withCookies(playSession));
                        assertTrue(status(deleteeventfail) == 303);
                    }
                }
        );
    }
}