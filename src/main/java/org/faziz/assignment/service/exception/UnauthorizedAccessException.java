package org.faziz.assignment.service.exception;

/**
 *
 * @author faziz
 */
public class UnauthorizedAccessException extends BusinessException {    

    /**
     * Constructs an instance of <code>UnauthorizedAccessException</code> with the specified detail message.
     * @param username username of the user who tried to access the resource.
     */
    public UnauthorizedAccessException(String username) {
        super(username + " is not allowed to access the required resource.");
    }

    public UnauthorizedAccessException() {
        super("Username, password and API token not provided.");
    }
}
