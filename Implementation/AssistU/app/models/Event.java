package models;

import play.data.format.Formats;
import play.data.validation.Constraints;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.*;

/**
 * Calendar model will be used to hold variables regarding planning events
 */
@Entity
public class Event extends Model {

    @Id
    public long id;
    @Constraints.Required
    public String title;
    @Constraints.Required
    @Formats.DateTime(pattern="dd-MM-yyyy")
    public Date startDate;

    @Constraints.Required
    @Formats.DateTime(pattern="dd-MM-yyyy")
    public Date endDate;

    @OneToOne(mappedBy = "event")
    public User user;

    public static Model.Finder<Long,Event> find = new Model.Finder(
            Long.class, Event.class
    );

}
