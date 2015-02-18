package models;

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;

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
//        Person person = Person.create("testuser", "testuser@test.nl", "test");
//        Project.create("testfolder", "testname", person.id, "testdescription");
//        Project testproject = Project.find.where().eq("folder" , "testfolder").findUnique();
//        assertNotNull(testproject);
//        assertEquals("testfolder" , testproject.folder);
//        assertEquals("testname" , testproject.name);
//        assertEquals(true, testproject.users.contains(Person.find.byId(person.id)));
//    }
//
//    @Test
//    public void editProjectTest(){
//        Person person = Person.create("testuser", "testuser@test.nl", "test");
//        Project.create("testfolder", "testname", person.id, "testdescription");
//        Project testproject = Project.find.where().eq("folder" , "testfolder").findUnique();
//        Project.edit(testproject.id, "edittestfolder", "edittestname");
//        testproject = Project.find.byId(testproject.id);
//        assertNotNull(testproject);
//        assertEquals("edittestfolder" , testproject.folder);
//        assertEquals("edittestname" , testproject.name);
//        assertEquals(true , testproject.users.contains(Person.find.byId(person.id)));
//    }
//
//    @Test
//    public void archiveProjectTest() {
//        Person person = Person.create("testuser", "testuser@test.nl", "test");
//        Project.create("testfolder", "testname", person.id, "testdescription");
//        Project testproject = Project.find.where().eq("folder", "testfolder").findUnique();
//        Project.archive(testproject.id);
//        testproject = Project.find.byId(testproject.id);
//        assertNotNull(testproject);
//        assertEquals(false, testproject.active);
//    }
//
//    @Test
//    public void addMemberAsTest() {
//        Person person1 = Person.create("testuser1", "testuser1@test.nl", "test");
//        Person person2 = Person.create("testuser2", "testuser2@test.nl", "test");
//        Project.create("testfolder", "testname", person1.id, "testdescription");
//        Project testproject = Project.find.where().eq("folder", "testfolder").findUnique();
//        Project.addMemberAs(testproject.id, person2.id);
//        testproject = Project.find.byId(testproject.id);
//        assertNotNull(testproject);
//        assertEquals(true , testproject.users.contains(Person.find.byId(person1.id)));
//        assertEquals(true , testproject.users.contains(Person.find.byId(person2.id)));
//    }
//
//    @Test
//    public void removeMemberFromTest() {
//        Person person1 = Person.create("testuser1", "testuser1@test.nl", "test");
//        Person person2 = Person.create("testuser2", "testuser2@test.nl", "test");
//        Project.create("testfolder", "testname", person1.id, "testdescription");
//        Project testproject = Project.find.where().eq("folder", "testfolder").findUnique();
//        Project.addMemberAs(testproject.id, person2.id);
//        testproject = Project.find.byId(testproject.id);
//        assertNotNull(testproject);
//        assertEquals(true, testproject.users.contains(Person.find.byId(person1.id)));
//        assertEquals(true, testproject.users.contains(Person.find.byId(person2.id)));
//        Project.removeMemberFrom(testproject.id, person2.id);
//        testproject = Project.find.byId(testproject.id);
//        assertNotNull(testproject);
//        assertEquals(true, testproject.users.contains(Person.find.byId(person1.id)));
//        assertEquals(false, testproject.users.contains(Person.find.byId(person2.id)));
//    }
}
