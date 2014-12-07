package models;

import controllers.Application;
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
        Project.create("Thesis", "first thesis", "boring");
        Project T = Project.find.where().eq("tabname" , "Thesis").findUnique();
        assertNotNull(T);
        assertEquals("Thesis" , T.tabname);
    }

    @Test
    public void createAndDeleteProject(){
        Project.create("Thesis", "first thesis", "boring");
        Project T = Project.find.where().eq("tabname" , "Thesis").findUnique();
        assertNotNull(T);
        assertEquals("Thesis" , T.tabname);
        assertEquals("first thesis" , T.name);
        assertEquals("boring" , T.description);
        Application.deleteProject(T.id);
        Project Td = Project.find.where().eq("tabname" , "Thesis").findUnique();
        assertEquals(Td, null);
    }
}
