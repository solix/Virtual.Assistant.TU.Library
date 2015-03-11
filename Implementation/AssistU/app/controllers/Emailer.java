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
 *
 * This class has methods to send email ato the users in different occasions using play-mailer plugin
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
     * this is done to generally notify users of any important activities
     * @param subject
     * @param user
     * @param body
     */
    public static void sendInvitationEmail(String subject, String emailaddress,Html body){

        final Email email = new Email();
        email.setSubject(subject);
        email.setFrom("we.assitu@gmail");
        email.addTo(emailaddress);
        email.setBodyHtml(body.toString());
        MailerPlugin.send(email);

    }



}