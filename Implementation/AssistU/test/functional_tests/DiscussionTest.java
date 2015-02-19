package functional_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Person;
import models.Project;
import models.Task;
import models.TokenAction;
import org.junit.*;
import static org.junit.Assert.*;

import play.Logger;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static play.test.Helpers.*;
import static play.test.Helpers.start;

/**
 * Created by arnaud on 4-12-14.
 */
public class DiscussionTest extends WithApplication {

    @Test
    public void discussion_test() {
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

                        //LOAD SPECIFIC DISCUSSION
                        Project project = Project.find.where().eq("folder", "BEP").findUnique();
                        Result loaddiscussionnosession = routeAndCall(fakeRequest(GET, "/discussion/" + project.id));
                        assertTrue(status(loaddiscussionnosession) == OK);
                        Result loaddiscussion = routeAndCall(fakeRequest(GET, "/discussion/" + project.id).withCookies(playSession));
                        assertTrue(status(loaddiscussion) == OK);

                        //CREATE DISCUSSION
                        ObjectNode discussiondata = new ObjectMapper().createObjectNode();
                        discussiondata.put("subject", "testsubject");
                        discussiondata.put("content", "testcontent");
                        discussiondata.put("date", "" + (new Date()).toGMTString());
                        discussiondata.put("projectID", "" + project.id);
                        discussiondata.put("isChild", "" + false);
                        Result postdiscussion = routeAndCall(fakeRequest(POST, "/chat").withJsonBody(discussiondata).withCookies(playSession));
                        assertTrue(status(postdiscussion) == OK);

                        //CREATE DISCUSSION FROM FILE
                        Map<String,String> extdiscussiondata = new HashMap<String, String>();
                        extdiscussiondata.put("subject", "testsubject");
                        extdiscussiondata.put("content", "testcontent");
                        extdiscussiondata.put("attachment", "testattachment");
                        extdiscussiondata.put("projectID", "" + project.id);
                        extdiscussiondata.put("isChild", "" + false);
//                        Result postextdiscussion = routeAndCall(fakeRequest(GET, "/postexternal").withFormUrlEncodedBody(extdiscussiondata).withCookies(playSession));
//                        assertTrue(status(postextdiscussion) == OK);
                    }
                }
        );
    }
}