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
    public String description;
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
    public Event(User owner,String title, Date start, Date end, Boolean allDay) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.user=owner;
        this.allDay = allDay;

    }


    public static List<Event> findInDateRange(Date start, Date end,User user) {


        return find.where().in("user",user).or(
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
    public static Event createArticleEvent(User owner,String title,Date startDate,int interval){

        Date start=startDate;
        DateTime sd=new DateTime(start);
        DateTime ed=sd.plusDays(interval);
        Event event = new Event(owner,title,sd.toDate(),ed.toDate(),true);
        event.endsSameDay=false;
        event.allDay=true;
        event.save();
        return event;

    }

    /**
     * increment date to number of  desired days
     * @param d
     * @return
     */
    public static Date movedate(Date d){
            DateTime getIt= new DateTime(d);
            DateTime res= getIt.plusDays(1);
            return res.toDate();


    }

    /**
     * Creates full planning for writing an article article owners of the project
     *
     */
    public static void defaultPlanningArticle(User user,Project p){
        Date startDate = p.dateCreated;
        Event event1=Event.createArticleEvent(user,"Getting Started", startDate, 0);
        event1.endsSameDay=true;
        event1.description="Make sure you are well prepared. Understand the publishing phase as part of scientific method.";
        event1.update();
        Event event2=Event.createArticleEvent(user,"Keypoints", Event.movedate(event1.end), 1);
        event2.description="Before you start writing, think about what do you have to say. Make a list of keypoints you want to make in your paper.";
        event2.update();
        Event event3=Event.createArticleEvent(user,"Publication Strategy", Event.movedate(event2.end), 2);
        event3.description="Define your audience and specify a target journal list.";
        event3.update();
        Event event4=Event.createArticleEvent(user,"Introduction", Event.movedate(event3.end), 7);
        event4.description="Explain the context of your work and your research question or hypothesis.";
        event4.update();
        Event event5=Event.createArticleEvent(user,"Materials & Methods", Event.movedate(event4.end), 4);
        event5.description="In this section you explain how you carried out your study.";
        event5.update();
        Event event6=Event.createArticleEvent(user,"Results & Discussion", Event.movedate(event5.end), 2);
        event6.description="Objectively present your key research Results and interpret them in the (separate) Discussion section.";
        event6.update();
        Event event7=Event.createArticleEvent(user,"Abstract, keywords & Title ", Event.movedate(event6.end), 1);
        event7.description="Summerize the main aspects of your entire article in one paragraph. Keywords can help others find your article. The title should be attractive, descriptive and specific. ";
        event7.update();
        Event event8=Event.createArticleEvent(user,"References and Acknowledgment",Event.movedate( event7.end), 0);
        event8.endsSameDay=true;
        event8.description="A reference list contains all resources you have cited in the article. Acknowledgements lists people and organisations that contributed in a significant way.";
        event8.update();
        Event event9=Event.createArticleEvent(user,"Layout & Styles", Event.movedate(event8.end), 0);
        event9.endsSameDay=true;
        event9.description="Make sure the layout and style follow the journal’s style format.";
        event9.update();
    }

}
