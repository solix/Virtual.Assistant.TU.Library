package models;

/**
 * Created by spyruo on 5-12-14.
 */

import org.junit.*;
import static org.junit.Assert.*;
import play.test.WithApplication;
import static play.test.Helpers.*;
import static play.test.Helpers.start;

public class DocumentFileTest extends WithApplication{

    @Before
    public void setUp(){
        start(fakeApplication(inMemoryDatabase()));
    }

    @Test
    public void createDocumentTest(){
    assertTrue(false);
    }

}
