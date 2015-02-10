package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import models.User;
import org.apache.commons.mail.EmailAttachment;
import play.Logger;
import play.Play;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
import play.mvc.Controller;
import play.mvc.Result;

import java.io.File;

/**
 * attention: in case you are using intellij idea you might see lots of errors , don't pay attention it works fine
 */
public class Emailer extends Controller {

    public static Result sendWelcomeMessage() {
        User user = User.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));

        final Email email = new Email();
        email.setSubject("Welcome to AssisTU");
        email.setFrom("soheil.jahanshahi@gmail");
        email.addTo(user.email);
        email.setBodyText("Good day"+ user.name);
        email.setBodyHtml(views.html.email.testemail.render(user).toString());
        MailerPlugin.send(email);
        Logger.info("an email has been sent to "+ user.email);
        return ok("Email " + " sent!");
    }

}