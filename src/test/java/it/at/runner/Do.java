package it.at.runner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class Do {

	public static void main(String[] args) throws Exception {
		
		final ComboPooledDataSource ds = new ComboPooledDataSource();
		
		ds.setDriverClass("com.mysql.jdbc.Driver");		
		ds.setJdbcUrl("jdbc:mysql://db1m-staging.at.test:3306/");
		ds.setUser("u_systest");
		ds.setPassword("tsetsys_u");
		
		ds.setAcquireIncrement(10);
		ds.setMaxPoolSize(100);
		ds.setMinPoolSize(10);
		
		final Connection connection = ds.getConnection();
		
		executeSqlStatement(connection, "SHOW KEYS FROM `MLR_TA_DELIVERY` FROM `C3000100`");
		executeSqlStatement(connection, "show databases");
		
		connection.setCatalog("C3000100");		
		executeSqlStatement(connection, "show tables");
			
	}

	private static void executeSqlStatement(final Connection connection, final String sql) throws SQLException {		
		final Statement stmt = connection.createStatement();		
		
		System.out.println("execute> " + sql);
		final ResultSet rs = stmt.executeQuery(sql);
				
		while(rs.next()) {
			System.out.println("> " + rs.getString(1));
		}		
		
		stmt.close();
	}

//	private static void executeSqlStatement(final Connection connection, final String sql) throws SQLException {
//		final Statement stmt = connection.createStatement();		
//		
//		System.out.println("execute> " + sql);
//		System.out.println("result> " + stmt.execute(sql));
//		
//		if (stmt.getFetchSize() > 0) {
//			System.out.println("result> " + stmt.getResultSet().getString(0));	
//		}
//		
//		stmt.close();
//	}
}
