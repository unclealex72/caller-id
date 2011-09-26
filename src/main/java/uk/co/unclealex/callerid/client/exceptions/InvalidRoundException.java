package uk.co.unclealex.callerid.client.exceptions;

public class InvalidRoundException extends Exception {

	/**
	 * 
	 */
	public InvalidRoundException() {
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InvalidRoundException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public InvalidRoundException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public InvalidRoundException(Throwable cause) {
		super(cause);
	}

}
