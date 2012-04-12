package org.faziz.assignment.web;

import com.google.common.collect.Table;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.faziz.assignment.service.meta.HttpMetod;
import org.faziz.assignment.utils.ApplicationConstants;
import org.faziz.assignment.utils.ApplicationUtils;

/**
 * Web application lifecycle listener. Loads the REST request URI map on startup.
 *
 * @author faisal
 */
@WebListener()
public class LoadMetaDataListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(
            LoadMetaDataListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Table<HttpMetod, String, ServiceMetaData> table = ApplicationUtils.processServiceClasses(
            sce.getServletContext().getClassLoader());
        logger.log(Level.INFO, "Request map: ", table.size());
        
        sce.getServletContext().setAttribute(ApplicationConstants.REQUEST_MAP, table);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
