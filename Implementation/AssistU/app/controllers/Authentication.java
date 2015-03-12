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

    /**
     * This function catches the login form, checks it and then passes it for handling.
     * @return Result
     */
    public static Result doLogin() {
        Authenticate.noCache(response());
        final Form<LocalUsernamePasswordAuthProvider.NativeLogin> filledForm = LocalUsernamePasswordAuthProvider.LOGIN_FORM
                .bindFromRequest();
        if (filledForm.hasErrors()) {
            return ok(login.render(filledForm, true, "danger", "Your credentials did not match any user"));
        } else {
            // Everything was filled
            return UsernamePasswordAuthProvider.handleLogin(ctx());
        }
    }

    /**
     * This function renders a blank login page
     * @return Result
     */
    public static Result login() {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null)
            OAuthLogout();
        return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, false, "", ""));
    }

    /**
     * This function also renders the login, but with a warning. This function gets called from
     * the authentication library when it could authenticate a user.
     * @return Result
     */
    public static Result loginWithMessage(String message, String theme) {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null)
            OAuthLogout();
        return ok(login.render(LocalUsernamePasswordAuthProvider.LOGIN_FORM, true, theme, message));
    }

    /**
     * This function renders the signup page
     * @return Result
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
     * The function retrieves the Person model from the session
     * @param session: the session of the browser
     * @return Person
     */
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
        if(provider.equals("mendeley")){
            Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
            if(user != null){
                return Authenticate.authenticate(provider);
            } else {
                return redirect(routes.Authentication.loginWithMessage("Your session has expired, please log in again", "danger"));
            }
        } else {
            return Authenticate.authenticate(provider);
        }
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
