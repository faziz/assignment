package org.faziz.assignment.service;

/**
 *
 * @author faziz
 */
public interface SecurityService {
    
    /**
     * Authenticates the credentials. If matched returns true else false.
     * @param userName
     * @param password
     * @param apiToken
     * @return true if the credentials matches or false.
     */
    public boolean isAuthentic(String userName, String password, String apiToken);
}
