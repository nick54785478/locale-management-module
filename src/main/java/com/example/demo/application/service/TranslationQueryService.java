package com.example.demo.application.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.application.domain.localization.aggregate.TranslationCategory;
import com.example.demo.application.shared.PagedQueriedData;
import com.example.demo.application.shared.TranslateCategoryQueriedData;
import com.example.demo.infra.mapper.TranslationMapper;
import com.example.demo.infra.persistence.TranslationCategoryRepository;
import com.example.demo.infra.spec.GetTranslationSpecification;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TranslationQueryService {

	private final TranslationMapper mapper;
	private final TranslationCategoryRepository translateRepository;

	/**
	 * 取得單筆多語配置
	 *
	 * @param type 多語系類別
	 * @param code 多語系代碼
	 */
	@Transactional
	public TranslateCategoryQueriedData getCategory(String type, String code) {
		TranslationCategory translationCategory = translateRepository.findByTypeAndCode(type, code)
				.orElseGet(TranslationCategory::new);
		return mapper.transformAggregate(translationCategory);

	}
	
	/**
	   * 分頁查詢
	   *
	   * @param type 多語系類別
	   * @param code 多語系代碼
	   * @param page 頁數
	   * @param size 每頁筆數
	   * @return 分頁查詢資料
	   */
	  @Transactional
	  public PagedQueriedData<TranslateCategoryQueriedData> getPagedCategories(String type, String code, Integer page, Integer size) {
	    GetTranslationSpecification specification =
	      new GetTranslationSpecification(type, code);

	    Pageable pageable = PageRequest.of(page, size);
	    Page<TranslationCategory> pagedData = translateRepository.findAll(specification, pageable);
	    // 將 Aggregate 轉成 DTO
	    List<TranslateCategoryQueriedData> content = pagedData.stream()
	      .map(mapper::transformAggregate)
	      .toList();

	    // 回傳 PagedResult
	    return new PagedQueriedData<>(
	      content,
	      pagedData.getTotalElements(),
	      pagedData.getTotalPages(),
	      pagedData.getNumber(),
	      pagedData.getSize()
	    );
	  }


}
