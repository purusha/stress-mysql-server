package it.at.script;

import it.at.os.SplitGeneralQueryLogFile;
import it.at.runner.ApplicationRunner;

public class LocalSplitGeneralQueryLogFile {

	public static void main(String[] args) throws Exception {
		
		final String generalLogQueryFile = "/media/alan/DATA/6/general.log"; 
		final String parentDir = "/media/alan/DATA/6" + ApplicationRunner.SUB_PATH_FILES;
		
		new SplitGeneralQueryLogFile().run(generalLogQueryFile, parentDir);
		
	}
}
