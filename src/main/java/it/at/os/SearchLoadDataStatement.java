package it.at.os;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SearchLoadDataStatement {

	public Set<Integer> run(String generalLogInput) throws Exception {
	    final List<String> param = Lists.<String>newArrayList();
	    param.add("/bin/sh");
	    param.add("-c");
	    param.add("grep \"LOAD DATA\" " + generalLogInput);
		
	    final ProcessBuilder processBuilder = new ProcessBuilder();	    
	    processBuilder.command(param);	    
	    
	    final Process p = processBuilder.start();
	    
        final StreamRender outputStderr = new StreamRender(p.getErrorStream(), StreamRender.STDERR);
        outputStderr.start();	    
	    
	    final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	 
	    final Set<Integer> result = Sets.newHashSet();
	    
	    String line = "";			
	    while ((line = reader.readLine())!= null) {	    	
	    	result.add(Integer.valueOf(StringUtils.strip(line).split(" ")[0]));
	    }		
	    
        int waitFor = p.waitFor();
        System.err.println("process ends with " + waitFor);	    
	    
	    return result;
	}	

//	public static void main(String[] args) throws Exception {
//		Set<Integer> run = new SearchLoadDataStatement().run("/home/developer/Desktop/mysql-stress-tool/db18master.log");
//		
//		System.err.println(run);
//	}
	
	public static class StreamRender extends Thread {
//        public static final String STDOUT = "STDOUT";
        public static final String STDERR = "STDERR";

        private InputStream inputStream;
        private String inputType;

        public StreamRender(InputStream inputStream, String inputType) {
                this.inputStream = inputStream;
                this.inputType = inputType;
        }

        public void run() {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(inputStreamReader);
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(this.inputType + " > " + line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
	}
	
}