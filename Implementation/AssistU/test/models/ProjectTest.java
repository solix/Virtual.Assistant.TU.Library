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
        Project.create("Thesis", "first thesis", "arnaud@assistu.nl");
        Project T = Project.find.where().eq("folder" , "Thesis").findUnique();
        assertNotNull(T);
        assertEquals("Thesis" , T.folder);
        assertEquals("first thesis" , T.name);
        assertEquals(true , T.userlist.contains(User.find.byId("arnaud@assistu.nl")));
        Application.archiveProject("arnaud@assistu.nl", T.id);
        Project Td = Project.find.where().eq("folder" , "Thesis").findUnique();
        assertEquals(Td.active, false);
    }

    @Test
    public void createAndUpdateProject(){
        Project.create("Thesis", "first thesis", "arnaud@assistu.nl");
        Project T = Project.find.where().eq("folder" , "Thesis").findUnique();
        assertNotNull(T);
        assertEquals("Thesis" , T.folder);
        Project.edit(T.id, "Report", "first thesis");
        Project Tu = Project.find.where().eq("name" , "first thesis").findUnique();
        assertEquals(Tu.folder, "Report");
        assertEquals("Report", Project.find.byId(T.id).folder);
        assertTrue(Project.find.where().eq("folder", "Report").findUnique().userlist.contains(User.find.byId("arnaud@assistu.nl")));
    }

    /**
     * User Arnaud (arnaud@assistu.nl) creates a new project called "Thesis", invites Soheil (soheil@assistu.nl), and then leaves it himself
     * Now Soheil is the only member of the project
     */
    @Test
    public void projectScene1(){
        Project thesis = Project.create("Thesis", "Final Thesis", "arnaud@assistu.nl");
        assertEquals(thesis.name, "Final Thesis");
        Application.addMemberToProjectAs("soheil@assistu.nl", thesis.id);
        assertTrue(thesis.userlist.contains(User.find.byId("soheil@assistu.nl")));
        Application.leaveProject("arnaud@assistu.nl", thesis.id);
        assertFalse(thesis.userlist.contains(User.find.byId("arnaud@assistu.nl")));
        assertTrue(thesis.userlist.contains(User.find.byId("soheil@assistu.nl")));
        assertTrue(thesis.userlist.size() == 1);
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
