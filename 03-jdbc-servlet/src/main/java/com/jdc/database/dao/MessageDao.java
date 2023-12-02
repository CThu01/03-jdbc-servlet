package com.jdc.database.dao;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.jdc.database.connection.ConnectionManager;
import com.jdc.database.dto.MemberDto;
import com.jdc.database.dto.MessageDto;
import com.jdc.database.dto.MemberDto.Role;
import com.jdc.database.utils.exception.MessageDbException;

public class MessageDao {

	ConnectionManager connectionManager;
	MemberDao memberDao;
	
	public MessageDao(ConnectionManager connecitonManager) {
		this.connectionManager = connecitonManager;
		memberDao = new MemberDao(connecitonManager);
	}
	
	public MessageDto create(MessageDto message) {
		
		if(null == message) {
			throw new IllegalArgumentException();
		}
		
		if(null == message.title()) {
			throw new MessageDbException("Enter Title");
		}
		
		if(null == message.message()) {
			throw new MessageDbException("Enter Message");
		}
		
		if(null == message.member()) {
			throw new MessageDbException("Enter Member");
		}
		
		if(null == memberDao.findByEmail(message.member().email())) {
			throw new MessageDbException("Invalid Member");
		}
		
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
				var stmt = conn.prepareStatement("""
						select ms.id id,ms.email email,ms.title title,ms.message message,ms.post_at post_at,mb.email email,
						mb.name name,mb.password password,mb.dob dob,mb.role role 
						from message ms inner join member mb on mb.email=ms.email where ms.id=?
						""")){
			
			stmt.setInt(1, id);
			var resultSet = stmt.executeQuery();
			
			while(resultSet.next()) {
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
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		throw new MessageDbException("There is no message");
	}
	
	public List<MessageDto> search(String memberName,String keyword) {
		return null;
	}
	
	public List<MessageDto> searchByEmail(String email) {
		return null;
	}
	
	public int save(MessageDto message) {
		return 0;
	}
	
}
 