package it.at.script;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationParameterHandler {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationParameterHandler.class);
	
	public static final String BYNARY_LOG_DIR = "/bin/";
	public static final String GENERAL_QUERY_LOG_FILE = "/general.log";
	public static final String NAMES_FILE = "/names.properties";
	public static final String LOAD_DATA_DIR = "/load-data/";	
		
	public ApplicationParameter run() throws Exception {
		final Scanner scanner = new Scanner(System.in);
		final ApplicationParameter result = new ApplicationParameter();

		//numberOfThread PARAM
		Integer numberOfThread;
		try {
			System.out.println("Number of Thread:");
			numberOfThread = new Integer(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("No number received, add default !!?");

			numberOfThread = 5;
		}
		logger.info("Supplied Number of Thread: " + numberOfThread);
		result.setNumberOfThread(numberOfThread);
				
		//serverName PARAM
		String serverName = null;
		while (StringUtils.isBlank(serverName)) {
			System.out.println("Db Server:");
			serverName = scanner.nextLine();					
		}		
		logger.info("Supplied Db Server: " + serverName);
		result.setDatabaseServer(serverName);
		
		//pathOfPayload PARAM
		String pathOfPayload = null;
		while (StringUtils.isBlank(pathOfPayload)) {
			System.out.println("Path of payload:");
			pathOfPayload = scanner.nextLine();					
		}		
		logger.info("Supplied Path of payload: " + pathOfPayload);
		result.setPathOfPayload(pathOfPayload);
		
		//databaseUser PARAM
        String databaseUser = null;
        while (StringUtils.isBlank(databaseUser)) {
            System.out.println("Db User:");
            databaseUser = scanner.nextLine();                    
        }       
        logger.info("Supplied Db User: " + databaseUser);
        result.setDatabaseUser(databaseUser);
		
	    //databasePassword PARAM
        String databasePassword = null;
        while (StringUtils.isBlank(databasePassword)) {
            System.out.println("Db Password:");
            databasePassword = scanner.nextLine();                    
        }       
        logger.info("Supplied Db Password: " + databasePassword);
        result.setDatabasePassword(databasePassword);
		
		scanner.close();
		
		check(pathOfPayload);	
				
		return result;
	}

	public void check(String pathOfPayload) throws FileNotFoundException {
		if (!isAccesibleDir(pathOfPayload)) {
			throw new FileNotFoundException("Can't read: " + pathOfPayload);
		}
		
		if (!isAccesible(pathOfPayload + GENERAL_QUERY_LOG_FILE)) {
			throw new FileNotFoundException("Can't read file: " + pathOfPayload + GENERAL_QUERY_LOG_FILE);
		}
		
		if (!isAccesibleDir(pathOfPayload + BYNARY_LOG_DIR)) {
			throw new FileNotFoundException("Can't read: " + pathOfPayload + BYNARY_LOG_DIR);
		}
				
		if (!isAccesibleDir(pathOfPayload + LOAD_DATA_DIR)) {
			throw new FileNotFoundException("Can't read: " + pathOfPayload + LOAD_DATA_DIR);
		}
		
		System.out.println("####################################################################################################");
		System.out.println("Each binary log file must be pre-processed with: (mysqlbinlog --verbose bin.000003 > bin.000003.txt)");
		System.out.println("####################################################################################################");
	}
	
	public static boolean isAccesibleDir(final String path) {
		final File f = new File(path);		
		
		return f.isDirectory() && isAccesible(f);
	}	

	private static boolean isAccesible(final File f) {
		return f.exists() && f.canRead();
	}	

	public static boolean isAccesible(final String path) {
		final File f = new File(path);		
		
		return isAccesible(f);
	}	
}
