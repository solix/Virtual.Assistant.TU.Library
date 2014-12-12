import play.*;
import play.libs.*;
import models.*;
import java.util.*;
import com.avaje.ebean.*;

/**
 * this class  injects default data into the webapp  to load a YAML file at application load time
 */
public class Global extends GlobalSettings {
    public void onStart(Application app) {
        InitialData.insertUsers(app);

    }

    /**
     * loads the data from yaml file and add rows in User table
     */
    static class InitialData {
        public static void insertUsers(Application app) {
            if (Ebean.find(User.class).findRowCount() == 0) {
                Map<String, List<Object>> all =
                        (Map<String, List<Object>>) Yaml.
                                load("initial-data.yml");
                Ebean.save(all.get("users"));
            }
        }

    }
}
