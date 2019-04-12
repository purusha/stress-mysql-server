package it.at.script;

import static org.junit.Assert.*;
import it.at.script.MyCallable;
import it.at.script.ParsingUtil;
import it.at.script.PayloadAsync;
import it.at.script.TimerConnectionAggregator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.*;

import com.google.common.collect.Sets;
import com.google.common.io.Files;

@RunWith(MockitoJUnitRunner.class)
public class MyCallableTest {
	private static final Integer id = 42;	
	
	private File root;
	private String rootDir;
	
	private TimerConnectionAggregator tca;
	private DataSource ds;	
	
	@Before
	public void setUp() throws Exception {		
		root = Files.createTempDir();
		
		final File connectionFile = new File(root, String.valueOf(id));		
		connectionFile.createNewFile();
		
		Files.write(buildQuery("q1", "q2", "q3"), connectionFile, Charset.defaultCharset());			
		
		rootDir = root.getAbsolutePath() + "/";		
		tca = new TimerConnectionAggregator(NumberUtils.LONG_ZERO, Sets.newHashSet(id));
		ds = Mockito.mock(DataSource.class);		
	}

	@After
	public void tearDown() throws Exception {		
		delete(root);
	}

	@Test
	public void simpleUsage() throws Exception {				
		//init
		final Connection connection = Mockito.mock(Connection.class);
		Mockito.when(ds.getConnection()).thenReturn(connection);
		
		final Statement statement1 = Mockito.mock(Statement.class);
		final Statement statement2 = Mockito.mock(Statement.class);
		final Statement statement3 = Mockito.mock(Statement.class);
		
		Mockito.when(connection.createStatement()).thenReturn(statement1, statement2, statement3);
		
		//run
		final PayloadAsync call = new MyCallable(tca, rootDir, null, ds, false).call();
		
		//verify
		assertThat(call, is(notNullValue()));		
		assertThat(call.getAllConnectionsId(), hasItem(id));		
		assertThat(call.getNumberOfConnection(), is(1));		
		assertThat(call.getNumberOfStatement(), is(3l));
		
		Mockito.verify(ds).getConnection();
		
		Mockito.verify(connection, Mockito.times(3)).createStatement();
		Mockito.verify(connection, Mockito.times(1)).close();
		
		Mockito.verify(statement1).execute("q1");
		Mockito.verify(statement1).close();
		Mockito.verify(statement2).execute("q2");
		Mockito.verify(statement2).close();
		Mockito.verify(statement3).execute("q3");
		Mockito.verify(statement3).close();
		
		Mockito.verifyNoMoreInteractions(ds, connection, statement1, statement2, statement3);
	}
	
	@Test
	public void usingTwiceId() {
		
	}
	
	/*
	 * no test method under
	 */
	
	private String buildQuery(String ... sql) {
		final StringBuffer sb = new StringBuffer();
		
		for (String s : sql) {
			sb.append(ParsingUtil.QUERY +  " " + s + "\n");
		}
		
		return sb.toString(); 
	}
	
	private void delete(File file) throws IOException {
		if (file.exists() && file.isDirectory()) {
			// directory is empty, then delete it
			if (file.list().length == 0) {
				file.delete();
			} else {
				// list all the directory contents
				final String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					final File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
				}
			}
		} else {
			// if file, then delete it
			file.delete();
		}
	}	

}
