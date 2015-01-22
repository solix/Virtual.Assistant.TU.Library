package controllers;

import models.Event;
import play.data.Form;
import play.mvc.*;

/**
 * This class will take care of the Calendar event submission,edition,deletetion,etc
 */
public class CalData extends Controller {

    private static Form<Event> calForm = Form.form(Event.class);

    public static Result addEvent(){
        Form<Event> bindcal=calForm.bindFromRequest();

        Event event = bindcal.get();

        event.save();
        flash("Event"+ event.title+"is added"  );
        return redirect(controllers.routes.Application.calendar());

    }


}
