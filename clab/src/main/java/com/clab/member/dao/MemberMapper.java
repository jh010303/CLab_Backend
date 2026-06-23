package com.clab.member.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.clab.member.dto.MemberDto;

@Mapper
public interface MemberMapper {

	List<MemberDto> findAll();

	MemberDto findById(int id);
	
	MemberDto findByEmail(String email);

	String findRefreshTokenByEmail(String email);

	int insert(MemberDto dto);

	int update(int id, MemberDto dto);
	
	int updatePassword(int id, String encodedPassword);

	int updateImage(int id, String imageUrl);

	void updateRefreshToken(int id, String refreshToken);
	
	int delete(int id);
}
