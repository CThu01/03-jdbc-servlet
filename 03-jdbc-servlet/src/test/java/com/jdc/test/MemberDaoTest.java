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
		var result = memberDao.findById(memberDto.email());
		assertEquals(memberDto, result);
	}
	
	@Test
	void testFindByEmailException() {
		var result = memberDao.findById("%s1".formatted(memberDto.email()));
		assertNull(result);
	}
	
	@Test
	void testFindByEmailNull() {
		assertThrows(IllegalArgumentException.class, () -> memberDao.findById(null));
	}
	
	
	
}









