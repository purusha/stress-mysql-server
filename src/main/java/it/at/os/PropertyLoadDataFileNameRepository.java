package it.at.os;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class PropertyLoadDataFileNameRepository implements LoadDataFileNameRepository {
	protected static final Logger logger = LoggerFactory.getLogger(PropertyLoadDataFileNameRepository.class);
	
	private final Properties data;

	@Inject
	public PropertyLoadDataFileNameRepository() {
		data = new Properties();		
	}
	
	public void run(Set<Integer> i, String pathOfRealFileNames) throws Exception {
		data.load(new FileInputStream(pathOfRealFileNames));
		
		if (data.size() != i.size()) {
			logger.error("When load real file name of " + i.size() + " thread statement !!? ... found only " + data.size());
		}
	}

	@Override
	public boolean containsKey(Integer i) {		
		return data.get(String.valueOf(i)) != null;
	}

	@Override
	public String[] get(Integer i) {
		final String value = String.valueOf(data.get(String.valueOf(i)));
		
		if (StringUtils.isNoneBlank(value)) {			
			return StringUtils.split(StringUtils.substringBetween(value, "[", "]"), ", ");			
		} else {
			return null;	
		}
	}

}
