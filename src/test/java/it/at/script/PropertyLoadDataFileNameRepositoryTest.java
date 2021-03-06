package it.at.script;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import it.at.os.PropertyLoadDataFileNameRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

public class PropertyLoadDataFileNameRepositoryTest {

	private static final String VALUE_2 = Arrays.toString(new String[] { "2a" });
	private static final String VALUE_1 = Arrays.toString(new String[] { "1a", "1b", "1c" });
	
	public static final String PREFIX = "bellazio";
    public static final String SUFFIX = ".properties";	
    
    final File f;
    
    public PropertyLoadDataFileNameRepositoryTest() throws IOException {
    	f = File.createTempFile(PREFIX, SUFFIX);
    	f.deleteOnExit();
	}

	@Before
	public void setUp() throws Exception {
		final Properties p = new Properties();
		p.put("1", VALUE_1);
		p.put("2", VALUE_2);

		final OutputStream out = new FileOutputStream(f);
		p.store(out, "");
	}

	@Test
	public void simpleCaseOfTwoValidValues() throws Exception {
		final PropertyLoadDataFileNameRepository repository = new PropertyLoadDataFileNameRepository();
		
		repository.run(Sets.newHashSet(1, 2), f.getAbsolutePath());
		
		assertTrue(repository.containsKey(1));			
		assertThat(Arrays.toString(repository.get(1)), org.hamcrest.CoreMatchers.is(VALUE_1));
		
		assertTrue(repository.containsKey(2));			
		assertThat(Arrays.toString(repository.get(2)), org.hamcrest.CoreMatchers.is(VALUE_2));
						
		assertFalse(repository.containsKey(3));
		assertFalse(repository.containsKey(42));
		
	}

}
