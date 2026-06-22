package com.clab.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberUpdateDto {
	private String email;
	private String originPassword;
	private String password;
	private String username;
	private String phoneNumber;
	private String image;
}
