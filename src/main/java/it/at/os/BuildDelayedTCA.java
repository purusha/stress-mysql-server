package it.at.os;

import java.io.IOException;

public class BuildDelayedTCA extends WriteEachLine {
		
	public BuildDelayedTCA(String parentDir) {
		super(parentDir);		
	}
	
	protected void appendToConnectionFile(String strLine, final Integer connectionId) throws IOException {
		//DO NOT WRITE NOTHING ... FILES JUST EXIST !!?
	}
}
