package exceptions;

import java.io.IOException;

/**
 * Exception class that the other exception classes inherit, used for miscallenous exceptions
 */
public class InvalidException extends IOException{
	
	/**
	 * Default constructor for class
	 */
	public InvalidException() {
		
		super("Error: Input row cannot be parsed due to missing information");
	}
	
	/**
	 * Paramterized constructor for class
	 * @param msg message to be printed to user
	 */
	public InvalidException(String msg) {
		
		super(msg);
	}
}
