package org.faziz.assignment.web;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.faziz.assignment.service.meta.Export;
import org.faziz.assignment.service.meta.HttpMetod;

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
        Table<HttpMetod, String, ServiceMetaData> table = processServiceClasses(sce);
        logger.log(Level.INFO, "Request map: ", table.size());
        
        sce.getServletContext().setAttribute(ApplicationConstants.REQUEST_MAP, table);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    private final Table<HttpMetod, String, ServiceMetaData> processServiceClasses(
        ServletContextEvent sce) {
        Table<HttpMetod, String, ServiceMetaData> table = HashBasedTable.create();
        
        try {
            Class[] classes = getClasses("org.faziz.assignment.service", sce);
            for (Class clazz : classes) {
                logger.log(Level.INFO, "class name: {0}", 
                    clazz.getCanonicalName());
                processServiceClass(clazz, table);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception occurred.", ex);
            throw new IllegalStateException("Exception occurred.", ex);
        }
        
        return table;
    }

    /**
     * Returns Array of classes from the given package name.
     * @param packageName package name used to load the classes from.
     * @param sce servlet context event used for loading classes from.
     * @return array of classes.
     * @throws ClassNotFoundException
     * @throws IOException 
     */
    private final Class[] getClasses(String packageName, ServletContextEvent sce)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = sce.getServletContext().getClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and
     * subdirectories.
     *
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base
     * directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private final List<Class> findClasses(File directory, String packageName)
            throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.'
                        + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    /**
     * Scans the class for <code>Export</code> annotation.
     * @param clazz given class.
     * @param table table used to store information into.
     */
    private final void processServiceClass(Class clazz, Table<HttpMetod, String, ServiceMetaData> table) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Export.class)) {
                processMethod(method, table);
            }
        }
    }

    /**
     * Processes the given method. Extracts all the information and store it in the table.
     * @param method
     * @param table 
     */
    private final void processMethod(Method method, Table<HttpMetod, String, ServiceMetaData> table) {
        logger.log(Level.INFO, "method name: {0} is annotated: {1}",
                new Object[]{method, method.isAnnotationPresent(Export.class)});
        ServiceMetaData metaData = new ServiceMetaData();
        Export meta = method.getAnnotation(Export.class);
        
        logger.log(Level.INFO, "name():{0}", meta.name());
        metaData.setAuthenticate(meta.authenticate());
        metaData.setHttpMethod(meta.method());
        metaData.setName(meta.name());
        metaData.setMethod(method);
        
        table.put(meta.method(), meta.name(), metaData);
    }
}
