package com.model_shared.models.pages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedRequestModel {
    private int page;
    private int size;
    private String sortBy;
    private String sortDirection;
    
    public PagedRequestModel(int page, int size) {
        this.page = page;
        this.size = size;
    }
}
