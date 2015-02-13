package controllers;

import com.feth.play.module.pa.PlayAuthenticate;
import models.Event;
import models.Person;
import org.joda.time.DateTime;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.calendar;
import views.html.formEdit;
import views.html.formNew;
import views.html.list;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class CalData extends Controller {

    final static Form<Event> eventForm = Form.form(Event.class);

    /**
     * Checks if events ends the same day which starts
     * @param start Date
     * @param end Date
     * @return Boolean: True if ends same day
     */
    private static Boolean endsSameDay(Date start, Date end){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(start).equals(dateFormat.format(end));
    }


    /**
     * Returns list of events for calendar view
     * @param start Long Timestamp of current view start
     * @param end Long Timestamp of current view end
     * @return Result
     */
    public static Result json(Long start, Long end) {

        Date startDate = new Date(start*1000);
        Date endDate = new Date(end*1000);
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        List<Event> resultList = Event.findInDateRange(startDate, endDate, person);
        ArrayList<Map<Object, Serializable>> allEvents = new ArrayList<Map<Object, Serializable>>();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Event event : resultList) {
            Map<Object, Serializable> eventRemapped = new HashMap<Object, Serializable>();
            eventRemapped.put("id", event.id);
            eventRemapped.put("title", event.title);
            eventRemapped.put("description", event.description);
            eventRemapped.put("start", df.format(event.start));
            eventRemapped.put("end", df.format(event.end));
            eventRemapped.put("allDay", event.allDay);
            eventRemapped.put("url", controllers.routes.CalData.edit(event.id).toString());

            allEvents.add(eventRemapped);
        }
        return ok(play.libs.Json.toJson(allEvents));
    }


    /**
     * Displays full calendar
     * @return Result
     */
    public static Result calendar() {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));
        if(person != null)
            return ok(calendar.render("My Calendar", person,eventForm,Event.find.where().in("person", person).order().asc("start").findList()));
        else
        return ok(calendar.render("My Calendar", person, eventForm, Event.find.where().in("person", person).order().asc("start").findList()));
    }


    /**
     * List of events in table view
     * @return Result
     *
     */
    public static Result list(long uid) {
        Person person = Person.find.byId(uid);
        List<Event> events = Event.find.where().in("person", person).order().asc("start").findList();
        return ok(list.render("List of events", person,events));
    }


    /**
     * Displays blank form
     * @return Result
     */
    public static Result blank() {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));

        return ok(formNew.render("new event form", person,eventForm));
    }


    /**
     * Save new event in DB (a.k.a. submit action in other examples)
     * @return Result
     */
    public static Result add() {
        Form<Event> eventForm = Form.form(Event.class).bindFromRequest();
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));

        if (eventForm.hasErrors()) {
            return badRequest(formNew.render("new event form", person,eventForm));
        }

        Event newEvent = eventForm.get();

        newEvent.allDay = newEvent.allDay != null;
        if (newEvent.end == null) {
            newEvent.end = new DateTime(newEvent.start).plusMinutes(30).toDate();
        }
        newEvent.endsSameDay = endsSameDay(newEvent.start, newEvent.end);
        person.events.add(newEvent);
        Logger.debug("add new event function in CalData is used :" + newEvent.title);
        newEvent.save();

        return redirect(controllers.routes.CalData.list(person.id));
    }




    /**
     * Dislays form for editing existing event
     * @param id Long
     * @return Result
     */
    public static Result edit(Long id) {
        Event event = Event.find.byId(id);
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));

        Form<Event> eventForm = Form.form(Event.class).fill(event);
        return ok(formEdit.render("Edit events", person,id, eventForm, event));
    }


    /**
     * Save updated event in DB
     * @param id Long
     * @return Result
     */
    public static Result update(Long id) {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));

        Form<Event> eventForm = Form.form(Event.class).bindFromRequest();
        if (eventForm.hasErrors()) {
            return badRequest(formEdit.render("update", person,id, eventForm, Event.find.byId(id)));
        }
        Event updatedEvent = eventForm.get();
        updatedEvent.allDay = updatedEvent.allDay != null;
        if (updatedEvent.end == null) {
            updatedEvent.end = new DateTime(updatedEvent.start).plusMinutes(30).toDate();
        }
        updatedEvent.endsSameDay = endsSameDay(updatedEvent.start, updatedEvent.end);
        updatedEvent.update(id);

        return redirect(controllers.routes.CalData.list(person.id));
    }


    /**
     * Deletes event
     * @param id Long
     * @return Result
     */
    public static Result delete(Long id) {
        Person person = Person.findByAuthUserIdentity(PlayAuthenticate.getUser(session()));

        Event.find.ref(id).delete();
        return redirect(controllers.routes.CalData.list(person.id));
    }


    /**
     * Adds event after clicking on calendar
     * @return Result
     */
    public static Result addByAjax(long uid) {
        Form<Event> eventForm = Form.form(Event.class).bindFromRequest();
        Person person = Person.find.byId(uid);
        Event newEvent = eventForm.get();
        Event event = new Event(person,newEvent.title,newEvent.start,newEvent.end,newEvent.allDay);
        event.endsSameDay = endsSameDay(newEvent.start, newEvent.end);
        event.description=newEvent.description;
        event.save();

        if (eventForm.hasErrors()){
            return badRequest("There was some errors in form");
        }

        Map<String, String> result = new HashMap<String, String>();
        result.put("id", event.id.toString());
        result.put("url", controllers.routes.CalData.edit(event.id).toString());
        Logger.debug("add By ajax is used: " + result);
        return ok(play.libs.Json.toJson(result));
    }


    /**
     * Saves in DB date changed by event drag
     * @return Result
     */
    public static Result move() {

        Long id = Long.valueOf(Form.form().bindFromRequest().get("id"));
        int dayDelta = Integer.parseInt(Form.form().bindFromRequest().get("dayDelta"));
        int minuteDelta = Integer.parseInt(Form.form().bindFromRequest().get("minuteDelta"));

        Event event = Event.find.byId(id);
        event.start = new DateTime(event.start).plusDays(dayDelta).plusMinutes(minuteDelta).toDate();
        event.end = new DateTime(event.end).plusDays(dayDelta).plusMinutes(minuteDelta).toDate();
        event.allDay = Boolean.valueOf(Form.form().bindFromRequest().get("allDay"));
        event.endsSameDay = endsSameDay(event.start, event.end);
        event.update();


        return ok("changed");
    }

    /**
     * Saves in DB end date changed by event resize
     * @return Result
     */
    public static Result resize() {

        Long id = Long.valueOf(Form.form().bindFromRequest().get("id"));
        int dayDelta = Integer.parseInt(Form.form().bindFromRequest().get("dayDelta"));
        int minuteDelta = Integer.parseInt(Form.form().bindFromRequest().get("minuteDelta"));

        Event event = Event.find.byId(id);
        event.end = new DateTime(event.end).plusDays(dayDelta).plusMinutes(minuteDelta).toDate();
        event.endsSameDay = endsSameDay(event.start, event.end);
        event.update();


        return ok("changed");
    }



}