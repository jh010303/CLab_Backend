package com.clab.chatFile.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.clab.chatFile.dao.ChatFileMapper;
import com.clab.chatFile.dto.ChatFileDto;
import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.common.s3.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatFileServiceImpl implements ChatFileService {

	private final ChatFileMapper mapper;
	private final S3Service s3Service;

	@Override
	@Transactional
	public int storeFile(MultipartFile file) {
		String originalFileName = file.getOriginalFilename();
		String saveFileName = UUID.randomUUID() + "_" + originalFileName;

		String source = s3Service.upload("chat/files", file);

		log.info("S3 파일 업로드 성공: {}", source);
		ChatFileDto chatFile = new ChatFileDto(null, originalFileName, saveFileName, source, (int) file.getSize(), null);

		int result = mapper.insert(chatFile);
		if (result == 0) {
			throw new CustomException(ErrorCode.CHATFILE_BAD_REQUEST);
		}

		return chatFile.getId();
	}

	@Override
	@Transactional
	public int delete(int id) {
		ChatFileDto chatFile = mapper.findById(id);
		if (chatFile == null) {
			throw new CustomException(ErrorCode.CHATFILE_DB_NOT_FOUND);
		}

		s3Service.delete(chatFile.getSource());
		return mapper.delete(id);
	}
}
