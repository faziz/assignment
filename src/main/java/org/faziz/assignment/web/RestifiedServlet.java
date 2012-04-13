package org.faziz.assignment.web;

import org.faziz.assignment.utils.ApplicationConstants;
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
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;
import org.faziz.assignment.service.SecurityService;
import org.faziz.assignment.service.exception.UnauthorizedAccessException;
import org.faziz.assignment.service.meta.HttpMetod;
import org.faziz.assignment.utils.ApplicationUtils;

/**
 * A front controller servlet for REST calls.
 * @author faisal
 */
public class RestifiedServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(RestifiedServlet.class.getName());
    
    @PersistenceUnit(name="assignment")
    private EntityManagerFactory entityManagerFactory;
    
    @Resource
    private UserTransaction tx;
    
    @EJB
    private SecurityService securityService;
    
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
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            logger.log(Level.INFO, "entityManagerFactory: {0}", entityManagerFactory);
            
            String contentType = getContentType(request);
            tx.begin();
            
            Object result = processRESTRequest(request, response, 
                    HttpMetod.valueOf(request.getMethod()));
            
            tx.commit();
            
            writeContent(result, out, contentType);
        } catch(NoSuchRESTRequestMappingFoundException ex){
            logger.log(Level.SEVERE, "Resource not found: ", ex);
            rollback();
            response.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
        }catch(Exception ex){
            logger.log(Level.SEVERE, "Exception occurred: ", ex);
            rollback();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }finally {            
            out.close();
        }
    }
    
    private final void rollback(){
        try {
            tx.rollback();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Exception occurred: ", ex);
        }
    }
    
    private final void writeContent(final Object data, final Writer writer, final String contentType) 
            throws IOException, JAXBException{
        if( "application/json".equalsIgnoreCase(contentType)){
            writeJSON(data, writer);
        }else{
            writeXML(data, writer);
        }
    }
    
    private final Object processRESTRequest(final HttpServletRequest request, 
            final HttpServletResponse response, 
            final HttpMetod httpMetod) 
        throws NoSuchMethodException, 
            InstantiationException, 
            IllegalAccessException, 
            IllegalArgumentException, 
            InvocationTargetException, 
            IOException{
        
        String requestURI = request.getRequestURI();
        String actionRequest = requestURI.substring(requestURI.indexOf("api") + 3);        
        logger.log(Level.INFO, "actionRequest: {0}", actionRequest);
        
        Table<HttpMetod, String, ServiceMetaData> table = (Table<HttpMetod, String, ServiceMetaData>) 
                getServletContext().getAttribute(ApplicationConstants.REQUEST_MAP);
        
        Object result = null;
        ServiceMetaData service = null;
        
        if( httpMetod != null ){
            service = getServiceMetaData(table, httpMetod, actionRequest);
        }else{
            throw new IllegalArgumentException("Please privide http method type. " + HttpMetod.values());
        }
        
        if(null != service) {
            if(service.requireAuthentication()){
                checkAuthorization(request);
            }
            
            logger.log(Level.INFO, "metaData: {0}", service);
            result = invokeService(request, service, actionRequest);
        }else{
            throw new NoSuchRESTRequestMappingFoundException(
                "REST request mapping not found." + actionRequest);
        }
        
        return result;
    }
    
    /**
     * Provides authorization assistance.
     * @param request 
     * @throws UnauthorizedAccessException if username, password and API token 
     * parameters are not found in the request or if the parameters do not match 
     * with the system.
     */
    private final void checkAuthorization(final HttpServletRequest request){
        String userName = request.getParameter(ApplicationConstants.REQUEST_USERNAME_PARAM_NAME);
        String password = request.getParameter(ApplicationConstants.REQUEST_PASSWORD_PARAM_NAME);
        String token = request.getParameter(ApplicationConstants.REQUEST_APITOKEN_PARAM_NAME);

        if( StringUtils.isEmpty(userName) || StringUtils.isEmpty(password) 
                || StringUtils.isEmpty(token)){
            throw new UnauthorizedAccessException();
        }

        if( securityService.isAuthentic(userName, password, token) == false){
            throw new UnauthorizedAccessException(userName);
        }
    }
    
    /**
     * Tries to load content type from the request. If not found, then sets it to 
     * application/json.
     * 
     * Only application/json and application/xml are supported content types.
     * @param request
     * @return 
     */
    private final String getContentType(final HttpServletRequest request){
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

    /**
     * Invokes REST service for the given HTTP request.
     * 
     * @param request
     * @param metaData
     * @param actionRequest 
     * @return Returns data if supported by the REST service else null.
     * 
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws IOException 
     */
    private final Object invokeService(final HttpServletRequest request, 
            final ServiceMetaData metaData, 
            final String actionRequest) throws NoSuchMethodException, 
            InstantiationException, 
            IllegalAccessException, 
            IllegalArgumentException, 
            InvocationTargetException, 
            IOException {
        logger.info("Invoking service...");
        
        Method method = metaData.getMethod();
        Constructor<?> constructor = method.getDeclaringClass().getConstructor(
             EntityManager.class);
        
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Object newInstance = constructor.newInstance(entityManager);
        
        Map<String, String[]> parameters = request.getParameterMap();        
        String ajaxPostedData = getDataFromRequest(request);
        Object[] dataFromURI = getParameterDataFromURI(actionRequest, metaData);
        
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] methodInputParameters = new Object[parameterTypes.length];
        methodInputParameters[0] = parameters;
        
        if( parameterTypes.length > 1){
            Object postedData = null;
            if( StringUtils.isNotEmpty(ajaxPostedData)){
                postedData = readData(ajaxPostedData, 
                    parameterTypes[StringUtils.countMatches(metaData.getName(), "*") + 1]);
            }
            
            int index = 1;
            for (int dataFromURIIndex = 0; dataFromURIIndex < dataFromURI.length; dataFromURIIndex++, index++) {
                methodInputParameters[index] = dataFromURI[dataFromURIIndex];
            }
            if( index < parameterTypes.length){
                methodInputParameters[index] = postedData;
            }
        }
        
        Object result = method.invoke(newInstance, methodInputParameters);
        
        entityManager.flush();
        entityManager.close();
        
        return result;
    }
    
    /**
     * Extracts posted AJAXED data from the request.
     * TODO: HACK.
     * 
     * @param request
     * @return 
     */
    private final String getDataFromRequest(HttpServletRequest request){
        String data = null;
        
        //For GET request, AJAX data is posted with parameters string.
        //For the rest of the methods AJAX data is posted with request body.
        if( request.getMethod().equals( HttpMetod.GET.toString()) ){
            Map<String, String[]> parameterMap = request.getParameterMap();
            Set<String> keys = parameterMap.keySet();
            for (String key : keys) {
                //TODO: HACK.
                if(key.startsWith("{")) {
                   data = key;
                }
            }
        }else{
            data = ApplicationUtils.getPostData(request);
        }
        
        return data;        
    }
    
    /**
     * Reads JSON data string to create a new instance.
     * @param data
     * @param clazz
     * @return
     * @throws IOException 
     */
    private final Object readData(final String data, Class clazz) throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        AnnotationIntrospector introspector = new JaxbAnnotationIntrospector();
        // make deserializer use JAXB annotations (only)
        mapper.getDeserializationConfig().setAnnotationIntrospector(introspector);
        // make serializer use JAXB annotations (only)
        mapper.getSerializationConfig().setAnnotationIntrospector(introspector);
        
        return mapper.readValue(data, clazz);
    }
    
    

    /**
     * Writes JSON data to the servlet stream.
     * @param data
     * @param writer
     * @throws IOException 
     */
    private final void writeJSON(final Object data, final Writer writer) 
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
    private final void writeXML(final Object data, final Writer writer) 
        throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(data.getClass());
        Marshaller marshaller = context.createMarshaller();
        
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(data, stringWriter);
        IOUtils.write( stringWriter.toString(), writer);
    }

    /**
     * Tries to match to a given request URL with the mapping supported in the
     * system.
     * 
     * TODO: HACK
     * TODO: Not very elegant. Will have to redesign it. Will have to use REGEX
     *  to simplify it.
     * 
     * @param table
     * @param httpMetod
     * @param actionRequest
     * @return 
     */
    private final ServiceMetaData getServiceMetaData(final Table<HttpMetod, String, 
            ServiceMetaData> table, 
            final HttpMetod httpMetod, 
            final String actionRequest) {
        ServiceMetaData metaData  = table.get( httpMetod, actionRequest);
        
        //Lets make a pass.
        if(null == metaData){
            Set<String> keys = table.row(httpMetod).keySet();
            for (String key : keys) {
                logger.log( Level.INFO, "key: {0}", key);
                
                //TODO: aaargh this is so ugly i wanna puke.
                if( key.split("/").length == actionRequest.split("/").length ){
                    if( key.contains("/*/")){
                        logger.log( Level.INFO, "sub-resource is required.");
                        String[] keySplit = key.split("/");
                        String[] actionSplit = actionRequest.split("/");
                        
                        //If this loop finishes it means our actionRequest matches the 
                        //key.
                        for (int index = 0; index < keySplit.length; index++) {
                            String keyPart = keySplit[index];
                            String actionPart = actionSplit[index];
                            
                            //Do not compare '*'
                            if( keyPart.equals("*") == false){
                                if(keyPart.equals(actionPart) == false){
                                    return null;
                                }
                            }
                        }
                    }
                }
                metaData = table.row(httpMetod).get(key);
                
                if( metaData != null)
                    break;
            }
        }
        
        return metaData;
    }    

    /**
     * Retrieves data part from the URL. 
     * 
     * @param actionRequest
     * @param metaData
     * @return 
     */
    private final Object[] getParameterDataFromURI(String actionRequest, ServiceMetaData metaData) {
        List data = new ArrayList();
        String[] handlerSplit = metaData.getName().split("/");
        String[] actionSplit = actionRequest.split("/");
        //If this loop finishes it means our actionRequest matches the 
        //key.
        Class<?>[] parameterTypes = metaData.getMethod().getParameterTypes();
        int inputCount = 0;
        for (int index = 0; index < handlerSplit.length; index++) {
            String keyPart = handlerSplit[index];
            String actionPart = actionSplit[index];

            //Do not compare '*'
            if( keyPart.equals("*") == true){
                inputCount++;
                Class<?> clazz = parameterTypes[inputCount];
                if( Integer.TYPE == clazz){
                    logger.log(Level.INFO, "Integer input? {0}", actionPart); 
                    data.add(Integer.valueOf(actionSplit[index]));
                }else{
                    data.add(actionSplit[index]);
                }
                
            }
        }
        
        return data.toArray( new Object[data.size()]);
    }
}
