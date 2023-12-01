package com.jdc.database.dao;

import com.jdc.database.connection.ConnectionManager;
import com.jdc.database.dto.MemberDto;
import com.jdc.database.dto.MessageDto;

public class MessageDao {

	ConnectionManager connectionManager;
	
	public MessageDao(ConnectionManager connecitonManager) {
		this.connectionManager = connecitonManager;
	}
	
	public MessageDto create(MessageDto data) {
		return null;
	}
	
	public MessageDto findById(int id) {
		return null;
	}
	
	public MessageDto search(String email,String keyword) {
		return null;
	}
	
	public MessageDto searchMember(MemberDto member) {
		return null;
	}
	
	public int save(MessageDto message) {
		return 0;
	}
	
}
