package models;

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;

public class PersonTest extends WithApplication {

    @Before
    public void setUp(){
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void createUserTest(){
        Person person = Person.create("testuser", "testuser@test.nl", "test");
        assertNotNull(person);
        assertEquals("testuser" , person.name);
        assertEquals("testuser@test.nl" , person.email);
    }
//    @Test
//    public void createUser() {
//        Application.("admin@AssisTU.nl","secret");
//        User solix = User.find.where().eq("email", "admin@AssisTU.nl").findUnique();
//        assertNotNull(solix);
//        assertEquals("solix", solix.email);
//    }
//
//    @Test
//    public void authenticateTest(){
//        new User("admin@AssisTU.nl","secret").save();
//
//        assertNotNull(User.authenticate("admin@AssisTU.nl","secret"));
//        assertNull(User.authenticate("admin@AssisTU.nl","badpassword"));
//        assertNull(User.authenticate("bademail@ls.io", "secret"));
//
//    }
}