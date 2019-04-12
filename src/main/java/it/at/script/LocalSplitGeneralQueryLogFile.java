package it.at.script;

import static it.at.script.ApplicationParameterHandler.*;
import java.io.FileNotFoundException;
import it.at.os.SplitGeneralQueryLogFile;
import it.at.runner.ApplicationRunner;

public class LocalSplitGeneralQueryLogFile {
	public static void main(String[] args) throws Exception {		
        try {
            if (args.length == 1) {
                
                final String pathOfPayload = args[0];
                final String generalLogQueryFile = pathOfPayload + GENERAL_QUERY_LOG_FILE;
                final String filesDir = pathOfPayload + ApplicationRunner.SUB_PATH_FILES;
                
                if (!isAccesible(generalLogQueryFile)) {
                    throw new FileNotFoundException("Can't read file: " + generalLogQueryFile);
                }                
                
                new SplitGeneralQueryLogFile().run(generalLogQueryFile, filesDir);
                
            } else {
                
                System.out.println("Execute with: absolutePathOfPayload");
                System.out.println(">java -jar mysql-stress-tool-pre1.jar /x");
                
            }                               
        } catch (Exception e) {         
            System.err.println(e);
        }       
	}
}
