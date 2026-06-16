package com.clab.content_category.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.clab.content_category.dto.ContentCategoryDto;

@Mapper
public interface ContentCategoryMapper {

	ContentCategoryDto findById(int id);

	List<ContentCategoryDto> findByContentId(int contentId);

	int insert(ContentCategoryDto dto);

	int delete(int id);

}
