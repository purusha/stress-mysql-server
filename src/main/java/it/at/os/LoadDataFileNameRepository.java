package it.at.os;

public interface LoadDataFileNameRepository {
	
	public static final String END_LOADDATA_STATEMENT = " INTO TABLE";
	public static final String START_LOADDATA_STATEMENT = "LOAD DATA LOCAL INFILE ";	

	boolean containsKey(Integer i);

	String[] get(Integer i);

}
