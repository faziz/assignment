package org.faziz.assignment.web;

import org.faziz.assignment.service.meta.HttpMetod;

/**
 *
 * @author faisal
 */
public class ServiceMetaData {
    private String className = null;
    private HttpMetod method = null;
    private String name = null;
    private boolean authenticate;

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @return the method
     */
    public HttpMetod getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(HttpMetod method) {
        this.method = method;
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
}
