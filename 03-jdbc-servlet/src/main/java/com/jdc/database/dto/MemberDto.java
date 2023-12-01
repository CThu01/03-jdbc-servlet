package com.jdc.database.dto;

import java.time.LocalDate;

public record MemberDto(
		String email,
		String name,
		String password,
		LocalDate dob,
		Role role
		) {

	public enum Role{
		Admin,Member;
	}
}
