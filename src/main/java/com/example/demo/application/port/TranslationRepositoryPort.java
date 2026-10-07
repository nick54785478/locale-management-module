package com.example.demo.application.port;

import java.util.List;

import com.example.demo.application.shared.command.outbound.CreateTranslateCategoryPortCommand;
import com.example.demo.application.shared.command.outbound.UpdateTranslateCategoryPortCommand;
import com.example.demo.application.shared.dto.PagedGottenResult;
import com.example.demo.application.shared.dto.TranslateCategoryGottenResult;
import com.example.demo.application.shared.payload.TranslationChangedPayload;

/**
 * 多語系分類 Repository Port
 *
 * <p>用於讓 Application Layer 進行持久化操作時，不直接依賴 Spring Data JPA 或 Infra 層實作。</p>
 */
public interface TranslationRepositoryPort {

	/**
	 * 取得單筆多語配置
	 */
	TranslateCategoryGottenResult getCategory(String type, String code);

	/**
	 * 分頁查詢多語系分類
	 */
	PagedGottenResult<TranslateCategoryGottenResult> getPagedCategories(String type, String code, Integer page, Integer size);

	/**
	 * 新增或更新多語系分類，並回傳異動的 Payload 以供快取更新
	 */
	List<TranslationChangedPayload> createCategory(CreateTranslateCategoryPortCommand command);

	List<TranslationChangedPayload> updateCategory(UpdateTranslateCategoryPortCommand command);

	/**
	 * 批次新增或更新多語系分類
	 */
	void createCategoryList(List<CreateTranslateCategoryPortCommand> commands);
}
