package it.at.os;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.at.os.SearchLoadDataStatement.StreamRender;


public class GrepLoadDataFileNameRepository implements LoadDataFileNameRepository {
	
	private final HashMap<Integer, String[]> result;
	
	public GrepLoadDataFileNameRepository() {
		result = Maps.newHashMap();
	}

	public void run(Set<Integer> intersection, String binaryLogDirPath) throws Exception {
	    final ExecutorCompletionService<Pair<Integer, List<String>>> service = new ExecutorCompletionService<>(Executors.newFixedThreadPool(5)); //XXX must be an application params ??    
	    
	    intersection.forEach(id -> {
	        service.submit(() -> {
	            	            
	            final List<String> param = Lists.<String>newArrayList();
	            param.add("/bin/sh");
	            param.add("-c");
	            param.add("grep -r -A5 \"Execute_load_query[[:space:]]thread_id=" + id + "\" " + binaryLogDirPath);
	            
	            final ProcessBuilder processBuilder = new ProcessBuilder();     
	            processBuilder.command(param);      
	            
	            final Process p = processBuilder.start();
	            
	            final StreamRender outputStderr = new StreamRender(p.getErrorStream(), StreamRender.STDERR);
	            outputStderr.start();                   
	            
	            final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));            
	            final List<String> fileNames = Lists.newArrayList();
	            
	            String line = "";           
	            while ((line = reader.readLine())!= null) {
	                System.err.println(line);
	                
	                final String s = StringUtils.strip(line);
	                final String[] between = StringUtils.substringsBetween(s, START_LOADDATA_STATEMENT, END_LOADDATA_STATEMENT);                                
	                
	                if (between != null) {
	                    fileNames.add(new File(StringUtils.substringBetween(between[0], "'")).getName());
	                }               
	            }    
	            
	            int waitFor = p.waitFor();
	            System.err.println("process ends with " + waitFor);             	            
	            
	            return Pair.of(id, fileNames);	  
	            
	        });
	    });
	    
	    IntStream.range(0, intersection.size())
	        .forEach(i -> {
	            
	            try {
                    final Pair<Integer, List<String>> d = service.take().get();
                    
                    result.put(d.getLeft(), d.getRight().toArray(new String[]{}));                    
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println(e);
                }
	            
	        });	    	    
	    
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
	
//	public static void main(String[] args) throws Exception {
//	    final GrepLoadDataFileNameRepository repository = new GrepLoadDataFileNameRepository();
//	    
//	    repository.run(Sets.newHashSet(375199324), "/home/developer/Desktop/mysql-stress-tool/test1" + ApplicationParameterHandler.BYNARY_LOG_DIR);
//	    
//	    for (Entry<Integer, String[]> entry : repository.getResult().entrySet()) {
//	        System.err.println(entry);
//	    }
//	}
}
