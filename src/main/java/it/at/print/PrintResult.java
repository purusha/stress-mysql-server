package it.at.print;

import it.at.runner.ApplicationRunner;
import it.at.script.ApplicationCheckPointCounter;
import it.at.script.PayloadAsync;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class PrintResult {
	
	private static final String odd = "<tr style='background-color:#FFF;'>";
	private static final String even = "<tr style='background-color:#CCC;'>";
	
	public void print(List<PayloadAsync> scheduleResult, String pathOfResult, ApplicationCheckPointCounter checkPoint) {		
		final StringBuilder sb = new StringBuilder();	
		
		sb.append("<!DOCTYPE html><html><body>");		
		sb.append("<br /><hr /><br /><h1>" + checkPoint.build() + "</h1><br /><hr /><br />");		
		sb.append("<table style=\"width:100%;border-spacing:10px;\">");
		
		sb.append("<tr>");
		sb.append("<th>Id</th>");
		sb.append("<th># Connection</th>");
		sb.append("<th># Statement</th>");
		sb.append("<th>Max Statement (ms)</th>");
		sb.append("<th>Min Statement (ms)</th>");
		sb.append("<th>Average Statement (ms)</th>");
		sb.append("<th>Connection Id's</th>");
		sb.append("</tr>\n");

		int totalConnectionNumber = 0;
		long totalStatementNumber = 0;
		int i = 1;
		
		for (PayloadAsync payloadAsync : scheduleResult) {
			sb.append(openTr(i));
			sb.append("<td>" + i + "</td>");
			sb.append("<td>" + payloadAsync.getNumberOfConnection() + "</td>");
			sb.append("<td>" + payloadAsync.getNumberOfStatement() + "</td>");			
			sb.append("<td>" + payloadAsync.getMaxStatementExecution() + "</td>");
			sb.append("<td>" + payloadAsync.getMinStatementExecution() + "</td>");
			sb.append("<td>" + payloadAsync.getAverageStatementExecution() + "</td>");			 
			sb.append("<td>" + buildLink(payloadAsync.getAllConnectionsId()) + "</td>");
			sb.append("</tr>\n");

			//for total row
			totalConnectionNumber += payloadAsync.getNumberOfConnection();
			totalStatementNumber += payloadAsync.getNumberOfStatement();			
			
			i++;				
		}
		
		sb.append(openTr(i));
		sb.append("<td>" + "TOT. </td>");
		sb.append("<td>" + totalConnectionNumber + "</td>");
		sb.append("<td>" + totalStatementNumber + "</td>");			
		sb.append("<td>" + "</td>");
		sb.append("<td>" + "</td>");
		sb.append("<td>" + "</td>");
		sb.append("<td>" + "</td>");
		sb.append("</tr>\n");					
				
		sb.append("</table></body></html>");
		
		writeToFile(pathOfResult + "/" + System.currentTimeMillis() + ".html", sb);
	}

	private String buildLink(Set<Integer> c) {
		final StringBuilder sb = new StringBuilder();
		
		for (Integer id : c) {
			sb.append("<a href=\"." + ApplicationRunner.SUB_PATH_FILES + id + "\" target=\"_blank\">" + id + "</a> ");
		}
		
		return sb.toString();
	}

	private String openTr(int index) {
		return index % 2 == 0 ? even : odd; 
	}
	
	private void writeToFile(String pFilename, StringBuilder pData) {
		try {
			final BufferedWriter out = new BufferedWriter(new FileWriter(pFilename));
			
	        out.write(pData.toString());
	        out.flush();
	        out.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}        
    }	
}
