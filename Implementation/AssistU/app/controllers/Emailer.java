package controllers;

import plugins.com.feth.play.module.mail.Mailer;
import plugins.com.feth.play.module.pa.PlayAuthenticate;
import models.Person;
import play.Logger;
import play.libs.mailer.Email;
import play.libs.mailer.MailerPlugin;
import play.mvc.Controller;
import play.mvc.Result;
import play.twirl.api.Html;

/**
 * attention: in case you are using intellij idea you might see lots of errors , don't pay attention it works fine
 */
public class Emailer extends Controller {
    /**
     * sends email to verify users
     * @param subject
     * @param user
     * @param body
     */
    public static void sendVerifyEmail(String subject, Person user,Html body){

        final Email email = new Email();
        email.setSubject(subject);
        email.setFrom("we.assitu@gmail");
        email.addTo(user.email);
        email.setBodyHtml(body.toString());
        MailerPlugin.send(email);

    }

    /**
     * this is done to generally notify users of any important activities
     * @param subject
     * @param user
     * @param body
     */
    public static void sendNotifyEmail(String subject,Person user,Html body){

        final Email email = new Email();
        email.setSubject(subject);
        email.setFrom("we.assitu@gmail");
        email.addTo(user.email);
        email.setBodyHtml(body.toString());
        MailerPlugin.send(email);

    }



    /**
     * TODO remove this method when all the other methods are implemenetd this is only for sending test email and has no added valuefor the app
     * @return
     */
    public static Result sendWelcomeMessage() {
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null) {
            final Email email = new Email();
            email.setSubject("Welcome to AssisTU");
            email.setFrom("we.assistu@gmail");
            email.addTo(user.email);
            email.setBodyText("Good day" + user.name);
            email.setBodyHtml(views.html.email.testemail.render(user).toString());
            MailerPlugin.send(email);
            Logger.info("an email has been sent to " + user.email);
            return ok("Email " + " sent!");
        } else {
            return badRequest("EMAIL NOT SENT - NO SESSION");
        }
    }

}