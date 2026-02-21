package com.DTOs;


import com.domain.enums.SortType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class PaginationAndSortDTO {
	
	
	private SortType sort ;
    @Min(value = 0, message = "number of page >= 0")
	private int page = 0; // الصفحة الافتراضية
    @Min(value = 1, message = "size of page at least  >= 1")
    @Max(value = 100, message = "max size of page is 100")
    private int size = 20; // الحجم الافتراضي
    
	public SortType getSort() {
		return sort;
	}

	public void setSort(SortType sort) {
		this.sort = sort;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
