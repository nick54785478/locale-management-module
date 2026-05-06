package com.example.demo.application.shared;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedQueriedData <T> {
	
    private List<T> content;
    
    private long totalElements;
    
    private int totalPages;
    
    private int page;
    
    private int size;
}