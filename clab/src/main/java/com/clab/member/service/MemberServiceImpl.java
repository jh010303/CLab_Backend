package com.clab.member.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.common.s3.S3Service;
import com.clab.member.dao.MemberMapper;
import com.clab.member.dto.MemberDto;
import com.clab.member.dto.MemberUpdateDto;
import com.clab.member.dto.MemberUpdatePasswordDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberMapper mapper;
	private final PasswordEncoder passwordEncoder;
	private final S3Service s3Service;

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
		MemberDto member = new MemberDto(dto.getId(), dto.getEmail(), encodedPassword, 
				dto.getUsername(), normalizePhone(dto.getPhoneNumber()), dto.getImage(),null,null);

		int changed = mapper.insert(member);
		if (changed == 0) {
			throw new CustomException(ErrorCode.MEMBER_BAD_REQUEST);
		}
	}
	
	@Override
	public void updateImage(int id, MultipartFile image) {
		MemberDto existingMember = mapper.findById(id);
		String imageUrl = existingMember.getImage();
		
		if (imageUrl != null && !imageUrl.isBlank()) {
			s3Service.delete(imageUrl);
		}
		
		String newImageUrl = null;
		
		if(null != image && !image.isEmpty()) {
			String originalFileName = image.getOriginalFilename();
			String saveFileName = UUID.randomUUID() + "_" + originalFileName;

			newImageUrl = s3Service.upload("member/images", image, saveFileName);			
		}
		
		int changed = mapper.updateImage(id, newImageUrl);
		
		if (changed == 0) {
			throw new CustomException(ErrorCode.MEMBER_BAD_REQUEST);
		}
	}

	@Override
	public void updatePassword(int id, MemberUpdatePasswordDto dto) {
		MemberDto existingMember = mapper.findById(id);
		
		if (dto.getOriginPassword() == null || dto.getOriginPassword().isBlank()) {
			throw new CustomException(ErrorCode.MEMBER_PASSWORD_REQUIRED);
		}

		if (!passwordEncoder.matches(dto.getOriginPassword(), existingMember.getPassword())) {
			throw new CustomException(ErrorCode.MEMBER_PASSWORD_MISMATCH);
		}

		String encodedPassword = passwordEncoder.encode(dto.getPassword());
	
		int changed = mapper.updatePassword(id, encodedPassword);
		if (changed == 0) {
			throw new CustomException(ErrorCode.MEMBER_BAD_REQUEST);
		}
	}

	@Override
	public void update(int id, MemberUpdateDto dto) {
		MemberDto existingMember = mapper.findById(id);
		
		MemberDto member = new MemberDto(
				null,
				null,
				null,
				dto.getUsername() == null ? existingMember.getUsername():dto.getUsername(),
				dto.getPhoneNumber() == null ?  existingMember.getPhoneNumber():normalizePhone(dto.getPhoneNumber()),
				null,
				null,
				null
		);

		int changed = mapper.update(id, member);
		if (changed == 0) {
			throw new CustomException(ErrorCode.MEMBER_BAD_REQUEST);
		}
	}

	@Override
	@Transactional
	public void delete(int id) {
		MemberDto existingMember = mapper.findById(id);
		if (existingMember == null) {
			throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
		}

		if (existingMember.getImage() != null && !existingMember.getImage().isBlank()) {
			s3Service.delete(existingMember.getImage());
		}

		int changed = mapper.delete(id);
		if (changed == 0) {
			throw new CustomException(ErrorCode.MEMBER_BAD_REQUEST);
		}
	}

	private String normalizePhone(String phone) {
		return phone == null ? null : phone.replaceAll("-", "");
	}
}
