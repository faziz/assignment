package org.faziz.assignment.utils;

import javax.persistence.EntityManagerFactory;
import org.faziz.assignment.service.SecurityService;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
public class ServiceLocatorTest {
    
    @Test
    public void testGetEntityManagerFactory() {
        EntityManagerFactory entityManagerFactory = ServiceLocator.getEntityManagerFactory();
        assertNotNull(entityManagerFactory);
    }
    
    @Test
    public void testGetSecurityService() {
        SecurityService securityService = ServiceLocator.getSecurityService();
        assertNotNull(securityService);
    }
}
