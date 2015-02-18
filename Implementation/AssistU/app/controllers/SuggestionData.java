package controllers;

import models.Person;
import play.mvc.*;
import views.html.*;
import com.feth.play.module.pa.PlayAuthenticate;

/**
 * Created by arnaud on 8-1-15.
 */
public class SuggestionData extends Controller{

    public static Result suggestion(String subject) {
        Person user = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(user != null){
//            if(subject.equals("Orientation")){
//                return ok(suggestions_tagcloud.render("Suggestions", subject, "fa-eye", user));
//            } else if(subject.equals("Exploration")){
//                return ok(suggestions_tagcloud.render("Suggestions", subject, "fa-arrows-alt", user));
//            } else if(subject.equals("Processing")){
//                return ok(suggestions_tagcloud.render("Suggestions", subject, "fa-sitemap", user));
//            } else if(subject.equals("Literature Search")){
//                return ok(suggestions_tagcloud.render("Suggestions", subject, "fa-book", user));
//            } else if(subject.equals("Publish")){
//                return ok(suggestions_tagcloud.render("Suggestions", subject, "fa-newspaper-o", user));
//            } else if(subject.equals("Keeping Up-to-date")){
//                return ok(suggestions_tagcloud.render("Suggestions", subject, "fa-refresh", user));
//            } else {

                return Application.suggestions();
//            }

        }
        else
            return Authentication.login();
    }
}
