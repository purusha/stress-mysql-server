package it.at.os;

import it.at.script.ParsingUtil;
import it.at.script.TimerConnectionAggregator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class WriteEachLine extends ParsingUtil {
		
	private int previousConnectionId = -1;
	private Long currentDelay = -1L;
	private Set<Integer> currentIds = null;
	private String parentDir = null;	
	
	public WriteEachLine(String parentDir) {
		this.parentDir = parentDir;		
	}
	
	public List<TimerConnectionAggregator> run(BufferedReader br) throws IOException {
		final List<TimerConnectionAggregator> data = Lists.newArrayList();
		
		String strLine;
		while ((strLine = br.readLine()) != null) {
			if (isUsefulLine(strLine)) {
				final Integer connectionId = retriveConnectionId(strLine);
				
				if (connectionId != null) {
					previousConnectionId = connectionId;
				}				
				
				appendToConnectionFile(strLine, previousConnectionId);
				
				final Long delay = retriveDelay(strLine);
				
				if (delay != null) {						
					if (currentDelay != -1 && !currentIds.isEmpty()) {
						data.add(new TimerConnectionAggregator(currentDelay, currentIds));
					}
					
					currentDelay = delay;
					currentIds = Sets.newHashSet(previousConnectionId);	
				} else {
					currentIds.add(previousConnectionId);
				}
			} else if (isUselessLine(strLine)) {
				/* DO NOTHING */
			} else {
				/* QUERY CHE VANNO A CAPO NEL FILE !!? */				
				appendToConnectionFile(strLine, previousConnectionId); 
			}
		}
		
		//altrimenti mi perdo l'ultimo connectionId con le sue query
		data.add(new TimerConnectionAggregator(currentDelay, currentIds));

		//riscrivo i delay per ogni singolo elemento
		for (int i = data.size() - 1; i > 0; i--) {
			data.get(i).setDelay(
				data.get(i).getDelay() - data.get(i - 1).getDelay()
			);
		}

		/*
		 	potrebbe capitare che due TimerConnectionAggregator contigui
		 	abbiano in comune dei connectionId
		 	
		 	questo perchè lo scopo di questa classe è quello di scrivere 
		 	lo statament corretto nel file corretto
		 	
		 	ma visto che produce anche la lista degli oggetti da eseguire
		 	ed anche con che delay ... 
		 	
		 	ed il delay potrebbe capitare anche a metà di un file ...
		 			 	
		*/
		for (int i = data.size() - 1; i > 0; i--) {
			final Set<Integer> after = data.get(i).getIds();
			final Set<Integer> before = data.get(i - 1).getIds();
			
			final SetView<Integer> intersection = Sets.intersection(before, after);		
			
			if (!intersection.isEmpty()) {
				for (Iterator<Integer> iterator = before.iterator(); iterator.hasNext(); ) {
					if (intersection.contains(iterator.next())) {
						iterator.remove();
					}
				}
			}
		}
		
		//il primo delay parte da 0
		data.get(0).setDelay(0L);
		
		return data;
	}

	protected void appendToConnectionFile(String strLine, final Integer connectionId) throws IOException {
		final BufferedWriter writer = new BufferedWriter(new FileWriter(parentDir + connectionId, true));
		
		writer.append(strLine + "\n");		
		writer.close();
	}
}
