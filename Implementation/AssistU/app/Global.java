import play.*;
import play.libs.*;
import com.avaje.ebean.Ebean;
import models.*;
import java.util.*;

/**
 * this class  injects default data into the webapp  to load a YAML file at application load time
 */
public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app){
        if(User.find.findRowCount() == 0){
            Ebean.save((List) Yaml.load("user-data.yml"));
        }
    }
}
