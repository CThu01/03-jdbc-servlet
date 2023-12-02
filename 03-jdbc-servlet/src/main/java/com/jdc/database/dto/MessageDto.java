package com.jdc.database.dto;

import java.time.LocalDateTime;

public record MessageDto(
		int id,
		String title,
		String message,
		LocalDateTime postAt,
		MemberDto member
		) {

	public MessageDto(String title,String message,MemberDto member) {
		this(0,title,message,null,member);
	}
	
	public MessageDto cloneWithId(int id) {
		return new MessageDto(id,this.title,this.message,this.postAt,this.member);
	}
	
	public MessageDto cloneWithTitle(String title) {
		return new MessageDto(this.id,title,this.message,this.postAt,this.member);
	}

	public MessageDto cloneWithMessage(String message) {
		return new MessageDto(this.id,this.title,message,this.postAt,this.member);
	}

	
}
