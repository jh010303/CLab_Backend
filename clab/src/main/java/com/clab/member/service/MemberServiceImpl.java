package com.clab.member.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.member.dao.MemberMapper;
import com.clab.member.dto.MemberDto;
import com.clab.member.dto.MemberUpdateDto;

import lombok.RequiredArgsConstructor;

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
	@Transactional
	public void insert(MemberDto dto) {
		if (mapper.findByEmail(dto.getEmail()) != null) {
			throw new CustomException(ErrorCode.MEMBER_DUPLICATED);
		}
		String encodedPassword = passwordEncoder.encode(dto.getPassword());
		MemberDto member = new MemberDto(dto.getId(), dto.getEmail(), encodedPassword, dto.getUsername(), normalizePhone(dto.getPhoneNumber()), dto.getImage());

		int changed = mapper.insert(member);
		if (changed == 0) {
			throw new CustomException(ErrorCode.MEMBER_BAD_REQUEST);
		}
	}

	@Override
	@Transactional
	public void update(int id, MemberUpdateDto dto) {
		MemberDto existingMember = mapper.findById(id);
		String encodedPassword = existingMember.getPassword();
		
	    if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
	        if (dto.getOriginPassword() == null || dto.getOriginPassword().isBlank()) {
	            throw new CustomException(ErrorCode.MEMBER_PASSWORD_REQUIRED);
	        }

	        if (!passwordEncoder.matches(dto.getOriginPassword(), existingMember.getPassword())) {
	            throw new CustomException(ErrorCode.MEMBER_PASSWORD_MISMATCH);
	        }

	        encodedPassword = passwordEncoder.encode(dto.getPassword());
	    }
		
	    MemberDto member = new MemberDto(
	            id,
	            dto.getEmail() != null ? dto.getEmail() : existingMember.getEmail(),
	            encodedPassword,
	            dto.getUsername() != null ? dto.getUsername() : existingMember.getUsername(),
	            dto.getPhoneNumber() != null ? normalizePhone(dto.getPhoneNumber()) : existingMember.getPhoneNumber(),
	            dto.getImage() != null ? dto.getImage() : existingMember.getImage()
	        );
	    
	    int changed = mapper.update(id, member);
		if (changed == 0) {
			throw new CustomException(ErrorCode.MEMBER_BAD_REQUEST);
		}
	}

	@Override
	@Transactional
	public void delete(int id) {
		int changed = mapper.delete(id);
		if (changed == 0) {
			throw new CustomException(ErrorCode.MEMBER_BAD_REQUEST);
		}
	}
	
	private String normalizePhone(String phone) {
		return phone == null ? null : phone.replaceAll("-", "");
	}
}
