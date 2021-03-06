package it.at.runner;

import it.at.script.ApplicationParameter;
import it.at.script.ApplicationParameterHandler;
import it.at.script.PayloadAsync;

import java.beans.PropertyVetoException;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.PooledDataSource;

public abstract class ApplicationRunner {
	protected static final Logger logger = LoggerFactory.getLogger(ApplicationRunner.class);
	public static final String SUB_PATH_FILES = "/files/";
	
	public void run() throws Exception {
		logger.info("Give me application parameters ...");
		final ApplicationParameter params = new ApplicationParameterHandler().run();

		realRun(params);		
	}
	
	public void run(final ApplicationParameter params) throws Exception {
		realRun(params);
	}
	
	protected abstract void realRun(final ApplicationParameter params) throws Exception; 

	protected PooledDataSource buildDataSource(final ApplicationParameter params) {
		final ComboPooledDataSource ds = new ComboPooledDataSource();
		
		try {
			ds.setDriverClass("com.mysql.jdbc.Driver");
		} catch (PropertyVetoException e) {
			logger.error(e.getMessage(), e);
		}		
		
		ds.setJdbcUrl("jdbc:mysql://" + params.getDatabaseServer() + ":3306/"); //add ?profileSQL=true for profile all sql stament with c3p0
		ds.setUser(params.getDatabaseUser());
		ds.setPassword(params.getDatabasePassword());
						
		ds.setMinPoolSize(params.getNumberOfThread());		
		ds.setMaxPoolSize(params.getNumberOfThread() * 2);
		ds.setAcquireIncrement(5);
		
		//Defines how many times c3p0 will try to acquire a new Connection from the database before giving up. If this value is less than or equal to zero, c3p0 will keep trying to fetch a Connection indefinitely.
		ds.setAcquireRetryAttempts(3);
		
		//Seconds a Connection can remain pooled but unused before being discarded. Zero means idle connections never expire.
		ds.setMaxIdleTime(5); 
		
		//numHelperThreads and maxAdministrativeTaskTime help to configure the behavior of DataSource thread pools. By default, each DataSource has only three associated helper threads. 
		//If performance seems to drag under heavy load, or if you observe via JMX or direct inspection of a PooledDataSource, that the number of "pending tasks" is usually greater than zero, 
		//try increasing numHelperThreads. maxAdministrativeTaskTime may be useful for users experiencing tasks that hang indefinitely and "APPARENT DEADLOCK" messages. (See Appendix A for more.)
		ds.setNumHelperThreads(8); //default is 3
		
		//maxAdministrativeTaskTime Default: 0 Seconds before c3p0's thread pool will try to interrupt an apparently hung task. Rarely useful. Many of c3p0's functions are not performed by client threads, 
		//but asynchronously by an internal thread pool. c3p0's asynchrony enhances client performance directly, and minimizes the length of time that critical locks are held by ensuring that slow jdbc operations are performed in non-lock-holding threads. 
		//If, however, some of these tasks "hang", that is they neither succeed nor fail with an Exception for a prolonged period of time, c3p0's thread pool can become exhausted and administrative tasks backed up. 
		//If the tasks are simply slow, the best way to resolve the problem is to increase the number of threads, via numHelperThreads. But if tasks sometimes hang indefinitely, you can use this parameter to force a call to the task thread's interrupt() method if a task exceeds a set time limit. 
		//[c3p0 will eventually recover from hung tasks anyway by signalling an "APPARENT DEADLOCK" (you'll see it as a warning in the logs), replacing the thread pool task threads, and interrupt()ing the original threads. But letting the pool go into APPARENT DEADLOCK and then recover means that for some periods, 
		//c3p0's performance will be impaired. So if you're seeing these messages, increasing numHelperThreads and setting maxAdministrativeTaskTime might help. maxAdministrativeTaskTime should be large enough that any resonable attempt to acquire a Connection from the database, to test a Connection, or two destroy a Connection, 
		//would be expected to succeed or fail within the time set. Zero (the default) means tasks are never interrupted, which is the best and safest policy under most circumstances. If tasks are just slow, allocate more threads. If tasks are hanging forever, try to figure out why, and maybe setting maxAdministrativeTaskTime can help in the meantime.  
		ds.setMaxAdministrativeTaskTime(100);
		
		return ds;
	}	
	
	protected void addHookForCancelTask(final Collection<? extends Future<PayloadAsync>> futureData, final PooledDataSource ds) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				
				try {
					ds.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}
				
				logger.info("END'S !!");
				
				/*
				  se durante l'esecuzione dei thread che eseguono gli statement
				  un ctlr-c è intercettato ...
				   
				  cancello le altre schedulazioni che non hanno ancora eseguito le operazioni
				  e stampo solo quello che è stato eseguito veramente!!?
				  
				  altrimenti la cancellazione di un thread che ha già terminato 
				  non ha alcun effetto.
				 */
				
				for (Future<PayloadAsync> scheduledFuture : futureData) {
					scheduledFuture.cancel(true);
				}
				
			}
		});		
	}
	
	protected List<Integer> retriveConnectionFiles(final File folder) {
		final List<Integer> result = new ArrayList<Integer>();
		
	    for (final File fileEntry : folder.listFiles()) {
	        if (!fileEntry.isDirectory()) {
	        	try {
		        	final Integer i = Integer.valueOf(fileEntry.getName());
		        	
		        	result.add(i);	        		
	        	} catch (NumberFormatException nfe) { }
	        }
	    }
	    
	    Collections.sort(result);
	    
	    return result;
	}
	
}
