package com.clab.content_category.service;

import java.util.List;

import com.clab.content_category.dto.ContentCategoryDto;

public interface ContentCategoryService {

	ContentCategoryDto findById(int id);

	List<ContentCategoryDto> findByContentId(int contentId);

	int insert(ContentCategoryDto dto);

	void delete(int id);

}
