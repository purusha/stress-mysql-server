package it.at.script;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class ParsingUtil {
	
	private static final String INIT_DB = " Init DB";
	private static final String QUIT = " Quit";
	public static final String QUERY = " Query";
	protected static final String CONNECT = " Connect";
	public static final String USE = "USE ";

	private static Pattern p = Pattern.compile("[0-9]+");
	private static Pattern n = Pattern.compile("(?:2[0-3]|[01]?[0-9]):[0-5][0-9]:[0-5][0-9]");
	
	protected Integer retriveConnectionId(String strLine) {
		Integer result = null;
		
		if (StringUtils.startsWith(strLine, "\t")) {
			final Matcher idMatcher = p.matcher(strLine);
			
			if (idMatcher.find()) {
				result = Integer.parseInt(idMatcher.group());	
			}
		} else {
			final String[] split = StringUtils.split(strLine, "\t");
			
			if (split.length > 1) {
				final Matcher idMatcher = p.matcher(split[1]);
				
				if (idMatcher.find()) {
					result = Integer.parseInt(idMatcher.group());	
				}					
			}									
		}			
				
		return result;
	}
	
	protected Long retriveDelay(String strLine) {
		Long result = null;
		
		final Matcher matcher = n.matcher(strLine);		
		if (matcher.find()) {
			final String[] split = matcher.group().split(":");
			
			final Calendar c = Calendar.getInstance();
			
			c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0]));
			c.set(Calendar.MINUTE, Integer.parseInt(split[1]));
			c.set(Calendar.SECOND, Integer.parseInt(split[2]));
			c.set(Calendar.MILLISECOND, 0);
			
			result = Long.valueOf(c.getTimeInMillis() % 86400000L); //(24L * 60 * 60 * 1000)						
		}
				
		return result;
	}
	
	protected boolean contains(String strLine, String match) {
		return StringUtils.contains(strLine, match);
	}

	protected boolean isUsefulLine(String strLine) {
		//" Init DB" ... li posso saltare
		
		return contains(strLine, QUERY) || contains(strLine, CONNECT) || contains(strLine, QUIT);
	}
	
	protected Pair<StatementType, String> getStatementData(String strLine) {
		if (isUsefulLine(strLine)) {			
			if (contains(strLine, QUERY)) {
				return Pair.of(StatementType.INIT, StringUtils.trim(StringUtils.substringAfter(strLine, QUERY)));
				
			} else if (contains(strLine, CONNECT)) {
				final String trim = StringUtils.trim(StringUtils.substringAfter(strLine, CONNECT));
				
//				String a = "C3000116@grndrt.at.test on C3000116";
//				String b = "C3000111@frntend.at.test on ";
//				String c = "u_james@activemq-tw.at.test on ";
//				String d = "u_systest@172.17.2.13 on C3000111";

				if (StringUtils.contains(trim, "@")) {
					return Pair.of(StatementType.INIT, USE + StringUtils.split(trim, "@")[0]); //mysql> USE [SCHEMA] command !!?
				} else {
					throw new IllegalArgumentException("Can't establish connection on DB with [" + trim + "]");
				}				
			} else {
				return null;
			}
		} else {		
			return Pair.of(StatementType.CONTINUE, StringUtils.trim(strLine));
		}
	}

	protected boolean isUselessLine(String strLine) {
		return contains(strLine, INIT_DB);		
	}
}