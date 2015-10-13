package it.at.script;

import it.at.os.GrepLoadDataFileNameRepository;
import it.at.os.SearchLoadDataStatement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class LocalSearchLoadDataFileName {

	public static void main(String[] args) throws Exception {
		
		final Properties p = new Properties();
		
		System.err.println("Start search LOAD DATA into general query log file");
		final Set<Integer> idsWithLoadData = new SearchLoadDataStatement().run("/media/alan/DATA/6" + ApplicationParameterHandler.GENERAL_QUERY_LOG_FILE);		
		
		final GrepLoadDataFileNameRepository repository = new GrepLoadDataFileNameRepository();		
		repository.run(idsWithLoadData, "/media/alan/DATA/6" + ApplicationParameterHandler.BYNARY_LOG_DIR);		

		for (Entry<Integer, String[]> entry : repository.getResult().entrySet()) {			
			p.put(String.valueOf(entry.getKey()), Arrays.toString(entry.getValue()));
		}

		final File f = new File("/media/alan/DATA/result.properties");
		final OutputStream out = new FileOutputStream(f);
		p.store(out, "");
		
	}

}