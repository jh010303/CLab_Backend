package com.clab.member.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.clab.member.dto.MemberDto;
import com.clab.member.dto.MemberUpdateDto;
import com.clab.member.dto.MemberUpdatePasswordDto;

public interface MemberService {
	List<MemberDto> findAll();
	MemberDto findById(int id);
	void insert(MemberDto dto);
	void updateImage(int id, MultipartFile image);
	void updatePassword(int id, MemberUpdatePasswordDto dto);
	void update(int id, MemberUpdateDto dto);
	void delete(int id);
}
