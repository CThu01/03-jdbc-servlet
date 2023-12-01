package com.jdc.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jdc.database.DatabaseInitializer;
import com.jdc.database.connection.ConnectionManager;
import com.jdc.database.dao.MemberDao;
import com.jdc.database.dto.MemberDto;
import com.jdc.database.dto.MemberDto.Role;
import com.jdc.database.utils.exception.MessageDbException;

public class MemberDaoTest {

	MemberDao memberDao;
	static MemberDto memberDto;
	
	@BeforeAll
	static void setupBefore() {
		DatabaseInitializer.truncate("memeber","message");
		memberDto = new MemberDto("floyd@gmail.com","Floyd","foyd",LocalDate.of(2000, 4, 24),Role.Admin);
	}
	
	@BeforeEach
	void setupEach() {
		memberDao = new MemberDao(ConnectionManager.getInstance());
	}
	
	@Test
	void testCreateMember() {
		var member = new MemberDto("floyd@gmail.com","Floyd","foyd",LocalDate.of(2000, 4, 24),Role.Admin);
		assertEquals(1, member);
	}
	
	@Test 
	void testDuplicateMember() {
		var member = new MemberDto("floyd@gmail.com","Floyd","foyd",LocalDate.of(2000, 4, 24),Role.Admin);
		var exception = assertThrows(MessageDbException.class, () -> memberDao.create(member));
		
		assertEquals("Your email has already been used", exception.getMessage());
	}
	
	@Test
	void testCreateMemberNull() {
		assertThrows(IllegalArgumentException.class, () -> memberDao.create(null));
	}
	
	@Test
	void testCreateMemberNullName(){
		
		var nameNull = new MemberDto("floyd@gmail.com", null, "foyd", LocalDate.of(2000, 4, 24), Role.Admin);
		var nullexception = assertThrows(MessageDbException.class, () -> memberDao.create(nameNull));
		assertEquals("Member name must be Enter", nullexception.getMessage());
		
		var nameEmpty = new MemberDto("floyd@gmail.com", "", "foyd", LocalDate.of(2000, 4, 24), Role.Admin);
		var emptyException = assertThrows(MessageDbException.class, () -> memberDao.create(nameEmpty));
		assertEquals("Member name must be Enter", emptyException.getMessage());
	}
	
	@Test
	void testCreateMemberNullEmail() {

		var emailNull = new MemberDto(null, "Floyd", "foyd", LocalDate.of(2000, 4, 24), Role.Admin);
		var nullexception = assertThrows(MessageDbException.class, () -> memberDao.create(emailNull));
		assertEquals("Member email must be Enter", nullexception.getMessage());
		
		var emailEmpty = new MemberDto("", "Floyd", "foyd", LocalDate.of(2000, 4, 24), Role.Admin);
		var emptyException = assertThrows(MessageDbException.class, () -> memberDao.create(emailEmpty));
		assertEquals("Member email must be Enter", emptyException.getMessage());

	}

	@Test
	void testCreateMemberNullPassword(){

		var passwordNull = new MemberDto("floyd@gmail.com", "Floyd", null, LocalDate.of(2000, 4, 24), Role.Admin);
		var nullexception = assertThrows(MessageDbException.class, () -> memberDao.create(passwordNull));
		assertEquals("Member password must be Enter", nullexception.getMessage());
		
		var passwordEmpty = new MemberDto("floyd@gmail.com", "Floyd", "", LocalDate.of(2000, 4, 24), Role.Admin);
		var emptyException = assertThrows(MessageDbException.class, () -> memberDao.create(passwordEmpty));
		assertEquals("Member email must be Enter", emptyException.getMessage());
	}

	@Test
	void testCreateMemberNullDob(){

		var dobNull = new MemberDto("floyd@gmail.com", "Floyd", "foyd", null, Role.Admin);
		var nullexception = assertThrows(MessageDbException.class, () -> memberDao.create(dobNull));
		assertEquals("Member's date of brith must be Enter", nullexception.getMessage());

	}

	@Test
	void testFindByEmailOK() {
		var result = memberDao.findByEmail(memberDto.email());
		assertEquals(memberDto, result);
	}
	
	@Test
	void testFindByEmailException() {
		var result = memberDao.findByEmail("%s1".formatted(memberDto.email()));
		assertNull(result);
	}
	
	@Test
	void testFindByEmailNull() {
		assertThrows(IllegalArgumentException.class, () -> memberDao.findByEmail(null));
	}
	
	
	@Test
	void testChangePasswordOK() {
		var newPassword = "changedPassword";
		int result = memberDao.changePassword(memberDto.email(), memberDto.password(), newPassword);
		assertEquals(1, result);
		
		var memberEmail = memberDao.findByEmail(memberDto.email());
		assertEquals(newPassword, memberEmail.password());
	}
	
	@Test
	void testChangePassowrdNotFoundEamil() {
		var exception = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword("%s1".formatted(memberDto.email()), memberDto.password(), "changedPassword"));
		
		assertEquals("Please check your email", exception.getMessage());
	}

	@Test
	void testChangePasswordNullEmail() {
		var exceptionNull = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(null, memberDto.password(), "changedPassword"));
		assertEquals("Email must not be empty", exceptionNull.getMessage());
		
		var exceptionEmpty = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword("", memberDto.password(), "changedPassword"));
		assertEquals("Email must not be empty", exceptionEmpty.getMessage());
	}
	
	@Test
	void testChangePasswordNullOld() {
		var exceptionNull = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(memberDto.email(), null, "changedPassword"));
		assertEquals("Old password must not be empty", exceptionNull.getMessage());
		
		var exceptionEmpty = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(memberDto.email(), "", "changedPassword"));
		assertEquals("Old password must not be empty", exceptionEmpty.getMessage());
	}
	
	@Test
	void testChangePasswordNullNew() {
		var exceptionNull = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(memberDto.email(), "changedPassword", null));
		assertEquals("New password not be empty", exceptionNull.getMessage());
		
		var exceptionEmpty = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(memberDto.email(), "changedPassword", ""));
		assertEquals("New password not be empty", exceptionEmpty.getMessage());
		
	}
	
	@Test
	void testChangePasswordNotEqualOld() {
		var exception = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(memberDto.email(), "fyguhjk", "secondTimeChangedPassword"));
		assertEquals("Your old password is invalid", exception.getMessage());
	}
	
	@Test
	void testChangePasswordSame() {
		var exception = assertThrows(MessageDbException.class, 
				() -> memberDao.changePassword(memberDto.email(), "changedPassword", "changedPassword"));
		assertEquals("old password and new password are the same", exception.getMessage());

	}
	
	
	
}



















