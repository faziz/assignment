package org.faziz.assignment.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.faziz.assignment.utils.ApplicationConstants;
import org.faziz.assignment.utils.ApplicationUtils;

/**
 * Web application lifecycle listener. Loads the REST request URI map on startup.
 *
 * @author faisal
 */
public class LoadMetaDataListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(
            LoadMetaDataListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.log(Level.INFO, "Generating URL MAP.");
        
        sce.getServletContext().setAttribute(ApplicationConstants.REQUEST_MAP, 
            ApplicationUtils.processServiceClasses(
                this.getClass().getClassLoader()));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
