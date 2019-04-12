package it.at.runner;

import it.at.os.BuildDelayedTCA;
import it.at.os.LoadDataFileNameRepository;
import it.at.os.PropertyLoadDataFileNameRepository;
import it.at.os.SearchLoadDataStatement;
import it.at.print.PartialResultWriter;
import it.at.script.ApplicationCheckPointCounter;
import it.at.script.ApplicationParameter;
import it.at.script.MyCallable;
import it.at.script.PayloadAsync;
import it.at.script.TimerConnectionAggregator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.inject.Inject;
import com.mchange.v2.c3p0.PooledDataSource;

public class DelayedApplicationRunner extends ApplicationRunner {
	
	private LoadDataFileNameRepository repository;
	private ApplicationCheckPointCounter checkPoint;

	@Inject
	public DelayedApplicationRunner(final LoadDataFileNameRepository repository, final ApplicationCheckPointCounter checkPoint) {
		this.repository = repository;
		this.checkPoint = checkPoint;
	}

	@Override
	protected void realRun(final ApplicationParameter params) throws Exception {
		logger.info("Starting ...");		
		final String filesPath = params.getPathOfPayload() + SUB_PATH_FILES;
				
		logger.info("Start search LOAD DATA into general query log file");
		final Set<Integer> idsWithLoadData = new SearchLoadDataStatement().run(params.generalQueryLogPath());
		
		logger.info("Start load file name of Connections");
		final List<Integer> connectionFileNames = retriveConnectionFiles(new File(filesPath));
				
		logger.info("Start load delayed data");
		final List<TimerConnectionAggregator>  tcas = retriveDelayedTCA(params.generalQueryLogPath(), filesPath);
				
		if (idsWithLoadData.isEmpty()) {
			System.out.println("Start schedule of " + connectionFileNames.size() + " connection's");
			schedule(params, tcas, filesPath, null, params.getPathOfPayload());											
		} else {			
			logger.info("Start searching of real file name of LOAD DATA: " + idsWithLoadData.size());
			((PropertyLoadDataFileNameRepository)repository).run(idsWithLoadData, params.getPathOfRealFileNames());
			
			for (TimerConnectionAggregator tca : tcas) {				
				for (Integer i : tca.getIds()) {
					if (repository.containsKey(i)) {
						tca.addLoadData(i, repository.get(i));
					}					
				}
			}
			
			logger.info("Start schedule of " + connectionFileNames.size() + " connection's and " + idsWithLoadData.size() + " load data");
			schedule(params, tcas, filesPath, params.loadDataDirPath(), params.getPathOfPayload()); 	
		}
	}
	
	private List<TimerConnectionAggregator> retriveDelayedTCA(String generalQueryLogPath, String filesPath) {
		List<TimerConnectionAggregator>  tcas = null;
		
		try {
			final FileInputStream fstream2 = new FileInputStream(generalQueryLogPath);	
			final BufferedReader br2 = new BufferedReader(new InputStreamReader(fstream2));
			
			tcas = new BuildDelayedTCA(filesPath).run(br2);
			
			br2.close();				
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}		
		
		return tcas;
	}

	private void schedule(
	    ApplicationParameter params, List<TimerConnectionAggregator> data, String filesPath, String loadDatasPath, String generalLogDirPath
    ) {
		checkPoint.totalNumber(data.size());
		
		final Queue<ScheduledFuture<PayloadAsync>> futureData = new ConcurrentLinkedQueue<ScheduledFuture<PayloadAsync>>();						
		final ScheduledExecutorService pool = Executors.newScheduledThreadPool(params.getNumberOfThread());
		final PooledDataSource ds = buildDataSource(params);
		
		addHookForCancelTask(futureData, ds);				
		
		long current = 0;		
		for (TimerConnectionAggregator tca : data) {
			current += tca.getDelay();
			
			futureData.add(
				pool.schedule(
					new MyCallable(tca, filesPath, loadDatasPath, ds, params.isDryRun()), 
					current, 
					TimeUnit.MILLISECONDS
				)
			);
		}			
		
		logger.info("thread pool shutdown");
		pool.shutdown();		
		
		//XXX thread pool for write partial report 						
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		 		
		final PartialResultWriter prw = new PartialResultWriter(futureData, generalLogDirPath, checkPoint);		
		final ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(prw, 60, 60, TimeUnit.SECONDS);
		
		scheduler.schedule(new Runnable() {
			public void run() {
				if (prw.isEnd()) {
					logger.error("shutdown partial result writer !!?");
					sf.cancel(false);
					
					scheduler.shutdown();
				}
			}
		}, 5, TimeUnit.MINUTES);
		
		try {
			logger.info("thread pool awaitTermination");
			pool.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);			
		} catch (InterruptedException e) {
			logger.error(null, e);
		}
	}
	
}
