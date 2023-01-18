package exceptions;

/**
 * Exception class used for when there is an attribute missing from the CSV file
 */
public class CSVFileInvalidException extends InvalidException{
	
	/**
	 * Default constructor for class
	 */
	public CSVFileInvalidException() {
		
		super();
	}
	
	/**
	 * Paramterized constructor for class
	 * @param msg message to be printed to user
	 */
	public CSVFileInvalidException(String msg) {
		
		super(msg);
	}
}
