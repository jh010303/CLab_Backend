package com.clab.common.dto;

import lombok.Getter;

@Getter
public class PageRequestDto {
	private int page = 1;
    private final int size = 10; 
    private String sortBy = "createdAt";
    private String sortOrder = "DESC";
    private String category;

    public void setPage(int page) {
        this.page = Math.max(1, page);
    }

    public void setSortBy(String sortBy) {
        if ("title".equals(sortBy) || "createdAt".equals(sortBy)) {
            this.sortBy = sortBy;
        }
    }

    public void setSortOrder(String sortOrder) {
        if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
            this.sortOrder = sortOrder.toUpperCase();
        }
    }
    
    public void setCategory(String category) {
    	if ("EMOTION".equalsIgnoreCase(category) || "MEETING".equalsIgnoreCase(category)) {
            this.category = category.toUpperCase();
        }
    }

    // offset 계산 로직을 내부에서 처리
    public int getOffset() {
        return (this.page - 1) * this.size;
    }

}
