package org.faziz.assignment.service.exception;

/**
 *
 * @author faisal
 */
public abstract class BusinessException extends RuntimeException {
    /**
     * Constructs an instance of
     * <code>BusinessException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public BusinessException(String msg) {
        super(msg);
    }
}
