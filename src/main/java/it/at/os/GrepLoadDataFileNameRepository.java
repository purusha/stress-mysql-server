package it.at.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public class GrepLoadDataFileNameRepository implements LoadDataFileNameRepository {
	
	private final HashMap<Integer, String[]> result;
	
	public GrepLoadDataFileNameRepository() {
		result = Maps.newHashMap();
	}

	public void run(Set<Integer> intersection, String binaryLogDirPath) throws Exception {
		for (Integer id : intersection) {
			final String[] cmd = {"/bin/sh", "-c", "grep -r -A5 'Execute_load_query[[:space:]]thread_id=" + id + "' " + binaryLogDirPath};		
			
			final Process p = Runtime.getRuntime().exec(cmd);
		    p.waitFor();
		 
		    final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));		    
		    final List<String> fileNames = Lists.newArrayList();
		    
		    String line = "";			
		    while ((line = reader.readLine())!= null) {
		    	final String s = StringUtils.strip(line);
		    	final String[] between = StringUtils.substringsBetween(s, START_LOADDATA_STATEMENT, END_LOADDATA_STATEMENT);		    			    	
		    	
		    	if (between != null) {
		    		fileNames.add(new File(StringUtils.substringBetween(between[0], "'")).getName());
		    	}		    	
		    }		
		    
		    if (!fileNames.isEmpty()) {
		    	result.put(id, fileNames.toArray(new String[]{}));	
		    }
		}		
	}	

	@Override
	public boolean containsKey(Integer i) {
		return result.containsKey(i);
	}

	@Override
	public String[] get(Integer i) {
		return result.get(i);
	}

	public HashMap<Integer, String[]> getResult() {
		return result;
	}
}
