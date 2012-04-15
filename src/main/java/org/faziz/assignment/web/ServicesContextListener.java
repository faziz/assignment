package org.faziz.assignment.web;

import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.faziz.assignment.utils.ServiceLocator;

/**
 * Web application lifecycle listener.
 *
 * @author faisal
 */
public class ServicesContextListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(
            ServicesContextListener.class.getName());
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initialize Services.");
        ServiceLocator.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
