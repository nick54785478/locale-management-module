package com.example.demo.application.service;

import com.example.demo.application.shared.dto.TranslateCategoryGottenResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.application.shared.dto.PagedGottenResult;
import com.example.demo.application.port.TranslationRepositoryPort;

import lombok.AllArgsConstructor;

/**
 * 翻譯分類查詢服務 (Application Query Service)
 *
 * <p>遵循 CQS (Command Query Separation) 原則，負責處理唯讀的多語系翻譯查詢。
 * 不涉及任何業務狀態變更，專注於從 Repository 提取資料並回傳查詢專用的 DTO 模型。</p>
 */
@Service
@AllArgsConstructor
public class TranslationQueryService {

	private final TranslationRepositoryPort translateRepository;

	/**
	 * 根據分類類型與代碼取得單筆多語配置
	 *
	 * @param type 分類類型，例如 "SYSTEM_MESSAGE"
	 * @param code 分類代碼，例如 "SUCCESS"
	 * @return 翻譯分類的查詢結果
	 */
	@Transactional(readOnly = true)
	public TranslateCategoryGottenResult getCategory(String type, String code) {
		return translateRepository.getCategory(type, code);
	}
	
	/**
	 * 分頁查詢多語系分類清單
	 *
	 * @param type 分類類型 (可選，用於過濾)
	 * @param code 分類代碼 (可選，用於過濾)
	 * @param page 頁碼 (0-indexed)
	 * @param size 每頁顯示筆數
	 * @return 包含分頁資訊與翻譯資料的結果物件
	 */
	@Transactional(readOnly = true)
	public PagedGottenResult<TranslateCategoryGottenResult> getPagedCategories(String type, String code, Integer page, Integer size) {
		return translateRepository.getPagedCategories(type, code, page, size);
	}
}
