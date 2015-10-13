package it.at.script;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import it.at.os.SplitGeneralQueryLogFile;
import it.at.runner.ApplicationRunner;
import it.at.script.TimerConnectionAggregator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class SplitGeneralQueryLogFileTest {

	private String realPathOfResource;
	private String parent;
	
	@Before
	public void setUp() throws Exception {
		realPathOfResource = ConnectionScannerTest.getRealPathOfResource("a");
		parent = getParentOfResource("a") + ApplicationRunner.SUB_PATH_FILES;			
		new File(parent).mkdir();
	}

	@After
	public void tearDown() throws Exception {
		delete(new File(parent));
	}
	
	@Test
	public void test() throws Exception {		
		final List<TimerConnectionAggregator> run = new SplitGeneralQueryLogFile().run(realPathOfResource, parent);
		assertThat(run.size(), CoreMatchers.is(2));
		
		TimerConnectionAggregator a = run.get(0);		
		System.err.println(a);
		
		assertThat(a.getDelay(), CoreMatchers.is(0L));
		assertThat(a.getIds().size(), CoreMatchers.is(9));
		
		for (Integer id : Lists.newArrayList(51, 52, 53, 54, 55, 56, 57, 58, 59)) {
			assertTrue(a.getIds().contains(id));
		}
		
		TimerConnectionAggregator b = run.get(1);
		System.err.println(b);
		
		assertThat(b.getDelay(), CoreMatchers.is(1000L));
		assertThat(b.getIds().size(), CoreMatchers.is(9));
		
		for (Integer id : Lists.newArrayList(60, 61, 62, 63, 64, 65, 66, 67, 68)) {
			assertTrue(b.getIds().contains(id));
		}
		
	}

	/*
  		NO TEST METHOD UNDER
	*/
	
	public static String getParentOfResource(String resource) throws Exception {
		String realPathOfResource = ConnectionScannerTest.getRealPathOfResource(resource);		
		String[] split = StringUtils.split(realPathOfResource, "/");
		
		return "/" + StringUtils.join(Arrays.copyOf(split, split.length - 1), "/");
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
