package org.faziz.assignment.utils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.faziz.assignment.service.meta.Export;
import org.faziz.assignment.service.meta.HttpMetod;
import org.faziz.assignment.web.ServiceMetaData;

/**
 * Provides commonly used functionality.
 * @author faziz
 */
public final class ApplicationUtils {

    private static final Logger logger = Logger.getLogger(ApplicationUtils.class.getName());
    
    public static void writeContent(final Object data, final Writer writer, final String contentType) 
            throws IOException, JAXBException{
        contentType.contains("application/json");
        if( contentType.contains("application/json")){
            writeJSON(data, writer);
        }else{
            writeXML(data, writer);
        }
    }
    
    /**
     * Writes JSON data to the servlet stream.
     * @param data
     * @param writer
     * @throws IOException 
     */
    private static void writeJSON(final Object data, final Writer writer) 
        throws IOException {
        
        ObjectMapper mapper = new ObjectMapper();
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        // make deserializer use JAXB annotations (only)
        mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
        // make serializer use JAXB annotations (only)
        mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
        
        StringWriter stringWriter = new StringWriter();
        mapper.writeValue( stringWriter, data);
        IOUtils.write( stringWriter.toString(), writer);
    }

    /**
     * Writes XML data to the servlet stream.
     * @param data
     * @param writer
     * @throws JAXBException 
     */
    private static void writeXML(final Object data, final Writer writer) 
        throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(data.getClass());
        Marshaller marshaller = context.createMarshaller();
        
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(data, stringWriter);
        IOUtils.write( stringWriter.toString(), writer);
    }
    
    /**
     * Reads the request body and extracts JSON posted data.
     * 
     * @param req
     * @return 
     */
    public static String getPostData(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = req.getReader();
            reader.mark(10000);

            String line;
            do {
                line = reader.readLine();
                sb.append(line).append("\n");
            } while (line != null);
            // do NOT close the reader here, or you won't be able to get the post data twice
            reader.reset();
        } catch (IOException e) {
            // This has happened if the request's reader is closed    
            logger.log(Level.WARNING, "getPostData couldn't.. get the post data", e);
            
        }

        return sb.toString();
    }
    
    /**
     * Prepares meta based on provided classloader.
     * 
     * @param classLoader
     * @return 
     */
    public static URLMapper processServiceClasses(ClassLoader classLoader) {
        return URLMapper.getInstance(processServiceClasses1(classLoader));
    }
    
    /**
     * Prepares meta based on provided classloader.
     * 
     * @param classLoader
     * @return 
     */
    private static Table<HttpMetod, String, ServiceMetaData> processServiceClasses1(
        ClassLoader classLoader) {
        Table<HttpMetod, String, ServiceMetaData> table = HashBasedTable.create();
        
        try {
            Class[] classes = getClasses("org.faziz.assignment.service", classLoader);
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
     * @param classLoader servlet context event used for loading classes from.
     * @return array of classes.
     * @throws ClassNotFoundException
     * @throws IOException 
     */
    private static Class[] getClasses(String packageName, ClassLoader classLoader)
            throws ClassNotFoundException, IOException {
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
    private static List<Class> findClasses(File directory, String packageName)
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
    private static void processServiceClass(Class clazz, Table<HttpMetod, String, ServiceMetaData> table) {
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
    private static void processMethod(Method method, Table<HttpMetod, String, ServiceMetaData> table) {
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
