package com.clab.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberDto {
	private Integer id;
	private String email;
	private String password;
	private String username;
	private String phoneNumber;
	private String image;
}
