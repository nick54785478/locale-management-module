package com.example.demo.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.application.shared.dto.PagedQueriedData;
import com.example.demo.application.shared.dto.TranslateCategoryQueriedData;
import com.example.demo.application.port.TranslationRepositoryPort;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TranslationQueryService {

	private final TranslationRepositoryPort translateRepository;

	/**
	 * 取得單筆多語配置
	 */
	@Transactional(readOnly = true)
	public TranslateCategoryQueriedData getCategory(String type, String code) {
		return translateRepository.getCategory(type, code);
	}
	
	/**
	 * 分頁查詢
	 */
	@Transactional(readOnly = true)
	public PagedQueriedData<TranslateCategoryQueriedData> getPagedCategories(String type, String code, Integer page, Integer size) {
		return translateRepository.getPagedCategories(type, code, page, size);
	}
}
