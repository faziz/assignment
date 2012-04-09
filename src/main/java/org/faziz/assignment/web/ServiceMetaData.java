package org.faziz.assignment.web;

import java.lang.reflect.Method;
import org.faziz.assignment.service.meta.HttpMetod;

/**
 *
 * @author faisal
 */
public class ServiceMetaData {
    
    private HttpMetod httpMethod = null;
    private String name = null;
    private Method method = null;
    private boolean authenticate;
    
    /**
     * @return the method
     */
    public HttpMetod getHttpMethod() {
        return httpMethod;
    }

    /**
     * @param method the method to set
     */
    public void setHttpMethod(HttpMetod method) {
        this.httpMethod = method;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the authenticate
     */
    public boolean isAuthenticate() {
        return authenticate;
    }

    /**
     * @param authenticate the authenticate to set
     */
    public void setAuthenticate(boolean authenticate) {
        this.authenticate = authenticate;
    }

    /**
     * @return the method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(Method method) {
        this.method = method;
    }

    @Override
    public String toString() {
        return "ServiceMetaData{" + "httpMethod=" + httpMethod + ", name=" + name + 
                ", method=" + method + ", authenticate=" + authenticate + '}';
    }
    
    
}
