package com.clab.contentCategory.service;

import java.util.List;

import com.clab.contentCategory.dto.ContentCategoryDto;

public interface ContentCategoryService {

	ContentCategoryDto findById(int id);

	List<ContentCategoryDto> findByContentId(int contentId);

	int insert(ContentCategoryDto dto);

	void delete(int id);

}
