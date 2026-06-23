package com.clab.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberUpdatePasswordDto {
	private String originPassword;
	private String password;
}
