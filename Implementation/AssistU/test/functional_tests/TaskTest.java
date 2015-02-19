package functional_tests;

import models.Person;
import models.Task;
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
public class TaskTest extends WithApplication {

    @Test
    public void task_test() {
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

                        //CREATE TASK
                        Map<String, String> taskdata = new HashMap<String, String>();
                        taskdata.put("name", "test task");
                        taskdata.put("dueDate", "01-01");
                        Result createtasknosession = routeAndCall(fakeRequest(POST, "/task/new").withFormUrlEncodedBody(taskdata));
                        assertTrue(status(createtasknosession) == OK);
                        Result createtaskfail = routeAndCall(fakeRequest(POST, "/task/new").withFormUrlEncodedBody(taskdata).withCookies(playSession));
                        assertTrue(status(createtaskfail) == BAD_REQUEST);
                        taskdata.put("dueDate", "01-01-2015");
                        Result createtask = routeAndCall(fakeRequest(POST, "/task/new").withFormUrlEncodedBody(taskdata).withCookies(playSession));
                        assertTrue(status(createtask) == 303);

                        //DONE TASK
                        Task task = Task.find.where().eq("name", "test task").findUnique();
                        routeAndCall(fakeRequest(GET, "/task/done/" + task.id).withCookies(playSession));
                        task = Task.find.byId(task.id);
                        assertTrue(task.done);

                        //UNDO TASK
                        routeAndCall(fakeRequest(GET, "/task/undo/" + task.id).withCookies(playSession));
                        task = Task.find.byId(task.id);
                        assertTrue(!task.done);

                        //REMOVE TASK
                        routeAndCall(fakeRequest(GET, "/task/delete/" + task.id).withCookies(playSession));
                        task = Task.find.byId(task.id);
                        assertTrue(task == null);
                    }
                }
        );
    }
}