package com.clab.common.dto;

import lombok.Getter;

@Getter
public class SortRequestDto {
    private String sortBy = "default";
    private String sortOrder = "DESC";

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public void setSortOrder(String sortOrder) {
        if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
            this.sortOrder = sortOrder.toUpperCase();
        }
    }
}