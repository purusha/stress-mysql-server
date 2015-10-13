package it.at.os;

import it.at.script.ParsingUtil;
import it.at.script.StatementType;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class ConnectionScanner extends ParsingUtil {

	private String filePath;

	public ConnectionScanner(String filePath) {
		this.filePath = filePath;
	}

	public List<String> buildStatemens() throws Exception {
		final List<String> result = Lists.newArrayList();		
		final FileInputStream fstream = new FileInputStream(filePath);			
		final BufferedReader br = new BufferedReader(new InputStreamReader(fstream));		
		
		String strLine;
		while ((strLine = br.readLine()) != null) {
			final Pair<StatementType, String> statementData = getStatementData(strLine);
			
			if (statementData != null && StringUtils.isNoneBlank(statementData.getRight())) {				
				if (statementData.getLeft() == StatementType.INIT) {
					result.add(statementData.getRight());	
				} else {
					final String concat = Iterables.getLast(result).concat(" " + statementData.getRight());
					
					result.remove(result.size() - 1);					
					result.add(concat);
				}
			}
		}	
		
		//fstream.close(); //serve ?
		br.close();	
		return result;
	}

}
