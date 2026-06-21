package com.clab.contentCategory.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.clab.common.exception.CustomException;
import com.clab.common.exception.ErrorCode;
import com.clab.contentCategory.dao.ContentCategoryMapper;
import com.clab.contentCategory.dto.ContentCategoryDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContentCategoryServiceImpl implements ContentCategoryService{
	
	private final ContentCategoryMapper mapper;

	@Override
	public ContentCategoryDto findById(int id) {
		ContentCategoryDto dto = mapper.findById(id);
		if(dto == null) {
			throw new CustomException(ErrorCode.CONTENT_CATEGORY_NOT_FOUND);
		}
		return dto;
	}

	@Override
	public List<ContentCategoryDto> findByContentId(int contentId) {
		List<ContentCategoryDto> dtos = mapper.findByContentId(contentId);
		if(dtos == null) {
			throw new CustomException(ErrorCode.CONTENT_CATEGORY_NOT_FOUND);
		}
		return dtos;
	}

	@Override
	public int insert(ContentCategoryDto dto) {
		int result = mapper.insert(dto);
		if(result == 0) {
			throw new CustomException(ErrorCode.CONTENT_CATEGORY_INSERT_FAILED);
		}
		return dto.getId();
	}

	@Override
	public void delete(int id) {
		int result = mapper.delete(id);
		if(result == 0) {
			throw new CustomException(ErrorCode.CONTENT_CATEGORY_DELETE_FAILED);
		}
	}

}
