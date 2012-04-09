package org.faziz.assignment.web;

import com.google.common.collect.Table;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.faziz.assignment.domain.User;
import org.faziz.assignment.service.meta.HttpMetod;

/**
 *
 * @author faisal
 */
public class RestifiedServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RestifiedServlet.class.getName());
    
    @PersistenceUnit(name="assignment")
    private EntityManagerFactory entityManagerFactory;
    
    
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            logger.log(Level.INFO, "entityManagerFactory: ", entityManagerFactory);
            
            String contentType = getContentType(request);
            Object result = processRESTRequest(request, response, 
                    HttpMetod.valueOf(request.getMethod()));
            
            writeContent(result, out, contentType);
        } catch(NoSuchRESTRequestMappingFoundException ex){
            logger.log(Level.SEVERE, "Resource not found: ", ex);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        }catch(Exception ex){
            logger.log(Level.SEVERE, "Exception occurred: ", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }finally {            
            out.close();
        }
    }
    
    private final void writeContent(Object data, Writer writer, String contentType) throws IOException, JAXBException{
        if( "application/json".equalsIgnoreCase(contentType)){
            writeJSON(data, writer);
        }else{
            writeXML(data, writer);
        }
    }
    
    private final Object processRESTRequest(HttpServletRequest request, HttpServletResponse response, HttpMetod httpMetod) 
            throws NoSuchMethodException, 
            InstantiationException, 
            IllegalAccessException, 
            IllegalArgumentException, 
            InvocationTargetException{
        
        String requestURI = request.getRequestURI();
        String actionRequest = requestURI.substring(requestURI.indexOf("api") + 3);        
        logger.log(Level.INFO, "actionRequest: ", actionRequest);        
        Table<HttpMetod, String, ServiceMetaData> table = (Table<HttpMetod, String, ServiceMetaData>) 
                getServletContext().getAttribute(ApplicationConstants.REQUEST_MAP);
        
        logger.log(Level.INFO, "actionRequest: ", actionRequest);
        
        Object result = null;
        ServiceMetaData metaData = null;
        switch(httpMetod){
            case GET:                
                metaData = getServiceMetaData(table, httpMetod, actionRequest);
                break;
        }
        
        //Request URI was not found.
        if(null != metaData) {
            logger.log(Level.INFO, "metaData: ", metaData);
                result = invokeService(request, metaData, entityManagerFactory);
        }else{
            throw new NoSuchRESTRequestMappingFoundException(
                "REST request mapping not found." + actionRequest);
        }
        
        return result;
    }
    
    
    
    /**
     * Tries to load content type from the request. If not found, then sets it to 
     * application/json.
     * 
     * Only application/json and application/xml are supported content types.
     * @param request
     * @return 
     */
    private final String getContentType(HttpServletRequest request){
        String contentType = request.getHeader("Content-Type");
        if( StringUtils.isEmpty(contentType)){
            contentType = "application/json";
        }
        
        return contentType;
    }
    

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet serving REST requests.";
    }// </editor-fold>

    private final Object invokeService(final HttpServletRequest request, final ServiceMetaData metaData, 
            final EntityManagerFactory entityManagerFactory) throws NoSuchMethodException, 
            InstantiationException, 
            IllegalAccessException, 
            IllegalArgumentException, 
            InvocationTargetException {
        logger.info("Invoking service...");
        
        
        Method method = metaData.getMethod();
        Constructor<?> constructor = method.getDeclaringClass().getConstructor(
             EntityManager.class);
        
        Object newInstance = constructor.newInstance(entityManagerFactory.createEntityManager());
        Map<String, String[]> parameters = request.getParameterMap();
        
        Object result = method.invoke(newInstance, new Object[]{parameters});
        
        return result;
    }

    /**
     * Writes JSON data to the servlet stream.
     * @param data
     * @param writer
     * @throws IOException 
     */
    private final void writeJSON(final Object data, final Writer writer) throws IOException {
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
    private final void writeXML(final Object data, final Writer writer) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(data.getClass());
        Marshaller marshaller = context.createMarshaller();
        
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(data, stringWriter);
        IOUtils.write( stringWriter.toString(), writer);
    }

    private final ServiceMetaData getServiceMetaData(final Table<HttpMetod, String, ServiceMetaData> table, 
            final HttpMetod httpMetod, final String actionRequest) {
        ServiceMetaData metaData  = table.get(HttpMetod.GET, actionRequest);
        
        //Lets make another pass.
        if(null == metaData){
            Set<String> keySet = table.row(httpMetod).keySet();
            for (String key : keySet) {
                logger.log( Level.INFO, "key: ", key);
                
            }
        }
        
        return metaData;
    }
}
