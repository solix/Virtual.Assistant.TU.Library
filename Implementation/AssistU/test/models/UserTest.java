package models;

import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class UserTest extends WithApplication {

    @Before
    public void setUp(){
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void createAndRetrieveUser() {
        new User("admin@AssisTU.nl", "solix", "jj").save();
        User solix = User.find.where().eq("email", "admin@AssisTU.nl").findUnique();
        assertNotNull(solix);
        assertEquals("solix", solix.name);
    }
}