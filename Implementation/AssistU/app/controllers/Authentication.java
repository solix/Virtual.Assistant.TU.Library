package controllers;

import models.User;
import play.data.*;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;

import com.feth.play.module.pa.controllers.Authenticate;

/**
 * Created by spyruo on 12-12-14.
 */
public class Authentication extends Controller {


    /**
     * inner class login
     */
    public static class Login {
        public  String email;
        public  String password;
        /**
         * validate the form
         */
        public String validate(){
            if(User.authenticate(email,password) == null){
                return "invalid user name or password";
            }
            return null;
        }
    }

    private static final Form<Login> loginform = Form.form(Login.class);

    /**
     * login page
     *
     * @return
     */
    public static Result login() {
        return ok(login.render(loginform));
    }

    /**
     * authenticate the user
     */
    public static Result authenticate(){
        Form<Login> signinform = loginform.bindFromRequest();
        if(signinform.hasErrors()){
            return badRequest(login.render(loginform));
        }else {
            session().clear();
            session("email" , signinform.get().email);
            return redirect(routes.Application.index());
        }
    }

    /**
     * OAuth Authentication
     * @param provider: Name of the service, such as google, facebook, twitter, etc.
     * @return call to the plugin that passes the name of the service to user
     */
    public static Result OAuth(String provider){
//        TODO: NetID does not have credentials yet, overriding to google
        return Authenticate.authenticate("google");
    }

    /**
     * OAuth Authentication failed/denied
     * The authentication failed and the user gets redirected to the login page
     */
    public static Result OAuthDenied(String provider){
        Authenticate.noCache(response());
        return redirect(routes.Authentication.login());
    }

    /**
     * OAuth Logout
     */
    public static Result OAuthLogout(){

        return Authenticate.logout();
    }

}
