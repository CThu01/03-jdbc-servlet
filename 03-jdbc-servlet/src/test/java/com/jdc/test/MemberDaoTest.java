package com.jdc.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.jdc.database.DatabaseInitializer;
import com.jdc.database.connection.ConnectionManager;
import com.jdc.database.dao.MemberDao;
import com.jdc.database.dto.MemberDto;
import com.jdc.database.dto.MemberDto.Role;
import com.jdc.database.utils.exception.MessageDbException;

@TestMethodOrder(OrderAnnotation.class)
public class MemberDaoTest {

	MemberDao memberDao;
	static MemberDto memberDto;
	
	@BeforeAll
	static void setupBefore() {
		DatabaseInitializer.truncate("member","message");
		memberDto = new MemberDto("floyd@gmail.com","Floyd","foyd",LocalDate.of(2000, 4, 24),Role.Admin);
	}
	
	@BeforeEach
	void setupEach() {
		memberDao = new MemberDao(ConnectionManager.getInstance());
	}
	
	@Test
	@Order(1)
	void testCreateMember() {
		var member = new MemberDto("floyd@gmail.com","Floyd","foyd",LocalDate.of(2000, 4, 24),Role.Admin);
		var result = memberDao.create(member);
		assertEquals(1, result);
	}
	
	@Test 
	@Order(2)
	void testDuplicateMember() {
		var member = new MemberDto("floyd@gmail.com","Floyd","foyd",LocalDate.of(2000, 4, 24),Role.Admin);
		var exception = assertThrows(MessageDbException.class, () -> memberDao.create(member));
		
		assertEquals("Your email has been used", exception.getMessage());
	}
	
	@Test
	@Order(3)
	void testCreateMemberNull() {
		assertThrows(IllegalArgumentException.class, () -> memberDao.create(null));
	}
	
	@Test
	@Order(4)
	void testCreateMemberNullName(){
		
		var nameNull = new MemberDto("floyd@gmail.com", null, "foyd", LocalDate.of(2000, 4, 24), Role.Admin);
		var nullexception = assertThrows(MessageDbException.class, () -> memberDao.create(nameNull));
		assertEquals("Name must be Enter", nullexception.getMessage());
		
		var nameEmpty = new MemberDto("floyd@gmail.com", "", "foyd", LocalDate.of(2000, 4, 24), Role.Admin);
		var emptyException = assertThrows(MessageDbException.class, () -> memberDao.create(nameEmpty));
		assertEquals("Name must be Enter", emptyException.getMessage());
	}
	
	@Test
	@Order(5)
	void testCreateMemberNullEmail() {

		var emailNull = new MemberDto(null, "Floyd", "foyd", LocalDate.of(2000, 4, 24), Role.Admin);
		var nullexception = assertThrows(MessageDbException.class, () -> memberDao.create(emailNull));
		assertEquals("Email must be Enter", nullexception.getMessage());
		
		var emailEmpty = new MemberDto("", "Floyd", "foyd", LocalDate.of(2000, 4, 24), Role.Admin);
		var emptyException = assertThrows(MessageDbException.class, () -> memberDao.create(emailEmpty));
		assertEquals("Email must be Enter", emptyException.getMessage());

	}

	@Test
	@Order(6)
	void testCreateMemberNullPassword(){

		var passwordNull = new MemberDto("floyd@gmail.com", "Floyd", null, LocalDate.of(2000, 4, 24), Role.Admin);
		var nullexception = assertThrows(MessageDbException.class, () -> memberDao.create(passwordNull));
		assertEquals("Password must be Enter", nullexception.getMessage());
		
		var passwordEmpty = new MemberDto("floyd@gmail.com", "Floyd", "", LocalDate.of(2000, 4, 24), Role.Admin);
		var emptyException = assertThrows(MessageDbException.class, () -> memberDao.create(passwordEmpty));
		assertEquals("Password must be Enter", emptyException.getMessage());
	}

	@Test
	@Order(7)
	void testCreateMemberNullDob(){

		var dobNull = new MemberDto("floyd@gmail.com", "Floyd", "foyd", null, Role.Admin);
		var nullexception = assertThrows(MessageDbException.class, () -> memberDao.create(dobNull));
		assertEquals("Date of brith must be Enter", nullexception.getMessage());
	}

	@Test
	@Order(8)
	void testFindByEmailOK() {
		var result = memberDao.findByEmail(memberDto.email());
		assertEquals(memberDto, result);
	}
	
	@Test
	@Order(9)
	void testFindByEmailException() {
		var result = memberDao.findByEmail("%s1".formatted(memberDto.email()));
		assertNull(result);
	}
	
	@Test
	@Order(10)
	void testFindByEmailNull() {
		assertThrows(IllegalArgumentException.class, () -> memberDao.findByEmail(null));
	}
	
	@Test
	@Order(11)
	void testChangePasswordOK() {
		var newPassword = "changedPassword";
		int result = memberDao.changePassword(memberDto.email(), memberDto.password(), newPassword);
		assertEquals(1, result);
		
		var memberEmail = memberDao.findByEmail(memberDto.email());
		assertEquals(newPassword, memberEmail.password());
	}
	
	@Test
	@Order(12)
	void testChangePassowrdNotFoundEamil() {
		var exception = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword("%s1".formatted(memberDto.email()), memberDto.password(), "changedPassword"));
		
		assertEquals("Please check your email", exception.getMessage());
	}

	@Test
	@Order(13)
	void testChangePasswordNullEmail() {
		var exceptionNull = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(null, memberDto.password(), "changedPassword"));
		assertEquals("Email must not be empty", exceptionNull.getMessage());
		
		var exceptionEmpty = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword("", memberDto.password(), "changedPassword"));
		assertEquals("Email must not be empty", exceptionEmpty.getMessage());
	}
	
	@Test
	@Order(14)
	void testChangePasswordNullOld() {
		var exceptionNull = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(memberDto.email(), null, "changedPassword"));
		assertEquals("Old password must not be empty", exceptionNull.getMessage());
		
		var exceptionEmpty = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(memberDto.email(), "", "changedPassword"));
		assertEquals("Old password must not be empty", exceptionEmpty.getMessage());
	}
	
	@Test
	@Order(15)
	void testChangePasswordNullNew() {
		var exceptionNull = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(memberDto.email(), "changedPassword", null));
		assertEquals("New password not be empty", exceptionNull.getMessage());
		
		var exceptionEmpty = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(memberDto.email(), "changedPassword", ""));
		assertEquals("New password not be empty", exceptionEmpty.getMessage());
	}
	
	@Test
	@Order(16)
	void testChangePasswordOldUnmatch() {
		var exception = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(memberDto.email(), "fyguhjk", "secondTimeChangedPassword"));
		assertEquals("Your old password isn't correct", exception.getMessage());
	}
	
	@Test
	@Order(17)
	void testChangePasswordSame() {
		var exception = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(memberDto.email(), "changedPassword", "changedPassword"));
		assertEquals("Old password and New password must not be the same", exception.getMessage());

	}
	
	
	
}



















