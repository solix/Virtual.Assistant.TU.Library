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
public class SignupLoginTest extends WithApplication {

    @Test
    public void signup_login_test() {
        running(fakeApplication(new play.GlobalSettings()), new Runnable() {
            public void run() {
                Result loginpage = route(fakeRequest("GET", "/login"));
                assertTrue(status(loginpage) == OK);

                Result signuppage = route(fakeRequest("GET", "/signup"));
                assertTrue(status(signuppage) == OK);

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
            }
        });
    }
}
