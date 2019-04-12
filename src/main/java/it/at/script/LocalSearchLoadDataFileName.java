package it.at.script;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import it.at.os.GrepLoadDataFileNameRepository;
import it.at.os.SearchLoadDataStatement;

import static it.at.script.ApplicationParameterHandler.*;

public class LocalSearchLoadDataFileName {    
	public static void main(String[] args) throws Exception {
	    try {
	        if (args.length == 1) {

	            final String pathOfPayload = args[0];
	            final String generalLogQueryFile = pathOfPayload + GENERAL_QUERY_LOG_FILE;
	            final String binarys = pathOfPayload + ApplicationParameterHandler.BYNARY_LOG_DIR;
	            
                if (!isAccesible(generalLogQueryFile)) {
                    throw new FileNotFoundException("Can't read file: " + generalLogQueryFile);
                }                
	            
                if (!isAccesibleDir(binarys)) {
                    throw new FileNotFoundException("Can't read dir: " + binarys);
                }                	            
	            
	            System.err.println("Start search LOAD DATA into general query log file");
	            final Set<Integer> idsWithLoadData = new SearchLoadDataStatement().run(generalLogQueryFile);        
	            
	            System.err.println("Start mapping threads into binary log file");
	            final GrepLoadDataFileNameRepository repository = new GrepLoadDataFileNameRepository();
	            repository.run(idsWithLoadData, binarys);       

	            final Properties p = new Properties();
	            
	            for (Entry<Integer, String[]> entry : repository.getResult().entrySet()) {          
	                p.put(String.valueOf(entry.getKey()), Arrays.toString(entry.getValue()));
	            }

	            System.err.println("Writing ...");
	            final File f = new File(pathOfPayload + ApplicationParameterHandler.NAMES_FILE);
	            final OutputStream out = new FileOutputStream(f);
	            p.store(out, "");
	            
	        } else {
	            
	            System.out.println("Execute with: absolutePathOfPayload");
	            System.out.println(">java -jar mysql-stress-tool-pre2.jar /x");
	            
	        }                               
	    } catch (Exception e) {         
	        System.err.println(e);
	    }       
	}    
}
