package it.at.os;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Sets;

public class SearchLoadDataStatement {

	public Set<Integer> run(String generalLogInput) throws Exception {
		final Set<Integer> result = Sets.newHashSet();
		
		final String[] cmd = {"/bin/sh", "-c", "grep 'LOAD DATA' " + generalLogInput};
		
		final Process p = Runtime.getRuntime().exec(cmd);
	    p.waitFor();
	 
	    final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	 
	    String line = "";			
	    while ((line = reader.readLine())!= null) {	    	
	    	result.add(Integer.valueOf(StringUtils.strip(line).split(" ")[0]));
	    }		
	    
	    return result;
	}	

//	public static void main(String[] args) throws Exception {
//		new SearchLoadDataStatement().run("/home/alan/Desktop/db1m-staging.log");
//	}
	
}