package com.vagm.vagmdroid.exceptions;

/**
 * The Class ControllerNotFoundException.
 * @author Roman_Konovalov
 */
public class ControllerNotFoundException extends Exception {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
	 * 
	 */
    public ControllerNotFoundException() {
    }

    /**
     * @param detailMessage
     */
    public ControllerNotFoundException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * @param throwable
     */
    public ControllerNotFoundException(Throwable throwable) {
        super(throwable);
    }

    /**
     * @param detailMessage
     * @param throwable
     */
    public ControllerNotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

}
