package controllers;

import models.Person;
import play.data.*;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import views.html.*;
import plugins.com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import plugins.com.feth.play.module.pa.controllers.Authenticate;
import plugins.providers.localUsernamePassword.*;

import plugins.com.feth.play.module.pa.PlayAuthenticate;
import plugins.com.feth.play.module.pa.user.AuthUser;

/**
 */
public class Authentication extends Controller {

    public static final String FLASH_MESSAGE_KEY = "message";
    public static final String FLASH_ERROR_KEY = "error";
    public static final String USER_ROLE = "user";

    public static Result doLogin() {
        Authenticate.noCache(response());
        final Form<LocalUsernamePasswordAuthProvider.NativeLogin> filledForm = LocalUsernamePasswordAuthProvider.LOGIN_FORM
                .bindFromRequest();
        if (filledForm.hasErrors()) {
            // User did not fill everything properly
//            return badRequest(home.render("Could not log you in", null, true, "danger",
//                    "The combination of your email and password did not match any account"));
            return ok(login.render(filledForm, true, "danger", "Your credentials did not match any user"));
        } else {
            // Everything was filled
            return UsernamePasswordAuthProvider.handleLogin(ctx());
        }
    }

    /**
     * inner class login
     */
//    public static class Login {
//        public  String email;
//        public  String password;
//        /**
//         * validate the form
//         */
//        public String validate(){
//            if(User.authenticate(email,password) == null){
//                return "invalid user name or password";
//            }
//            return null;
//        }
//    }

//    private static final Form<Login> loginform = Form.form(Login.class);

    /**
     * login page
     *
     * @return
     */
    public static Result login() {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null)
            OAuthLogout();
        return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, false, "", ""));
    }

    public static Result loginWithMessage(String message, String theme) {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null)
            OAuthLogout();
        return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, theme, message));
    }

    /**
     * signup page
     *
     * @return
     */
    public static Result signup() {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null)
            OAuthLogout();
        return ok(signup.render(LocalUsernamePasswordAuthProvider.SIGNUP_FORM, false, "", ""));
    }

    public static Result forgotPasswordPage(){
        return ok(forgotPassword.render(null, false, "", ""));
    }

    /**
     * authenticate the user
     */
//    public static Result authenticate(){
//        Form<Login> signinform = loginform.bindFromRequest();
//        if(signinform.hasErrors()){
//            return badRequest(login.render(loginform));
//        }else {
//            session().clear();
//            session("email" , signinform.get().email);
//            return redirect(routes.Application.index());
//        }
//    }

    public static Person getLocalUser(final Http.Session session) {
        final AuthUser currentAuthUser = PlayAuthenticate.getUser(session);
        final Person localPerson = Person.findByAuthUserIdentity(currentAuthUser);
        return localPerson;
    }

    /**
     * OAuth Authentication
     * @param provider: Name of the plugins.service, such as google, facebook, twitter, etc.
     * @return call to the plugin that passes the name of the plugins.service to user
     */
    public static Result OAuth(String provider){
        return Authenticate.authenticate(provider);
    }

    /**
     * OAuth Authentication failed/denied
     * The authentication failed and the user gets redirected to the login page
     */
    public static Result OAuthDenied(String provider){
        Authenticate.noCache(response());
        return badRequest(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, "danger", "Could not log you in"));
    }

    /**
     * OAuth Logout
     */
    public static Result OAuthLogout(){
        return Authenticate.logout();
    }

}
