package com.jdc.database.dao;

import com.jdc.database.connection.ConnectionManager;
import com.jdc.database.dto.MemberDto;

public class MemberDao {
	
	ConnectionManager connectionManager;
	
	public MemberDao(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}
	
	public MemberDto create(MemberDto data) {
		return null;
	}
	
	public MemberDto findById(String email) {
		return null;
	}
	
	public int changePassword(String email, String oldPassword, String newPassword) {
		return 0;
	}

}
