package com.clab.chatFile.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.clab.chatFile.dao.ChatFileMapper;
import com.clab.chatFile.dto.ChatFileDto;
import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatFileServiceImpl implements ChatFileService {
	
	private final ChatFileMapper mapper;
	
	@Value("${upload.local.dir}")
	private String uploadDir;

	@Override
	public String getFullPath(String fileName) {
		return uploadDir + File.separator + fileName;
	}
	
	@Override
	@Transactional
	public int storeFile(MultipartFile file) {
		String originalFileName = file.getOriginalFilename();
		String saveFileName = generateStoredFileName(originalFileName);
		String source = getFullPath(saveFileName);
		File dest = new File(source);
		
		if(!dest.getParentFile().exists()) {
			boolean created = dest.getParentFile().mkdirs();
			if(!created) {
				log.error("디렉토리 생성 실패: {}", dest.getParentFile().getAbsolutePath());
				throw new CustomException(ErrorCode.CHATFILE_DIRECTORY_ERROR);
			}
		}
		
		try (InputStream inputStream = file.getInputStream()){
			Files.copy(inputStream, dest.toPath(),StandardCopyOption.REPLACE_EXISTING);
		}
		catch(IOException e) {
			log.error("file.transferTo(dest)에서 오류 발생!");
			throw new CustomException(ErrorCode.CHATFILE_SAVED_ERROR);
		}
		
		log.info("파일 저장 성공: {}", source);
		ChatFileDto chatFile = new ChatFileDto(null,originalFileName,saveFileName,source,(int)file.getSize(),null);
		
		int result = mapper.insert(chatFile);
		if(result == 0) {
			throw new CustomException(ErrorCode.CHATFILE_BAD_REQUEST);
		}
		
		int chatFileId = chatFile.getId();
		return chatFileId;
	}


	@Override
	@Transactional
	public int delete(int id) {
		ChatFileDto chatFile = mapper.findById(id);
		if(chatFile==null) {
			throw new CustomException(ErrorCode.CHATFILE_DB_NOT_FOUND);
		}
		try {
			Files.delete(Paths.get(chatFile.getSource()));
		}
		catch(NoSuchFileException e) {
			throw new CustomException(ErrorCode.CHATFILE_NOT_FOUND);
		}
		catch(IOException e) {
			throw new CustomException(ErrorCode.CHATFILE_DELETE_FAILED);
		}
		return mapper.delete(id);
	}
	
	private String generateStoredFileName(String originalFileName) {
		return UUID.randomUUID().toString()+"_"+originalFileName;
	}
}
