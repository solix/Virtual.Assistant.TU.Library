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

                        //CREATE SUBDISCUSSION
                        ObjectNode subdiscussiondata = new ObjectMapper().createObjectNode();
                        subdiscussiondata.put("subject", "testsubject");
                        subdiscussiondata.put("content", "testsubcontent");
                        subdiscussiondata.put("date", "" + (new Date()).toGMTString());
                        subdiscussiondata.put("projectID", "" + project.id);
                        subdiscussiondata.put("isChild", "" + true);
                        Result postsubdiscussion = routeAndCall(fakeRequest(POST, "/chat").withJsonBody(subdiscussiondata).withCookies(playSession));
                        assertTrue(status(postsubdiscussion) == OK);

                        //CREATE DISCUSSION FROM FILE
                        Person user = Person.find.where().eq("email", "arnaud@hambenne.com").findUnique();
                        File file = new File("public/images/favicon.png");
                        DocumentFile docFile = DocumentFile.create("testdocument", file, "public/images/favicon.png", project.id, user.id);
                        assertTrue(docFile != null);

                        Map<String,String> extdiscussiondata = new HashMap<String, String>();
                        extdiscussiondata.put("subject", "testsubject");
                        extdiscussiondata.put("content", "");
                        extdiscussiondata.put("attachment", "" + docFile.id);
                        extdiscussiondata.put("projectID", "" + project.id);
                        extdiscussiondata.put("isChild", "" + false);

                        //LOAD FILE DISCUSSION PAGE
                        Result loadfilediscussionnosession = routeAndCall(fakeRequest(GET, "/project/discuss/" + docFile.id));
                        assertTrue(status(loadfilediscussionnosession) == OK);
                        Result loadfilediscussion = routeAndCall(fakeRequest(GET, "/project/discuss/" + docFile.id).withCookies(playSession));
                        assertTrue(status(loadfilediscussion) == OK);

                        //POST NEW FILE DISCUSSION
                        Result postextdiscussionfail = routeAndCall(fakeRequest(GET, "/postexternal").withFormUrlEncodedBody(extdiscussiondata).withCookies(playSession));
                        assertTrue(status(postextdiscussionfail) == BAD_REQUEST);
                        extdiscussiondata.put("content", "testcontent");
                        Result postextdiscussion = routeAndCall(fakeRequest(GET, "/postexternal").withFormUrlEncodedBody(extdiscussiondata).withCookies(playSession));
                        assertTrue(status(postextdiscussion) == OK);

                        //GET ALL COMMENTS AS JSON
                        Result commentsAsJson = routeAndCall(fakeRequest(GET, "/comments").withCookies(playSession));
                        assertTrue(status(commentsAsJson) == OK);
                        assertTrue(contentAsString(commentsAsJson).contains("testcontent"));

                        //GET ALL SUBCOMMENTS AS JSON
                        Result subcommentsAsJson = routeAndCall(fakeRequest(GET, "/subcomments").withCookies(playSession));
                        assertTrue(status(subcommentsAsJson) == OK);
                        assertTrue(contentAsString(subcommentsAsJson).contains("testsubcontent"));

                        //DELETE DISCUSSION
                        Comment comment = Comment.find.where().eq("subject", "testsubject").eq("isChild", false).findUnique();
                        ObjectNode deletediscussiondata = new ObjectMapper().createObjectNode();
                        subdiscussiondata.put("cid", "" + comment.cid);
                        routeAndCall(fakeRequest(POST, "/deletemessage" + docFile.id).withJsonBody(deletediscussiondata));
                        assertTrue(Comment.find.byId(comment.cid) != null);
                        routeAndCall(fakeRequest(POST, "/deletemessage" + docFile.id).withJsonBody(deletediscussiondata).withCookies(playSession));
                    }
                }
        );
    }
}