import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import java.util.Scanner;
import java.io.PrintWriter;
import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import exceptions.*;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Driver and main class of code for CSV to JSON file converter
 */
public class FileConverter {
	
	/**
	 * Constants for directories used for files
	 */
	private final static String directoryCSV = "CSV files\\", directoryJSON = "JSON files\\";
	
	/**
	 * Reads input from user/Reads input file to be converted
	 */
	private static Scanner in, fileCSV;
	
	/**
	 * Writes to log file/Writes output of converted file
	 */
	private static PrintWriter log, fileJSON;
	
	/**
	 * File to be read
	 */
	private static BufferedReader readFile;
	
	/**
	 * 2D array keeping track of information from input file
	 */
	private static String[][] data;
	
	/**
	 * String buffers used in various places
	 */
	private static String fileName, buffer, processBuffer, directory;
	
	/**
	 * int counters used in various places
	 */
	private static int a, b, i, j, lastLine, count, lines, numOfMissingField;
	
	/**
	 * boolean variable used to break from write files method
	 */
	private static boolean breakMethod;
	
	/**
	 * Method used to display the array for debugging
	 */
	public static void debug() {
		
		for (a = 0; a <data.length; a++) {
			
			for(b = 0; b <data[0].length; b++) {
				System.out.print(data[a][b]+"  |  ");
			}
			System.out.println("\n____________________________________________________________________");
		}
	}
	
	/**
	 * Main method containing the initialization of the log file, and gives prompts to user on what they can do with the program
	 */	
	public static void main(String[] args) {
		
		try {
			
			log = new PrintWriter(new FileOutputStream("FileConverterLog.txt",true));
			Date date = new Date();
			SimpleDateFormat formatter =  new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			log.println(formatter.format(date) + "\n");
		}
		catch(FileNotFoundException e) {System.exit(0);}
		
		in = new Scanner(System.in);
		System.out.print("Hey, welcome to the CSV to JSON file converter.\n\n\n");
		
		System.out.print("\nDo you want to convert any files? (Type yes or anything else for no) ");
		buffer=in.nextLine();
		
		while(buffer.toLowerCase().equals("yes")) {
			
			processFilesForValidation();
			
			System.out.print("\nDo you want to convert another file? (Type yes or anything else for no) ");
			buffer=in.nextLine();
			log.flush();
		}
		System.out.print("\nDo you want to read any files? (Type yes or anything else for no) ");
		buffer=in.nextLine();
		
		
		while(buffer.toLowerCase().equals("yes")) {
			
			readFiles();
			
			System.out.print("\nDo you want to read another file? (Type yes or anything else for no) ");
			buffer=in.nextLine();
			log.flush();
		}
		System.out.print("\n\n\nThank you for checking out my program, have a great day.");
		in.close();
		log.close();
	}	
	
	/**
	 * Method used to read and validate the input file, then create and write the converted output file.
	 */
	public static void processFilesForValidation() {
		
		//----------------------------------------------------------------------------Creation of Scanner object-------------------------------------------------------------------------------
		
		System.out.print("\nIs the file you want to convert in the CSV files directory? (Type yes or anything else for no) ");
		buffer = in.nextLine();
	
		if(buffer.toLowerCase().equals("yes"))
			
			directory = directoryCSV;
		
		else {
			
			System.out.print("\nPlease input the relative path of your file from the directory of the project: ");
			directory = in.nextLine();
		}
		
		System.out.print("\nPlease input the file name: ");
		fileName = in.nextLine();
		
		try {
			fileCSV = new Scanner(new FileInputStream(directory + fileName));
			
			lines = 0;
			
			while(fileCSV.hasNextLine() && !fileCSV.nextLine().equals(""))
				
				lines++;
			
			if(lines <= 1)
				throw new InvalidException("\n" + fileName + " is either empty or improperly formatted, cannot be processed.\nProgram will exit after closing all open files.\n\n");
			
			fileCSV = new Scanner(new FileInputStream(directory + fileName));
		}
		catch(FileNotFoundException e) {
			
			System.out.println(e.getMessage() + "\nProgram will exit after closing all open files.\n\n");
			log.println(e.getMessage() + "\nProgram will exit after closing all open files.\n\n");
			close();
		}
		catch(InvalidException e) {
			
			System.out.println(e.getMessage());
			log.println(e.getMessage());
			close();
		}
		
		//--------------------------------------------------------------------------Validation of Scanner Object--------------------------------------------------------------------------------
		
		try {
			
			breakMethod = false; 
			buffer = fileCSV.nextLine();
			
			data = new String[lines+1][(countLine(buffer))+1];
			
			if(!processLine(1,buffer))
				
				throw new CSVFileInvalidException("\n" + fileName + " is invalid, it has missing attributes, will not be converted.");
		}
		catch(CSVFileInvalidException e) {
			
			System.out.println(e.getMessage());
			log.println(e.getMessage());
			
			log.println(numOfMissingField + " missing attributes, " + (data[0].length-numOfMissingField-1) + " filled attributes.");
			
			for(j = 1; j < data[0].length; j++) {
				
				log.print(data[1][j] + "   ");
				
			}
			
			log.println("\n\n");
			
			breakMethod = true; 
		}
		
		if(breakMethod)
			return;
		
		//----------------------------------------------------------------------------Creation of PrintWriter Object---------------------------------------------------------------------------
		
		if(fileName.contains(".txt"))
			buffer = fileName.substring(0, fileName.indexOf(".txt"))+".json";
		
		else
			buffer = fileName + ".json";
		
		try {
			
			fileJSON = new PrintWriter(new FileOutputStream(directoryJSON + buffer));
			System.out.println("\nNew converted JSON file in JSON folder will be called: " + buffer);
		}
		catch(FileNotFoundException e) {
			
			System.out.println(e.getMessage() + "\nProgram will exit after closing all open files.\n\n");
			log.println(e.getMessage() + "\nProgram will exit after closing all open files.\n\n");
			close();
		}
		//-----------------------------------------------------------------------------Writing of PrintWriter Object---------------------------------------------------------------------------
		
		for(i = 2; i < data.length; i++) {
			
			try {
				buffer = fileCSV.nextLine();
				
				if(countLine(buffer)!=data[0].length-1)
					
					throw new InvalidException("\nLine " + (i-1) + " is has too many or too little columns, line will not be written to JSON file.\n");
				
				if(!processLine(i,buffer))
					
					throw new CSVDataMissing("\nLine " + (i-1) + " has missing data, will not be written to JSON file");
				
				data[i][0] = "true";
			}
			catch(CSVDataMissing e) {
				
				System.out.println(e.getMessage());
				log.println(e.getMessage());
				
				for(j = 1; j < data[0].length; j++) {
					
					log.print(data[i][j] + "   ");
				}
				
				log.print("\nMissing data:   ");
				
				for(j = 1; j < data[0].length; j++) {
					
					if(data[i][j].equals("***")) {
						
						log.print(data[1][j] + "   ");
					}
				}
				
				log.println("\n");
				
				data[i][0] = "false";
			}
			catch(InvalidException e) {
				
				System.out.println(e.getMessage());
				log.println(e.getMessage());
				data[i][0] = "false";
			}
		}
		
		for(j = 1; j < data[0].length; j++) {
			
			for(i = 2; i < data.length; i++) {
				
				if(data[i][0].equals("true")) {
					
					if(data[0][j] != "s" && isDouble(data[i][j])) {
					
						data[0][j] = "";
					}
					else
						data[0][j] = "s";
				}
			}
		}
		
		for(i = 2; i < data.length; i++) {
			
			if(data[i][0].equals("true"))
				lastLine = i;
		}
		
		fileJSON.println("[");
		for(i = 2; i <= lastLine; i++) {
			
			if(data[i][0].equals("true")) {
			
			fileJSON.println("   {");
			
				for(j = 1; j < data[0].length; j++) {
					
					if(data[0][j].equals("s")){
						fileJSON.println("      \""+data[1][j]+"\":  \"" + data[i][j]+"\",");
					}
					else
						fileJSON.println("      \""+data[1][j]+"\":  " + data[i][j]+",");
				}
			
			if (i != lastLine)
				fileJSON.println("   },");
				
			else
				fileJSON.println("   }");
			}
		}
		fileJSON.print("]");
		
		log.println(fileName + " was successfully converted to JSON\n\n");
		
		fileCSV.close();
		fileJSON.close();
	}
	
	/**
	 * Helper method to count how many attributes a line has, returns that value
	 * @param line current line we are reading
	 * @return the number of different attributes
	 */
	public static int countLine(String line) {
		
		processBuffer = line;
		count = 0;
		
		while(!processBuffer.equals("")) {
			
			count++;
			
			if(processBuffer.indexOf("\"") == 0 && processBuffer.contains("\",")) 
				processBuffer = processBuffer.substring(processBuffer.indexOf("\",") + 2);
					
			else if(processBuffer.indexOf("\"") == 0 && !processBuffer.contains("\","))
				processBuffer = "";
			
			else if(processBuffer.contains(","))
				processBuffer = processBuffer.substring(processBuffer.indexOf(",") + 1);
			
			else
				processBuffer = "";
		}
		
		return count;
	}
	
	/**
	 * Helper method used to split the line into different attributes to place them in the array,
	 * return whether or not there are empty attributes on that line.
	 * @param row the number of the line corresponding to the array
	 * @param line the line we want to process
	 * @return true if there is no missing attributes, false otherwise
	 */
	public static boolean processLine(int row, String line) {
		
		processBuffer = line;
		count = 0;
		numOfMissingField = 0;
		
		while(!processBuffer.equals("")) {
			
			count++;
			
			if(processBuffer.indexOf("\"") == 0 && processBuffer.contains("\",")) {
				
				data[row][count]= processBuffer.substring(1,processBuffer.indexOf("\","));
				processBuffer = processBuffer.substring(processBuffer.indexOf("\",") + 2);
			}
			else if(processBuffer.indexOf("\"") == 0 && !processBuffer.contains("\",")) {
				
				data[row][count] = processBuffer.substring(1,processBuffer.length()-1);
				processBuffer = "";
			}
			else if(processBuffer.contains(",")) {
				
				if(processBuffer.indexOf(",")==0) {
					numOfMissingField++;
					data[row][count] = "***";
				}
				else
					data[row][count] = processBuffer.substring(0 , processBuffer.indexOf(","));
				
				processBuffer = processBuffer.substring(processBuffer.indexOf(",") + 1);
			}
			else {
				
				data[row][count] = processBuffer;
				processBuffer = "";
			}	
		}
		
		if(numOfMissingField == 0)
			return true;
		
		return false;
	}
	
	/**
	 * Helper method used to check if a string can be parsed to a double
	 * @param doub input string
	 * @return true if the string can be parsed to a double, false otherwise
	 */
	public static boolean isDouble(String doub) {
		
		try {
			Double.parseDouble(doub);
			return true;
		}
		catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * Method used to read and print the file the user chooses to print
	 */
	public static void readFiles() {
		
		System.out.print("\nIs the file you want to read in the JSON files directory? (Type yes or anything else for no) ");
		buffer = in.nextLine();
	
		if(buffer.toLowerCase().equals("yes"))
			
			directory = directoryJSON;
		
		else {
			
			System.out.print("\nPlease input the relative path of your file from the directory of the project: ");
			directory = in.nextLine();
		}
			
	
		System.out.print("\nPlease input the file name: ");
		buffer = in.nextLine();
		System.out.println("\n");
		
		try {
			
			readFile = new BufferedReader(new FileReader(directory + buffer));
		}
		catch(FileNotFoundException e) {
			
			System.out.println(e.getMessage() + "\nProgram will exit after closing all open files.");
			log.println(e.getMessage() + "\nProgram will exit after closing all open files.\n\n");
			close();
		}
		
		try {
			
			while((buffer = readFile.readLine())!=null) {
				
				System.out.println(buffer);
			}
			System.out.println();
			readFile.close();
			
		} catch (IOException e) {
			
			System.out.println(e.getMessage() + "\nProgram will exit after closing all open files.");
			log.println(e.getMessage() + "\nProgram will exit after closing all open files.\n\n");
			close();
		}
		
		try {readFile.close();}
		catch(Exception e){}
	}
	
	/**
	 * Method used for cases when we want to close the program, closes all open IO streams first before exiting.
	 */
	public static void close() {
		
		in.close();
		
		try {log.close();}
		catch(Exception e){}
		
		try {fileCSV.close();}
		catch(Exception e){}
		
		try {fileJSON.close();}
		catch(Exception e){}
		
		try {readFile.close();}
		catch(Exception e) {}
		
		System.exit(0);
	}
}


























