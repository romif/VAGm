package com.vagm.vagmdroid.exceptions;

/**
 * The Class ControllerWrongResponseException.
 * @author Roman_Konovalov
 */
public class ControllerWrongResponseException extends Exception {


	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 3806305540960561980L;

	/**
	 * constructor.
	 */
	public ControllerWrongResponseException() {
	}

	/**
	 * @param detailMessage
	 */
	public ControllerWrongResponseException(final String detailMessage) {
		super(detailMessage);
	}

	/**
	 * @param throwable
	 */
	public ControllerWrongResponseException(final Throwable throwable) {
		super(throwable);
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public ControllerWrongResponseException(final String detailMessage, final Throwable throwable) {
		super(detailMessage, throwable);
	}

}
