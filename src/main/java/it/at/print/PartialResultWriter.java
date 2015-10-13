package it.at.print;

import it.at.script.ApplicationCheckPointCounter;
import it.at.script.PayloadAsync;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PartialResultWriter implements Runnable {	
	private static final String NO_ELEMENTS_WILL_BE_EXPORTED = ">> no elements will be exported";
	private static final String NO_ELEMENTS_AVAILABLE = ">> no elements available";
	private static final String START_WRITE_PARTIAL_RESULT = "INIT: write partial result";
	private static final String STOP_WRITE_PARTIAL_RESULT = "END: write partial result";
	
	private static final int NUMBER_OF_ITEMS = 15000;
	private static final Logger logger = LoggerFactory.getLogger(PartialResultWriter.class);

	private Queue<? extends Future<PayloadAsync>> futureData;
	private String pathWhereToWriteFile;
	private boolean end;
	private ApplicationCheckPointCounter checkPoint;

	public PartialResultWriter(Queue<? extends Future<PayloadAsync>> futureData, String pathWhereToWriteFile, ApplicationCheckPointCounter checkPoint) {
		this.futureData = futureData;
		this.pathWhereToWriteFile = pathWhereToWriteFile;
		this.checkPoint = checkPoint;
		this.end = false;
	}
	
	@Override
	public void run() {
		logger.info(START_WRITE_PARTIAL_RESULT);		
		
		if (futureData.isEmpty()) {
			logger.info(NO_ELEMENTS_AVAILABLE);
			end = true;
		} else {
			writeResult();
		}
				
		logger.info(STOP_WRITE_PARTIAL_RESULT);
	}
	
	private void writeResult() {
		final List<PayloadAsync> partialResult = new ArrayList<PayloadAsync>();
		
		try {
			int badElement = 0;
			for(Iterator<? extends Future<PayloadAsync>> iter = futureData.iterator(); iter.hasNext() && partialResult.size() < NUMBER_OF_ITEMS && badElement < 100;) {
				final Future<PayloadAsync> next = iter.next();
				
				if (next.isDone()) {
					badElement = 0;
					partialResult.add(next.get());
					iter.remove();
				} else {
					badElement += 1;
				}
			}						
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		if (partialResult.isEmpty()) {
			logger.info(NO_ELEMENTS_WILL_BE_EXPORTED);
		} else {
			checkPoint.partialNumber(partialResult.size());
			new PrintResult().print(partialResult, pathWhereToWriteFile, checkPoint);			
		}
	}

	public boolean isEnd() {
		return end;
	}

}
