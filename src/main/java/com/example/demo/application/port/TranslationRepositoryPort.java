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
	 *
	 * @param type 分類類型，例如 "SYSTEM_MESSAGE"
	 * @param code 分類代碼，例如 "SUCCESS"
	 * @return 對應的翻譯分類結果 DTO
	 */
	TranslateCategoryGottenResult getCategory(String type, String code);

	/**
	 * 分頁查詢多語系分類
	 *
	 * @param type 分類類型 (可選，作為過濾條件)
	 * @param code 分類代碼 (可選，作為過濾條件)
	 * @param page 頁碼 (0-indexed)
	 * @param size 每頁筆數
	 * @return 包含分頁資訊與翻譯資料的結果物件
	 */
	PagedGottenResult<TranslateCategoryGottenResult> getPagedCategories(String type, String code, Integer page, Integer size);

	/**
	 * 新增多語系分類
	 *
	 * @param command 封裝了欲新增的分類與翻譯資料 (Outbound Command)
	 * @return 包含異動資訊的 Payload 列表，以便後續觸發快取刷新
	 */
	List<TranslationChangedPayload> createCategory(CreateTranslateCategoryPortCommand command);

	/**
	 * 更新已存在的多語系分類
	 *
	 * @param command 封裝了欲更新的分類與翻譯資料 (Outbound Command)
	 * @return 包含異動資訊的 Payload 列表，以便後續觸發快取刷新
	 */
	List<TranslationChangedPayload> updateCategory(UpdateTranslateCategoryPortCommand command);

	/**
	 * 批次新增多語系分類 (通常用於系統初始化)
	 *
	 * @param commands 欲新增的分類指令清單
	 */
	void createCategoryList(List<CreateTranslateCategoryPortCommand> commands);
}
