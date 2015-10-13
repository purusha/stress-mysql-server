package it.at.script;

public class ApplicationParameter {
	
	private Integer numberOfThread;
	private String databaseServer;	
	private String pathOfPayload;
	
	public ApplicationParameter() {
	} 
	
	public ApplicationParameter(Integer numberOfThread, String databaseServer, String pathOfPayload) {
		this.numberOfThread = numberOfThread;
		this.databaseServer = databaseServer;
		this.pathOfPayload = pathOfPayload;		
	} 

	public Integer getNumberOfThread() {
		return numberOfThread;
	}
	
	public void setNumberOfThread(Integer numberOfThread) {
		this.numberOfThread = numberOfThread;
	}

	public void setDatabaseServer(String databaseServer) {
		this.databaseServer = databaseServer;
	}
	
	public String getDatabaseServer() {
		return databaseServer;
	}

	public void setPathOfPayload(String pathOfPayload) {
		this.pathOfPayload = pathOfPayload;
	}
	
	public String getPathOfPayload() {
		return pathOfPayload;
	}
	
	public String generalQueryLogPath() {
		return pathOfPayload + ApplicationParameterHandler.GENERAL_QUERY_LOG_FILE;
	}
	
	public String binaryLogDirPath() {
		return pathOfPayload + ApplicationParameterHandler.BYNARY_LOG_DIR;
	}
	
	public String loadDataDirPath() {
		return pathOfPayload + ApplicationParameterHandler.LOAD_DATA_DIR;
	}

	public String getPathOfRealFileNames() {
		return pathOfPayload + ApplicationParameterHandler.NAMES_FILE;
	}
}
