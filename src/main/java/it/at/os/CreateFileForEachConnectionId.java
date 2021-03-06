package it.at.os;

import it.at.script.ParsingUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.google.common.collect.Sets;

public class CreateFileForEachConnectionId extends ParsingUtil {
	
	public void run(BufferedReader br, String parentDir) throws IOException {		
		final Set<Integer> connectionIds = Sets.newHashSet();
		
		String strLine;
		while ((strLine = br.readLine()) != null) {
			if (isUsefulLine(strLine)) {
				final Integer connectionId = retriveConnectionId(strLine);
				
				if (connectionId != null) {
					connectionIds.add(connectionId);							
				}
			}
		}
		
		for (Integer id : connectionIds) {
			final File file = new File(parentDir + id);
			
			file.createNewFile();
		}
	}
	
}
