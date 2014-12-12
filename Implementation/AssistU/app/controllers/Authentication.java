package controllers;

import models.User;
import play.data.*;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.login;

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
            return redirect(
                    routes.Application.index()
            );
        }
    }


}
