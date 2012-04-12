package org.faziz.assignment.utils;

import com.google.common.collect.Table;
import javax.servlet.http.HttpServletRequest;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author faisal
 */
public class ApplicationUtilsTest {
    
    public ApplicationUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getPostData method, of class ApplicationUtils.
     */
    @Test
    public void testGetPostData() {
//        System.out.println("getPostData");
//        HttpServletRequest req = null;
//        String expResult = "";
//        String result = ApplicationUtils.getPostData(req);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of processServiceClasses method, of class ApplicationUtils.
     */
    @Test
    public void testProcessServiceClasses() {
        System.out.println("processServiceClasses");
        ClassLoader classLoader = getClass().getClassLoader();

        Table result = ApplicationUtils.processServiceClasses(classLoader);
        assertNotNull(result);
        assertTrue(result.size() > 0);
    }
}
