package functional_tests;

import controllers.Application;
import models.*;
import org.junit.*;
import static org.junit.Assert.*;

import play.mvc.Result;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;

import static play.test.Helpers.*;
import static play.test.Helpers.start;
import controllers.routes;

/**
 * Created by arnaud on 4-12-14.
 */
public class SignupTest extends WithApplication {

    @Test
    public void test() {
        running(fakeApplication(), new Runnable() {
            public void run() {
                Result initialpage = route(fakeRequest("GET", "/signup"));
                assert(contentAsString(initialpage).contains("signup"));
                Map<String, String> signupform = new HashMap<String, String>();
                signupform.put("first_name", "Arnaud");
                signupform.put("last_name", "Hambenne");
                signupform.put("email", "arnaud@hambenne.com");
                signupform.put("password", "lalala");
                signupform.put("repeatPassword", "lalala");
//                Result submission = callAction(controllers.Signup.doSignup(), fakeRequest().withFormUrlEncodedBody(signupform));
//                assert(contentAsString(initialpage).contains("verify"));
            }
        });
    }


}
