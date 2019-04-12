package it.at.runner;

import it.at.os.LoadDataFileNameRepository;
import it.at.os.PropertyLoadDataFileNameRepository;
import it.at.os.SearchLoadDataStatement;
import it.at.print.PartialResultWriter;
import it.at.script.ApplicationCheckPointCounter;
import it.at.script.ApplicationParameter;
import it.at.script.MyCallable;
import it.at.script.PayloadAsync;
import it.at.script.TimerConnectionAggregator;

import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.mchange.v2.c3p0.PooledDataSource;

public class BruteForceApplicationRunner extends ApplicationRunner {	
	private LoadDataFileNameRepository repository;
	private ApplicationCheckPointCounter checkPoint;

	@Inject
	public BruteForceApplicationRunner(final LoadDataFileNameRepository repository, final ApplicationCheckPointCounter checkPoint) {
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
		
		if (idsWithLoadData.isEmpty()) {
			logger.info("Start schedule of " + connectionFileNames.size() + " connection's");
			
			final List<TimerConnectionAggregator>  tcas = Lists.newArrayList();			
			for (Integer i : connectionFileNames) {
				tcas.add(new TimerConnectionAggregator(NumberUtils.LONG_ZERO, Sets.newHashSet(i)));
			}
			
			schedule(params, tcas, filesPath, null, params.getPathOfPayload());											
		} else {
			logger.info("Start searching of real file name of LOAD DATA: " + idsWithLoadData.size());
			((PropertyLoadDataFileNameRepository)repository).run(idsWithLoadData, params.getPathOfRealFileNames());
			
			final List<TimerConnectionAggregator>  tcas = Lists.newArrayList();			
			for (Integer i : connectionFileNames) {
				final TimerConnectionAggregator tca = new TimerConnectionAggregator(NumberUtils.LONG_ZERO, Sets.newHashSet(i));
				
				if (repository.containsKey(i)) {
					tca.addLoadData(i, repository.get(i));
				}
				
				tcas.add(tca);
			}
			
			logger.info("Start schedule of " + connectionFileNames.size() + " connection's and " + idsWithLoadData.size() + " load data");
			schedule(params, tcas, filesPath, params.loadDataDirPath(), params.getPathOfPayload()); 	
		}
	}
	
	private void schedule(
	    ApplicationParameter params, List<TimerConnectionAggregator> data, String filesPath, String loadDatasPath, String generalLogDirPath
    ) {
		checkPoint.totalNumber(data.size());
		
		final Queue<Future<PayloadAsync>> futureData = new ConcurrentLinkedQueue<Future<PayloadAsync>>(); //new LinkedList 		
		final ExecutorService pool = Executors.newFixedThreadPool(params.getNumberOfThread());		
		final PooledDataSource ds = buildDataSource(params);
		
		addHookForCancelTask(futureData, ds);
		
		for (TimerConnectionAggregator tca : data) {
			futureData.add(
				pool.submit(
					new MyCallable(tca, filesPath, loadDatasPath, ds, params.isDryRun()) 
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
