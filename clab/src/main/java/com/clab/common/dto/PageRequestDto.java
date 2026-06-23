package com.clab.common.dto;

import lombok.Getter;

@Getter
public class PageRequestDto {
	private int page = 1;
    private final int size = 10; 
    private String sortBy = "created_at";
    private String sortOrder = "DESC";

    public void setPage(int page) {
        this.page = Math.max(1, page);
    }

    public void setSortBy(String sortBy) {
        if ("title".equals(sortBy) || "created_at".equals(sortBy)) {
            this.sortBy = sortBy;
        }
    }

    public void setSortOrder(String sortOrder) {
        if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
            this.sortOrder = sortOrder.toUpperCase();
        }
    }

    // offset 계산 로직을 내부에서 처리
    public int getOffset() {
        return (this.page - 1) * this.size;
    }
}
