package it.at.os;

import it.at.script.TimerConnectionAggregator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class SplitGeneralQueryLogFile {	
	public List<TimerConnectionAggregator> run(final String generalLogQueryFile, String parentDir) throws IOException {
		final List<TimerConnectionAggregator> data;
		
		{
			System.err.println("Start of dir creation");
			final FileInputStream fstream = new FileInputStream(generalLogQueryFile);			
			final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));		
			
			new CreateFileForEachConnectionId().run(br, parentDir);			
			br.close();
		}
		
		{
			System.err.println("Start of file filler");
			final FileInputStream fstream2 = new FileInputStream(generalLogQueryFile);	
			final BufferedReader br2 = new BufferedReader(new InputStreamReader(fstream2));
			
			data = new WriteEachLine(parentDir).run(br2);
			br2.close();					
		}
		
//		for (TimerConnectionAggregator timerConnectionAggregator : data) {
//			System.err.println(timerConnectionAggregator.toString());
//		}				
		
		return data;
	}
}
