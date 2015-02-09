package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by arnaud on 8-2-15.
 */
@Entity
public class MendeleyDocument extends Model {

    @Id
    public String id;

}
