package com.example.demo.application.shared.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedGottenResult<T> {
	
    private List<T> content;
    
    private long totalElements;
    
    private int totalPages;
    
    private int page;
    
    private int size;
}