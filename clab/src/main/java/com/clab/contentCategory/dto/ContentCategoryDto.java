package com.clab.contentCategory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContentCategoryDto {
	private Integer id;
	private Integer contentId;
	private Integer categoryId;
}
