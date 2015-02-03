package models;


import com.avaje.ebean.Expr;
import org.joda.time.DateTime;
import play.Logger;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
public class Event extends Model {

    @Id
    public Long id;


    @Constraints.Required
    public String title;
    public Boolean allDay;

    @Constraints.Required
    @Formats.DateTime(pattern = "dd.MM.yyyy HH:mm")
    public Date start = new Date();

    @Formats.DateTime(pattern = "dd.MM.yyyy HH:mm")
    public Date end = new Date();

    public Boolean endsSameDay;

    @ManyToOne
    User user ;


    public static Finder<Long,Event> find = new Finder<Long,Event>(Long.class, Event.class);

    /**
     * Cosntructor to create an event
     * @param title
     * @param start
     * @param end
     * @param allDay
     */
    public Event(String title, Date start, Date end, Boolean allDay) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.allDay = allDay;

    }


    public static List<Event> findInDateRange(Date start, Date end) {


        return find.where().or(
                Expr.and(
                        Expr.lt("start", start),
                        Expr.gt("end", end)
                ),
                Expr.or(
                        Expr.and(
                                Expr.gt("start", start),
                                Expr.lt("start", end)
                        ),
                        Expr.and(
                                Expr.gt("end", start),
                                Expr.lt("end", end)
                        )
                )
        ).findList();
    }





    /**
     * Creates an event with the time interval for default planning of writing an article/dissertion
     * @param title
     * @param startDate
     * @param interval
     * @return
     */
    public static Event createArticleEvent(String title,Date startDate,int interval){

        Date start=startDate;
        DateTime sd=new DateTime(start);
        DateTime ed=sd.plusDays(interval);
        Event event = new Event(title,sd.toDate(),ed.toDate(),true);
        event.endsSameDay=false;
        event.allDay=true;
        event.save();
        return event;

    }

    /**
     * increment date to number of  desired days
     * @param d
     * @param i
     * @return
     */
    public static Date movedate(Date d){
            DateTime getIt= new DateTime(d);
            DateTime res= getIt.plusDays(1);
            return res.toDate();


    }

}
