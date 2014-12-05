package models;

import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;
import static play.test.Helpers.start;

/**
 * Created by arnaud on 4-12-14.
 */
public class ProjectTest extends WithApplication {

    @Before
    public void setUp(){
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void createProject(){
//        new Project("Thesis").save();
        Project.create("Thesis");
        Project T = Project.find.where().eq("folder" , "Thesis").findUnique();
        assertNotNull(T);
        assertEquals("Thesis" , T.folder);
    }

    @Test
    public void createAndDeleteProject(){
        Project.create("Thesis");
        Project T = Project.find.where().eq("folder" , "Thesis").findUnique();
        assertNotNull(T);
        assertEquals("Thesis" , T.folder);
        Project.find.ref(T.id).delete();
        Project Td = Project.find.where().eq("folder" , "Thesis").findUnique();
        assertEquals(Td, null);
    }
}
