package it.at.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class PayloadAsync {
	private HashMap<Integer, List<Long>> data;
	private Set<Integer> dataKeys;
	
	private Long min = Long.MAX_VALUE;
	private Long max = Long.MIN_VALUE;
	private Long numberOfStatement = 0L;
	private Long total = 0L;	

	public PayloadAsync() {
		this.data = new HashMap<Integer, List<Long>>();
		this.dataKeys = new HashSet<Integer>();
	}

	public void addExecutionTime(Integer id, long elapsed) {		
		final List<Long> timing;
		
		if (data.containsKey(id)) {
			timing = data.get(id);
		} else {			
			timing = new ArrayList<Long>();
			data.put(id, timing);
		}
		
		timing.add(elapsed);
	}

	public Long getNumberOfStatement() {
		return numberOfStatement;
	}
	
	public Integer getNumberOfConnection() {
		return dataKeys.size();
	}

	public Long getMinStatementExecution() {
		return min;
	}

	public Long getMaxStatementExecution() {
		return max;
	}

	public float getAverageStatementExecution() {		
		return (total / (float)numberOfStatement);
	}

	public void buildStatisticalInfo() {
		for (Entry<Integer, List<Long>> entry : data.entrySet()) {		
			dataKeys.add(entry.getKey());
			
			final List<Long> value = entry.getValue();			
			final Long m = Collections.min(value);
			final Long M = Collections.max(value);
			
			if (m < min) {
				min = m;
			}
			
			if (M > max) {
				max = M;
			}

			numberOfStatement += value.size();
			
			for (Long v : value) {
				total += v;
			}			
		}		
		
		data = null; //Avoiding java.lang.OutOfMemoryError: Java heap space
	}

	public Set<Integer> getAllConnectionsId() {
		return dataKeys;
	}
}
