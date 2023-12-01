package com.jdc.database;

import java.sql.SQLException;

import com.jdc.database.connection.ConnectionManager;

public class DatabaseInitializer {

	public static void truncate(String ...tables) {
		
		try(var conn = ConnectionManager.getInstance().getConnection();
				var stmt = conn.createStatement();){
			
			stmt.execute("set foreign_key_checks=0");
			for(String table : tables) {
				stmt.executeUpdate("truncate table %s".formatted(table));				
			}
			
			stmt.execute("set foreign_key_checks=1");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
