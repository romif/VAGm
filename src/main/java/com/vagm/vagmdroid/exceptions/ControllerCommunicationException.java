/**
 * 
 */
package com.vagm.vagmdroid.exceptions;

/**
 * @author Admin
 * 
 */
public class ControllerCommunicationException extends Exception {

    /**
	 * 
	 */
    private static final long serialVersionUID = 4404825151151825168L;

    /**
	 * 
	 */
    public ControllerCommunicationException() {
    }

    /**
     * @param detailMessage
     */
    public ControllerCommunicationException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * @param throwable
     */
    public ControllerCommunicationException(Throwable throwable) {
        super(throwable);
    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ControllerCommunicationException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
