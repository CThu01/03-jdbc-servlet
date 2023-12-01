package com.jdc.database.dao;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.jdc.database.connection.ConnectionManager;
import com.jdc.database.dto.MemberDto;
import com.jdc.database.dto.MemberDto.Role;
import com.jdc.database.utils.StringUtils;
import com.jdc.database.utils.exception.MessageDbException;

public class MemberDao {
	
	ConnectionManager connectionManager;
	
	public MemberDao(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}
	
	public int create(MemberDto member) {
		
		if(null == member) {
			throw new IllegalArgumentException();
		}
		
		if(StringUtils.isEmpty(member.email())) {
			throw new MessageDbException("Email must be Enter");
		}
		
		if(StringUtils.isEmpty(member.name())) {
			throw new MessageDbException("Name must be Enter");
		}
		
		if(StringUtils.isEmpty(member.password())) {
			throw new MessageDbException("Password must be Enter");
		}
		
		if(null == member.dob()) {
			throw new MessageDbException("Date of brith must be Enter");
		}
		
		try(var conn = connectionManager.getConnection();
				var stmt = conn.prepareStatement("""
						insert into member (email,name,password,dob,role) values (?,?,?,?,?)
						""")){
			
			stmt.setString(1, member.email());
			stmt.setString(2, member.name());
			stmt.setString(3, member.password());
			stmt.setDate(4, Date.valueOf(member.dob()));
			stmt.setString(5,member.role().name());
			
			return stmt.executeUpdate();
			
		} catch (SQLException e) {
			throw new MessageDbException("Your email has been used");
		}
		
	}
	
	public MemberDto findByEmail(String email) {
		
		if(null == email) {
			throw new IllegalArgumentException();
		}
		
		try(var conn = connectionManager.getConnection();
				var stmt = conn.prepareStatement("""
						select * from member where email=?
						""")){
			
			stmt.setString(1, email);
			var result = stmt.executeQuery();
			
			while(result.next()) {
				return new MemberDto(
						result.getString("email"), 
						result.getString("name"),
						result.getString("password"),
						result.getDate("dob").toLocalDate(),
						Role.valueOf(result.getString("role")));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public int changePassword(String email, String oldPassword, String newPassword) {
		
		if(StringUtils.isEmpty(email)) {
			throw new MessageDbException("Email must not be empty");
		}
		
		if(StringUtils.isEmpty(oldPassword)) {
			throw new MessageDbException("Old password must not be empty");
		}
		
		if(StringUtils.isEmpty(newPassword)) {
			throw new MessageDbException("New password not be empty");
		}
		
		if(oldPassword.equals(newPassword)) {
			throw new MessageDbException("Old password and New password must not be the same");
		}
		
		try(var conn = connectionManager.getConnection();
				var stmt = conn.prepareStatement("""
						select * from member where email=?
						""",ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_UPDATABLE)){
			stmt.setString(1, email);
			var resultSet = stmt.executeQuery();
			
			while(resultSet.next()) {
				if(!oldPassword.equals(resultSet.getString("password"))) {
					throw new MessageDbException("Your old password isn't correct");
				}
				
				resultSet.updateString("password", newPassword);
				resultSet.updateRow();
				return 1;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		throw new MessageDbException("Please check your email");
	}
	
	
	

}












