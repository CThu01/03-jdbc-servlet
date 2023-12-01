package com.jdc.database.dto;

import java.time.LocalDateTime;

public record MessageDto(
		int id,
		String email,
		String title,
		String message,
		LocalDateTime postAt,
		MemberDto member
		) {

	public MessageDto(String email,String title,String message,MemberDto member) {
		this(0,email,title,message,null,member);
	}
	
	public MessageDto cloneWithId(int id) {
		return new MessageDto(id,this.email,this.title,this.message,this.postAt,this.member);
	}
}
