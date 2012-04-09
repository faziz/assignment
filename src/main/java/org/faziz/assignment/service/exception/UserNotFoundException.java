package org.faziz.assignment.service.exception;

/**
 *
 * @author faisal
 */
public class UserNotFoundException extends BusinessException{

    public UserNotFoundException(String username, Throwable cause) {
        super(username + " not found exception");
    }    
}
