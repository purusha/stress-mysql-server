package it.at.runner;

import it.at.os.LoadDataFileNameRepository;
import it.at.os.PropertyLoadDataFileNameRepository;
import it.at.script.ApplicationCheckPointCounter;
import it.at.script.ApplicationParameter;
import it.at.script.ApplicationParameterHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.mchange.v2.log.FallbackMLog;

public class App {
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	
    public static void main( String[] args ) {        
    	final Injector injector = Guice.createInjector(
			new AbstractModule() {				
				@Override
				protected void configure() {					
					if (null == System.getProperty("delay")) {
						bind(ApplicationRunner.class).to(BruteForceApplicationRunner.class);												
					} else {
						bind(ApplicationRunner.class).to(DelayedApplicationRunner.class);							
					}
					
					bind(LoadDataFileNameRepository.class).to(PropertyLoadDataFileNameRepository.class).in(Singleton.class); //OR GrepLoadDataFileNameRepository.class							
					bind(ApplicationCheckPointCounter.class).in(Singleton.class);
				}
			}
    	);
    	
    	//XXX 
    	// c3p0 non ha un log binded with slf4j ... !!?
    	// visto che di default usa l'impementazione jdk che stampa su sysout
    	// gli alzo il log level a WARNING
    	System.setProperty("com.mchange.v2.log.MLog", FallbackMLog.class.getCanonicalName());
    	System.setProperty("com.mchange.v2.log.FallbackMLog.DEFAULT_CUTOFF_LEVEL", "WARNING");

    	try {
        	if (args.length == 0) {
        		
        		injector.getInstance(ApplicationRunner.class).run();
        		
        	} else if (args.length == 5) {
        		
        		final ApplicationParameter params = new ApplicationParameter(
        		    new Integer(args[0]), args[1], args[2], args[3], args[4]
		        );
        		
        		new ApplicationParameterHandler().check(params.getPathOfPayload());  
        		
        		injector.getInstance(ApplicationRunner.class).run(params);
        		
        	} else {
        		
        		System.out.println("Execute with: numberOfThread databaseServer absolutePathOfPayload databaseUser databasePassword");
        		System.out.println(">java [-Ddelay] [-Ddryrun] -jar mysql-stress-tool.jar 10 db1.at.test /home/alan/Desktop/4 user password");
        		
        	}    	    	        		
    	} catch (Exception e) {    		
    		logger.error(null, e);
    	}    	
    }
       
}

/*

	Export with:
	
	> mvn compile assembly:single
	
	http://stackoverflow.com/questions/574594/how-can-i-create-an-executable-jar-with-dependencies-using-maven

*/
