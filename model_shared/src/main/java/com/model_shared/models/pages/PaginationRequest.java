package com.model_shared.models.pages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequest {
    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;
    
    public PaginationRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }
}
