package controllers;

import models.*;
import play.data.Form;
import play.mvc.*;
import views.html.*;

/**
 * Created by arnaud on 16-12-14.
 */
public class UserData  extends Controller {

    static Form<User> userForm = Form.form(User.class);

    /**
     * The creation of a user through the registration form
     * @return
     */
    public static Result createUser() {
        return redirect(routes.Authentication.login());
    }

    /**
     * The creation of a user when he logs in through a social
     * service.
     * @return
     */
    public static Result createSocialUser() {
        return redirect(routes.Application.index());
    }

}
