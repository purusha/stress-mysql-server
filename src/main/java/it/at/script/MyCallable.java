package it.at.script;

import it.at.os.ConnectionScanner;
import it.at.os.LoadDataFileNameRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;

public class MyCallable implements Callable<PayloadAsync> {
	private static final Logger logger = LoggerFactory.getLogger(MyCallable.class);
	
	private final TimerConnectionAggregator tca;
	private final String filesPath;
	private final String loadDatasPath;
	private final DataSource ds;
	private final boolean dryRun;

	public MyCallable(TimerConnectionAggregator tca, String filesPath, String loadDatasPath, DataSource ds, boolean dryRun) {
		this.tca = tca;
		this.filesPath = filesPath;
		this.loadDatasPath = loadDatasPath;
		this.ds = ds;
		this.dryRun = dryRun;
	}

	@Override
	public PayloadAsync call() {
		final PayloadAsync result = new PayloadAsync();		
		
		for (Integer id : tca.getIds()) {
			Connection connection = null;
			try {				
				connection = dryRun ? null : ds.getConnection();	
				executeAllStatementOf(id, connection, result);
			} catch (Throwable e) {
				logger.error(id + " >> " + e.getMessage());
			} finally {
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
					}						
				}
			}
		}
		
		result.buildStatisticalInfo();
		
		return result;
	}

	private void executeAllStatementOf(final Integer id, final Connection connection, final PayloadAsync result) throws Exception {		
		for (String sql : new ConnectionScanner(filesPath + id).buildStatemens()) {
			String sqlStatement = null;
			
			if (StringUtils.contains(sql, LoadDataFileNameRepository.START_LOADDATA_STATEMENT)) {
				final String[] between = StringUtils.substringsBetween(
					sql, LoadDataFileNameRepository.START_LOADDATA_STATEMENT, LoadDataFileNameRepository.END_LOADDATA_STATEMENT
				);
				
				if (between.length != 1) {
					logger.error("Can't execute LOAD DATA with pattern: " + Arrays.toString(between));
				} else {											
					final Optional<String> loadDataFileName = tca.getLoadData(id);
					
					if (loadDataFileName.isPresent()) {
						sqlStatement = StringUtils.replace(sql, between[0], loadDatasPath + loadDataFileName.get());
					} else {
						logger.error("For " + sql + " can't find load data file name");
					}
				}
			} else {
				sqlStatement = sql;
			}
			
			if (StringUtils.isNotBlank(sqlStatement)) {
				try {
					result.addExecutionTime(id, realExecute(sqlStatement, connection));
				} catch (SQLException e) {
					break;
				}
			}
		}
	}
	
	private long realExecute(String sql, final Connection connection) throws SQLException {
		final Stopwatch sw = Stopwatch.createStarted();
				
		if (dryRun) {
		    logger.info("stmt: {}", sql);
		} else {
		    executeStatement(sql, connection);		    
		}
		
		return sw.elapsed(TimeUnit.MILLISECONDS);		
	}

	private void executeStatement(String sql, final Connection connection) throws SQLException {
		Statement stmt = null;
					
		try {
			if (StringUtils.startsWith(sql, ParsingUtil.USE)) {
				connection.setCatalog(StringUtils.split(sql, " ")[1]); 
			} else {
				stmt = connection.createStatement();
				stmt.execute(sql);
			}						
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}
}
