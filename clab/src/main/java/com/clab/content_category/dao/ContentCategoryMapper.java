package com.clab.content_category.dao;

import org.apache.ibatis.annotations.Mapper;

import com.clab.content_category.dto.ContentCategoryDto;

@Mapper
public interface ContentCategoryMapper {

	ContentCategoryDto findById(int id);

	ContentCategoryDto findByContentId(int contentId);

	int insert(ContentCategoryDto dto);

	int delete(int id);

}
