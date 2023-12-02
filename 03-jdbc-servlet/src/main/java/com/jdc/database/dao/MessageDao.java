package com.jdc.database.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.jdc.database.connection.ConnectionManager;
import com.jdc.database.dto.MemberDto;
import com.jdc.database.dto.MessageDto;
import com.jdc.database.dto.MemberDto.Role;
import com.jdc.database.utils.StringUtils;
import com.jdc.database.utils.exception.MessageDbException;

public class MessageDao {

	ConnectionManager connectionManager;
	MemberDao memberDao;
	
	String SQL_STMT = """
			select ms.id id,ms.email email,ms.title title,ms.message message,ms.post_at post_at,mb.email email,
			mb.name name,mb.password password,mb.dob dob,mb.role role 
			from message ms inner join member mb on mb.email=ms.email where 1=1
			""";
	
	public MessageDao(ConnectionManager connecitonManager) {
		this.connectionManager = connecitonManager;
		memberDao = new MemberDao(connecitonManager);
	}
	
	private void validate(MessageDto message) {
		
		if(null == message) {
			throw new IllegalArgumentException();
		}   
		
		if(StringUtils.isEmpty(message.title())) {
			throw new MessageDbException("Enter Title");
		}
		
		if(StringUtils.isEmpty(message.message())) {
			throw new MessageDbException("Enter Message");
		}
		
		if(null == message.member()) {
			throw new MessageDbException("Enter Member");
		}
		
		if(null == memberDao.findByEmail(message.member().email())) {
			throw new MessageDbException("Invalid Member");
		}

	}
	
	public MessageDto create(MessageDto message) {
		
		validate(message);
		
		try(var conn = connectionManager.getConnection();
				var stmt = conn.prepareStatement("""
						insert into message (email,title,message) values (?,?,?)
						""",Statement.RETURN_GENERATED_KEYS)){
			
			stmt.setString(1, message.member().email());
			stmt.setString(2, message.title());
			stmt.setString(3, message.message());
			stmt.executeUpdate();
			
			var keys = stmt.getGeneratedKeys();
			if(keys.next()) {
				return message.cloneWithId(keys.getInt(1));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public MessageDto findById(int id) {
		
		try(var conn = connectionManager.getConnection();
				var stmt = conn.prepareStatement(SQL_STMT.concat(" and ms.id=?"))){
			
			stmt.setInt(1, id);
			var resultSet = stmt.executeQuery();
			
			while(resultSet.next()) {
				return retriveMessage(resultSet);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		throw new MessageDbException("There is no message");
	}
	
	public List<MessageDto> search(String memberName,String keyword) {
		
		List<MessageDto> messageList = new ArrayList<MessageDto>();
		StringBuffer sql = new StringBuffer(SQL_STMT);
		List<String> params = new ArrayList<String>();
		
		if(!StringUtils.isEmpty(memberName)) {
			sql.append(" and lower(mb.name) like ?");
			params.add("%".concat(memberName).concat("%").toLowerCase());
		}
		
		if(!StringUtils.isEmpty(keyword)) {
			sql.append(" and lower(ms.title) like ? or lower(ms.message) like ?");
			params.add("%".concat(keyword).concat("%").toLowerCase());
			params.add("%".concat(keyword).concat("%").toLowerCase());
		}
		
		try(var conn = connectionManager.getConnection();
				var stmt = conn.prepareStatement(sql.toString())){
			
			for(int i=0; i < params.size(); i++) {
				stmt.setObject(i+1, params.get(i));
			}
			var resultSet = stmt.executeQuery();
			
			while(resultSet.next()) {
				messageList.add(retriveMessage(resultSet));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return messageList;
	}
	
	public List<MessageDto> searchByEmail(String email) {
		
		List<MessageDto> messageList = new ArrayList<MessageDto>();
		if(null == email) {
			throw new IllegalArgumentException();
		}
		
		try(var conn = connectionManager.getConnection();
				var stmt = conn.prepareStatement(SQL_STMT.concat(" and ms.email=?"))){
			
			stmt.setString(1, email);
			var resultSet = stmt.executeQuery();
			while(resultSet.next()) {
				messageList.add(retriveMessage(resultSet)); 
			}
			
			if(messageList.size() > 0) {
				return messageList;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		throw new MessageDbException("Email not Found");
	}
	
	public int save(MessageDto message) {
		
		validate(message);
		
		try(var conn = connectionManager.getConnection();
				var stmt = conn.prepareStatement("""
						update message set title=?,message=? where id=?
						""")){
			stmt.setString(1, message.title());
			stmt.setString(2, message.message());
			stmt.setInt(3, message.id());
			
			return stmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	private MessageDto retriveMessage(ResultSet resultSet) throws SQLException{
		return new MessageDto(
				resultSet.getInt("id"),
				resultSet.getString("title"), 
				resultSet.getString("message"),
				resultSet.getTimestamp("post_at").toLocalDateTime(),
				new MemberDto(
						resultSet.getString("email"),
						resultSet.getString("name"),
						resultSet.getString("password"), 
						resultSet.getDate("dob").toLocalDate(),
						Role.valueOf(resultSet.getString("role")))
				);
	}
	
	
}














 