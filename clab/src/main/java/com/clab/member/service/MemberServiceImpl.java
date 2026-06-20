package com.clab.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.member.dao.MemberMapper;
import com.clab.member.dto.MemberDto;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberMapper mapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public List<MemberDto> findAll() {
		return mapper.findAll();
	}

	@Override
	public MemberDto findById(int id) {
		MemberDto member = mapper.findById(id);
		if (member == null) {
			throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
		}
		return member;
	}

	@Override
	public void insert(MemberDto dto) {
		if (mapper.findByEmail(dto.getEmail()) != null) {
			throw new CustomException(ErrorCode.MEMBER_DUPLICATED);
		}
		String encodedPassword = passwordEncoder.encode(dto.getPassword());
		MemberDto member = new MemberDto(dto.getId(), dto.getEmail(), encodedPassword, dto.getUsername(), dto.getImage());

		int changed = mapper.insert(member);
		if (changed == 0) {
			throw new CustomException(ErrorCode.MEMBER_BAD_REQUEST);
		}
	}

	@Override
	public void update(int id, MemberDto dto) {
		String encodedPassword = passwordEncoder.encode(dto.getPassword());
		MemberDto member = new MemberDto(dto.getId(), dto.getEmail(), encodedPassword, dto.getUsername(), dto.getImage());
		int changed = mapper.update(id, member);
		if (changed == 0) {
			throw new CustomException(ErrorCode.MEMBER_BAD_REQUEST);
		}
	}

	@Override
	public void delete(int id) {
		int changed = mapper.delete(id);
		if (changed == 0) {
			throw new CustomException(ErrorCode.MEMBER_BAD_REQUEST);
		}
	}
}
