package com.jdc.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.jdc.database.DatabaseInitializer;
import com.jdc.database.connection.ConnectionManager;
import com.jdc.database.dao.MemberDao;
import com.jdc.database.dao.MessageDao;
import com.jdc.database.dto.MemberDto;
import com.jdc.database.dto.MemberDto.Role;
import com.jdc.database.dto.MessageDto;
import com.jdc.database.utils.exception.MessageDbException;

@TestMethodOrder(OrderAnnotation.class)
public class MessageDaoTest {

	MessageDao messageDao;
	MemberDao memberDao;
	static MessageDto messageDto;
	
	@BeforeAll
	static void setupBefore() {
		
		DatabaseInitializer.truncate("member","message");
		
		try(var conn = ConnectionManager.getInstance().getConnection();
				var stmt = conn.prepareStatement("""
						insert into member (email,name,password,dob,role) values (?,?,?,?,?)
						""")){
			
			stmt.setString(1, "found@gmail.com");
			stmt.setString(2, "Found User");
			stmt.setString(3, "Found User");
			stmt.setDate(4, Date.valueOf("2000-01-01"));
			stmt.setString(5, Role.Member.name());
			stmt.addBatch();
			
			stmt.setString(1, "notfound@gmail.com");
			stmt.setString(2, "Not Found User");
			stmt.setString(3, "Not Found User");
			stmt.setDate(4, Date.valueOf("2000-01-01"));
			stmt.setString(5, Role.Member.name());
			stmt.addBatch(); 
			
			stmt.executeBatch();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		messageDto = new MessageDto("Test Title", "Test Message",
				new MemberDto("found@gmail.com", "Found User", "Found User", LocalDate.of(2000, 1, 1) , Role.Admin)
			);
	}
	
	@BeforeEach
	void eachSetup() throws Exception{
		messageDao = new MessageDao(ConnectionManager.getInstance());
	}
	
	@Test
	@Order(1)
	void testCreate() {
		var result = messageDao.create(messageDto);
		assertEquals(1, result.id());
	}
	
	@Test
	@Order(2)
	void testCreateWithNull() {
		assertThrows(IllegalArgumentException.class, () -> messageDao.create(null));
	}
	
	@Test
	@Order(3)
	void testCreateInvalidMember() {
		var message = new MessageDto("Test Title", "Test Message", 
				new MemberDto("asfsdasd", "Fo", "Found User", null, null));
		
		var exception = assertThrows(MessageDbException.class, () -> messageDao.create(message));
		assertEquals("Invalid Member", exception.getMessage());
	}
	
	@Test
	@Order(4)
	void testCreateNullMember() {
		var messageNull = new MessageDto("Test Title", "Test Message", null);
		var exceptionNull = assertThrows(MessageDbException.class, () -> messageDao.create(messageNull));
		assertEquals("Enter Member", exceptionNull.getMessage());
	}
	
	@Test
	@Order(5)
	void testCreateNullTitle() {
		var messageNull = new MessageDto(null, "Test Message", 
				             new MemberDto("found@gmail.com", "Found User", "Found User", LocalDate.of(2000, 1, 1) , Role.Admin));
		var exceptionNull = assertThrows(MessageDbException.class, () -> messageDao.create(messageNull));
		assertEquals("Enter Title", exceptionNull.getMessage());
		
		var messageEmpty = new MessageDto("", "Test Message", 
	             new MemberDto("found@gmail.com", "Found User", "Found User", LocalDate.of(2000, 1, 1) , Role.Admin));
		var exceptionEmpty = assertThrows(MessageDbException.class, () -> messageDao.create(messageNull));
		assertEquals("Enter Title", exceptionEmpty.getMessage());
	}
	
	@Test
	@Order(6)
	void testCreateNullMessage() {
		
		var messageNull = new MessageDto("Test Title", null, 
	             				new MemberDto("found@gmail.com", "Found User", "Found User", LocalDate.of(2000, 1, 1) , Role.Admin));
		var exceptionNull = assertThrows(MessageDbException.class, () -> messageDao.create(messageNull));
		assertEquals("Enter Message", exceptionNull.getMessage());

		
		var messageEmpty = new MessageDto("Test Title", "", 
								new MemberDto("found@gmail.com", "Found User", "Found User", LocalDate.of(2000, 1, 1) , Role.Admin));
		var exceptionEmpty = assertThrows(MessageDbException.class, () -> messageDao.create(messageNull));
		assertEquals("Enter Message", exceptionEmpty.getMessage());
	}
	
	@Test
	@Order(7)
	void testFindById() {
		
		var result = messageDao.findById(1);
		assertEquals(1, result.id());
		assertEquals(messageDto.cloneWithId(1).title(), result.title());
	}
	
	@Test
	@Order(8)
	void testFindByIdNotFound() {
		
		var exception = assertThrows(MessageDbException.class, () -> messageDao.findById(2));
		assertEquals("There is no message", exception.getMessage());
	}
	
	@Test
	@Order(9)
	void testSave() {
		var title = "New Title";
		var text = "New Message";
		var message = messageDao.findById(1);
		message = message.cloneWithTitle(title).cloneWithMessage(text);
		int result = messageDao.save(message);
		assertEquals(1, result);
		assertEquals(message, messageDao.findById(1));
		assertEquals(title, message.title());
		assertEquals(text, message.message());
	}
	
	@Test
	@Order(10)
	void testSaveNull() {
		assertThrows(IllegalArgumentException.class, () -> messageDao.save(null));
	}
	
	@Test
	@Order(11)
	void testSaveNullTitle() {
		
		var messageNull = messageDao.findById(1).cloneWithTitle(null).cloneWithMessage("New Message");
		MessageDbException exceptionNull = assertThrows(MessageDbException.class, () -> messageDao.save(messageNull));
		assertEquals("Enter Title", exceptionNull.getMessage());
		
		var messageEmpty = messageDao.findById(1).cloneWithTitle("").cloneWithMessage("New Message");
		MessageDbException exceptionEmpty = assertThrows(MessageDbException.class, () -> messageDao.save(messageEmpty));
		assertEquals("Enter Title", exceptionEmpty.getMessage());
	}
	
	@Test
	@Order(12)
	void testSaveNullMessage() {
		
		var messageNull = messageDao.findById(1).cloneWithTitle("New Title").cloneWithMessage(null);
		MessageDbException exceptionNull = assertThrows(MessageDbException.class, () -> messageDao.save(messageNull));
		assertEquals("Enter Message", exceptionNull.getMessage());
		
		var messageEmpty = messageDao.findById(1).cloneWithTitle("New Title").cloneWithMessage("");
		MessageDbException exceptionEmpty = assertThrows(MessageDbException.class, () -> messageDao.save(messageEmpty));
		assertEquals("Enter Message", exceptionEmpty.getMessage());
	}
	
	@Test
	@Order(13)
	void testSearchByEmail() {
		var result = messageDao.searchByEmail("found@gmail.com");
		assertEquals(1, result.size());
		assertEquals("New Title", result.get(0).title());
	}
	
	@Test
	@Order(14)
	void testSearchByEmailWithNull() {
		assertThrows(IllegalArgumentException.class, () -> messageDao.searchByEmail(null));
	}
	
	@Test
	@Order(15)
	void testSearchByEmailNotFound() {
		var exception = assertThrows(MessageDbException.class, () -> messageDao.searchByEmail("found@gmail.com".concat(" ")));
		assertEquals("Email not Found", exception.getMessage());
	}

	@Test
	@Order(16)
	void testSearch() {
		
		var result = messageDao.search("found", "title");
		assertEquals(1, result.size());
	}

	@Test
	@Order(17)
	void testSearchWithNull() {
		
		var result = messageDao.search(null, null);
		assertEquals(1, result.size());
	}
	
	@Test
	@Order(18)
	void testSearchWithEmptyName() {
		
		var resultNull = messageDao.search(null, "title");
		assertEquals(1, resultNull.size());
		
		var resultEmpty = messageDao.search("", "title");
		assertEquals(1, resultEmpty.size());		
	}
	
	@Test
	@Order(19)
	void testSearchWithEmptyKeyword() {

		var resultNull = messageDao.search("found", null);
		assertEquals(1, resultNull.size());
		
		var resultEmpty = messageDao.search("found", "");
		assertEquals(1, resultEmpty.size());		
	}


}











