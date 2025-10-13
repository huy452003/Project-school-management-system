package com.model_shared.models.pages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponseModel<T> {
    private List<T> data;
    private int page; // Trang hiện tại
    private int size; // Số lượng phần tử trên mỗi trang
    private long totalElements; // Tổng số phần tử trong database
    private int totalPages; // Tổng số trang
    private boolean first; // Kiểm tra xem có phải trang đầu tiên không
    private boolean last; // Kiểm tra xem có phải trang cuối cùng không
    private boolean hasNext; // Kiểm tra xem có phải trang tiếp theo không
    private boolean hasPrevious; // Kiểm tra xem có phải trang trước đó không
    
    public PagedResponseModel(List<T> data, int page, int size, long totalElements) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.first = page == 0;
        this.last = page == totalPages - 1;
        this.hasNext = page < totalPages - 1;
        this.hasPrevious = page > 0;
    }
}
