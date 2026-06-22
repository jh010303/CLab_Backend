package com.clab.member.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.clab.member.dto.MemberDto;
import com.clab.member.dto.MemberUpdateDto;

public interface MemberService {
	List<MemberDto> findAll();
	MemberDto findById(int id);
	void insert(MemberDto dto);
	void update(int id, MemberUpdateDto dto, MultipartFile image);
	void delete(int id);
}
