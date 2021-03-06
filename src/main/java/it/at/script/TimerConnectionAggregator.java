package it.at.script;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TimerConnectionAggregator {	
	private Long delay;
	private Set<Integer> ids;
	private HashMap<Integer, Queue<String>> idsWithLoadData;

	public TimerConnectionAggregator(Long delay, Set<Integer> ids) {
		this.delay = delay;
		this.ids = ids;
		this.idsWithLoadData = Maps.newHashMap();
	}
	
	public Long getDelay() {
		return delay;
	}
	
	public void setDelay(Long delay) {
		this.delay = delay;
	}
	
	public Set<Integer> getIds() {
		return ids;
	}	
	
	public void addLoadData(int id, String[] fileNames) {
		if (ids.contains(id)) {
			final Queue<String> queue = new LinkedList<String>();			
			queue.addAll(Lists.newArrayList(fileNames));
			
			idsWithLoadData.put(id, queue);
		} else {
			throw new IllegalArgumentException("can't add real filename of load data for id: " + id);
		}		
	}
	
	public Optional<String> getLoadData(int id) {
		if (ids.contains(id)) {
			final Queue<String> queue = idsWithLoadData.get(id);
			
			if (queue != null) {
				return Optional.of(queue.remove());
			} else {
				return Optional.absent();
			}
		} else {
			throw new IllegalArgumentException("can't get real filename of load data for id: " + id);
		}		
	}
	
	@Override
	public String toString() {
		return String.valueOf(delay) + "=" + Arrays.toString(ids.toArray());
	}	
}
