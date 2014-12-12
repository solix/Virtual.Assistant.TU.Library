package models;

import controllers.Application;
import models.*;
import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;

import java.util.HashMap;
import java.util.Map;

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
    public void createAndArchiveProject(){
        Project.create("Thesis", "first thesis", "ah@gmail.com", "nanana");
        Project T = Project.find.where().eq("folder" , "Thesis").findUnique();
        assertNotNull(T);
        assertEquals("Thesis" , T.folder);
        assertEquals("first thesis" , T.name);
        assertEquals(true , T.users.contains(User.find.byId("ah@gmail.com")));
        Application.archiveProject("ah@gmail.com", T.id);
        Project Td = Project.find.where().eq("folder" , "Thesis").findUnique();
        assertEquals(Td.active, false);
    }

    @Test
    public void createAndEditProject(){
        Project.create("Thesis", "first thesis", "ah@gmail.com", "hahaha");
        Project T = Project.find.where().eq("folder" , "Thesis").findUnique();
        assertNotNull(T);
        assertEquals("Thesis" , T.folder);
        Project.edit(T.id, "Report", "first thesis");
        Project Tu = Project.find.where().eq("name" , "first thesis").findUnique();
        assertEquals(Tu.folder, "Report");
        assertEquals("Report", Project.find.byId(T.id).folder);
        assertTrue(Project.find.where().eq("folder", "Report").findUnique().users.contains(User.find.byId("ah@gmail.com")));
    }

    /**
     * User Arnaud (arnaud@assistu.nl) creates a new project called "Thesis", invites Soheil (soheil@assistu.nl), and then leaves it himself
     * Now Soheil is the only member of the project
     */
    @Test
    public void projectScene1(){
        Project thesis = Project.create("Thesis", "Final Thesis", "ah@gmail.com", "booboo");
        assertEquals(thesis.name, "Final Thesis");
        Project.addMemberAs(thesis.id, "soli@gmail.com");
        Application.removeMemberFromProject(thesis.id, "ah@gmail.com");
        //TODO: You need to reinstantiate the same project again, if you don't the test will fail, need to find out whether this is a bug or expected behaviour
        Project updated = Project.find.byId(thesis.id);
        assertFalse(updated.users.contains(User.find.byId("ah@gmail.com")));
        assertTrue(updated.users.contains(User.find.byId("soli@gmail.com")));
        assertTrue(updated.users.size() == 1);
    }

    /**
     * find involving in the projects
     */
//    @Test
//    public void findProjectsinvolvingTest(){
//        new User("tom@gmail.com" , "apple").save();
//        new User("kim@yahoo.com" , "candy").save();

       // Project.create("science article" , "")


//    }


}
