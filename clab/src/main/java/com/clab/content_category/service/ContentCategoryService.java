package com.clab.content_category.service;

import com.clab.content_category.dto.ContentCategoryDto;

public interface ContentCategoryService {

	ContentCategoryDto findById(int id);

	ContentCategoryDto findByContentId(int contentId);

	int insert(ContentCategoryDto dto);

	void delete(int id);

}
