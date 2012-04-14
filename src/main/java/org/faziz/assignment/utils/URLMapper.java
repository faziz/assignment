package org.faziz.assignment.utils;

import com.google.common.collect.Table;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.faziz.assignment.service.meta.HttpMetod;
import org.faziz.assignment.web.ServiceMetaData;

/**
 * This class provides URL->Service mapping functionality.
 * @author faisal
 */
public final class URLMapper {
    
    private static final Logger logger = Logger.getLogger(URLMapper.class.getName());
    
    private static URLMapper mapper = null;

    private Table<HttpMetod, String, ServiceMetaData> urlServiceMap = null;
    
    private URLMapper(Table<HttpMetod, String, ServiceMetaData> urlServiceMap) {
        this.urlServiceMap = urlServiceMap;
    }    
    
    public static URLMapper getInstance(Table<HttpMetod, String, ServiceMetaData> urlServiceMap){
        if( mapper == null ){
            mapper = new URLMapper(urlServiceMap);
        }
        return mapper;
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
    public final ServiceMetaData getService( 
            final HttpMetod httpMetod, 
            final String actionRequest) {
        ServiceMetaData metaData  = urlServiceMap.get( httpMetod, actionRequest);
        
        //Lets make a pass.
        if(null == metaData){
            Set<String> keys = urlServiceMap.row(httpMetod).keySet();
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
                metaData = urlServiceMap.row(httpMetod).get(key);
                
                if( metaData != null)
                    break;
            }
        }
        
        return metaData;
    }
}
