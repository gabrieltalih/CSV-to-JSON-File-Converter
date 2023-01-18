package exceptions;

/**
 * Exception class used for when there is data missing from the CSV file
 */
public class CSVDataMissing extends InvalidException{
	
	/**
	 * Default constructor for class
	 */
	public CSVDataMissing() {
		
		super();
	}
	
	/**
	 * Paramterized constructor for class
	 * @param msg message to be printed to user
	 */
	public CSVDataMissing(String msg) {
		
		super(msg);
	}
}
