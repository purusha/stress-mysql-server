package it.at.runner;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import it.at.script.PayloadAsync;

import org.junit.Test;

public class PayloadAsyncTest {

	private static final int SECOND_CONNECTION_ID = 100;
	private static final int FIRST_CONNECTION_ID = 99;

	@Test
	public void singleConnection() {		
		final PayloadAsync pa = new PayloadAsync();
		
		pa.addExecutionTime(FIRST_CONNECTION_ID, 1l);
		pa.addExecutionTime(FIRST_CONNECTION_ID, 2l);
		pa.addExecutionTime(FIRST_CONNECTION_ID, 3l);
		
		//run
		pa.buildStatisticalInfo();
		
		//assert		
		assertThat(pa.getNumberOfStatement(), is(3l));
		assertThat(pa.getNumberOfConnection(), is(1));
		
		assertThat(pa.getMinStatementExecution(), is(1l));
		assertThat(pa.getMaxStatementExecution(), is(3l));
		assertThat(pa.getAverageStatementExecution(), is(2f));
		assertThat(pa.getAllConnectionsId(), hasItems(FIRST_CONNECTION_ID));
	}

	@Test
	public void doubleConnection() {		
		final PayloadAsync pa = new PayloadAsync();
		
		pa.addExecutionTime(FIRST_CONNECTION_ID, 1l);
		pa.addExecutionTime(FIRST_CONNECTION_ID, 2l);
		pa.addExecutionTime(FIRST_CONNECTION_ID, 3l);
		pa.addExecutionTime(SECOND_CONNECTION_ID, 41l);
		pa.addExecutionTime(SECOND_CONNECTION_ID, 42l);
		pa.addExecutionTime(SECOND_CONNECTION_ID, 43l);
		
		//run
		pa.buildStatisticalInfo();
		
		//assert		
		assertThat(pa.getNumberOfStatement(), is(6l));
		assertThat(pa.getNumberOfConnection(), is(2));
		
		assertThat(pa.getMinStatementExecution(), is(1l));
		assertThat(pa.getMaxStatementExecution(), is(43l));
		assertThat(pa.getAverageStatementExecution(), is(22f));
		assertThat(pa.getAllConnectionsId(), hasItems(FIRST_CONNECTION_ID, SECOND_CONNECTION_ID));
	}
}
