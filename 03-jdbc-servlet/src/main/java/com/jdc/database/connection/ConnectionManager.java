package com.jdc.database.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public interface ConnectionManager {

	Connection getConnection() throws SQLException;
	
	String URL = "jdbc:mysql//localhost:3306/message_db_servlet";
	String USR = "root";
	String PWD = "admin";
	
	public static ConnectionManager getInstance() {
		return () -> DriverManager.getConnection(URL, USR, PWD);
	}
}
