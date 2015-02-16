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

//    @Before
//    public void setUp(){
//        start(fakeApplication(inMemoryDatabase()));
//    }
//
//    @Test
//    public void createProjectTest(){
////        User user = User.create("testuser", "testuser@test.nl", "test");
////        Project.create("testfolder", "testname", user.id, "testdescription");
////        Project testproject = Project.find.where().eq("folder" , "testfolder").findUnique();
////        assertNotNull(testproject);
////        assertEquals("testfolder" , testproject.folder);
////        assertEquals("testname" , testproject.name);
////        assertEquals(true, testproject.users.contains(User.find.byId(user.id)));
//    }
//
//    @Test
//    public void editProjectTest(){
////        User user = User.create("testuser", "testuser@test.nl", "test");
////        Project.create("testfolder", "testname", user.id, "testdescription");
////        Project testproject = Project.find.where().eq("folder" , "testfolder").findUnique();
////        Project.edit(testproject.id, "edittestfolder", "edittestname");
////        testproject = Project.find.byId(testproject.id);
////        assertNotNull(testproject);
////        assertEquals("edittestfolder" , testproject.folder);
////        assertEquals("edittestname" , testproject.name);
////        assertEquals(true , testproject.users.contains(User.find.byId(user.id)));
//    }

//    @Test
//    public void archiveProjectTest() {
//        User user = User.create("testuser", "testuser@test.nl", "test");
//        Project.create("testfolder", "testname", user.id, "testdescription");
//        Project testproject = Project.find.where().eq("folder", "testfolder").findUnique();
//        Project.archive(testproject.id);
//        testproject = Project.find.byId(testproject.id);
//        assertNotNull(testproject);
//        assertEquals(false, testproject.active);
//    }
//
//    @Test
//    public void addMemberAsTest() {
//        User user1 = User.create("testuser1", "testuser1@test.nl", "test");
//        User user2 = User.create("testuser2", "testuser2@test.nl", "test");
//        Project.create("testfolder", "testname", user1.id, "testdescription");
//        Project testproject = Project.find.where().eq("folder", "testfolder").findUnique();
//        Project.addMemberAs(testproject.id, user2.id);
//        testproject = Project.find.byId(testproject.id);
//        assertNotNull(testproject);
//        assertEquals(true , testproject.users.contains(User.find.byId(user1.id)));
//        assertEquals(true , testproject.users.contains(User.find.byId(user2.id)));
//    }
//
//    @Test
//    public void removeMemberFromTest() {
//        User user1 = User.create("testuser1", "testuser1@test.nl", "test");
//        User user2 = User.create("testuser2", "testuser2@test.nl", "test");
//        Project.create("testfolder", "testname", user1.id, "testdescription");
//        Project testproject = Project.find.where().eq("folder", "testfolder").findUnique();
//        Project.addMemberAs(testproject.id, user2.id);
//        testproject = Project.find.byId(testproject.id);
//        assertNotNull(testproject);
//        assertEquals(true, testproject.users.contains(User.find.byId(user1.id)));
//        assertEquals(true, testproject.users.contains(User.find.byId(user2.id)));
//        Project.removeMemberFrom(testproject.id, user2.id);
//        testproject = Project.find.byId(testproject.id);
//        assertNotNull(testproject);
//        assertEquals(true, testproject.users.contains(User.find.byId(user1.id)));
//        assertEquals(false, testproject.users.contains(User.find.byId(user2.id)));
//    }
}
