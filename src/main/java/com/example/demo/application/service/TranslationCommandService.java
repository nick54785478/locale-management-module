package com.example.demo.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.application.domain.localization.aggregate.TranslationCategory;
import com.example.demo.application.domain.localization.command.SaveTranslateCategoryCommand;
import com.example.demo.application.port.CacheRefresherRegistryPort;
import com.example.demo.infra.locale.refresher.AbstractCacheRefresher;
import com.example.demo.infra.mapper.TranslationMapper;
import com.example.demo.infra.persistence.TranslationCategoryRepository;

import lombok.AllArgsConstructor;

/**
 * 多語系設定指令服務（Application Command Service）
 *
 * <p>
 * 此服務屬於 Application Layer，主要負責：
 * </p>
 *
 * <ul>
 * <li>接收「寫入型」指令（Command）</li>
 * <li>協調 Repository、Aggregate、Mapper 與 Infrastructure 元件</li>
 * <li>處理交易邊界（Transaction Boundary）</li>
 * </ul>
 *
 * <p>
 * 注意：
 * </p>
 *
 * <ul>
 * <li>此類<strong>不實作業務規則</strong></li>
 * <li>所有狀態變更邏輯皆委派給 {@link TranslationCategory} Aggregate</li>
 * <li>此類負責「流程編排（Orchestration）」</li>
 * </ul>
 *
 * <p>
 * 特別職責：
 * </p>
 *
 * <ul>
 * <li>在多語系資料異動後，同步刷新對應的快取</li>
 * <li>透過 {@link CacheRefresherRegistryPort} 解耦快取實作</li>
 * </ul>
 */
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class TranslationCommandService {

	/**
	 * 多語系分類 Aggregate 的 Repository
	 */
	private TranslationCategoryRepository translateRepository;

	/**
	 * 快取刷新器 Registry（Port）
	 *
	 * <p>
	 * 用於依訊息類型動態取得對應的 Cache Refresher， 避免此 Service 直接依賴 Spring 或具體實作。
	 * </p>
	 */
	private CacheRefresherRegistryPort refresherRegistry;

	/**
	 * 將 Translation Entity 轉換為快取 Payload 的 Mapper
	 */
	private TranslationMapper mapper;

	/**
	 * 新增或更新一筆多語系設定
	 *
	 * <p>
	 * 此方法會：
	 * </p>
	 *
	 * <ol>
	 * <li>依 type + code 查詢既有 {@link TranslationCategory}</li>
	 * <li>若不存在則建立新 Aggregate</li>
	 * <li>交由 Aggregate 執行 apply 行為（狀態變更）</li>
	 * <li>儲存變更結果</li>
	 * <li>同步刷新對應的快取資料</li>
	 * </ol>
	 *
	 * <p>
	 * 快取刷新設計重點：
	 * </p>
	 *
	 * <ul>
	 * <li>僅在資料成功儲存後才進行刷新</li>
	 * <li>依 category type 動態選擇 Refresher</li>
	 * <li>不直接操作 Cache，確保基礎設施解耦</li>
	 * </ul>
	 *
	 * @param command 多語系分類儲存指令
	 */
	public void saveTranslateCategory(SaveTranslateCategoryCommand command) {

		// 嘗試取得既有 Aggregate，若不存在則建立新實例
		TranslationCategory category = translateRepository.findByTypeAndCode(command.getType(), command.getCode())
				.orElseGet(TranslationCategory::new);

		// 將狀態變更交由 Aggregate 決定（避免貧血模型）
		category.apply(command);

		// 儲存 Aggregate（包含 Translations）
		TranslationCategory saved = translateRepository.save(category);

		// === 資料更新完成後，同步刷新快取（關鍵流程） ===
		saved.getTranslations().forEach(translation -> {

			// 透過 Port 依 type 取得對應的 Refresher
			AbstractCacheRefresher refresher = refresherRegistry.getRefresher(saved.getType());

			if (refresher != null) {
				// 將最新翻譯結果寫入快取
				refresher.refreshCache(saved.getCode(), // messageKey
						translation.getLanguage(), // lang
						mapper.transformToPayload(translation) // cache payload
				);
			}
		});
	}

	/**
	 * 批次新增或更新多筆多語系設定
	 *
	 * <p>
	 * 此方法主要用於：
	 * </p>
	 *
	 * <ul>
	 * <li>批次匯入多語系資料</li>
	 * <li>後台管理批量更新</li>
	 * </ul>
	 *
	 * <p>
	 * 流程說明：
	 * </p>
	 *
	 * <ol>
	 * <li>先收集所有指令中的 type 與 code</li>
	 * <li>一次性查詢既有資料，避免 N+1 Query</li>
	 * <li>建立 Map 以利快速對應 Aggregate</li>
	 * <li>逐筆套用 command 至 Aggregate</li>
	 * <li>最後統一批次儲存</li>
	 * </ol>
	 *
	 * <p>
	 * 注意：
	 * </p>
	 *
	 * <ul>
	 * <li>此方法目前<strong>未進行快取刷新</strong></li>
	 * <li>通常搭配初始化或離線同步流程使用</li>
	 * <li>若需即時同步快取，可於此方法補上刷新邏輯</li>
	 * </ul>
	 *
	 * @param commands 多語系分類儲存指令清單
	 */
	@Transactional
	public void saveTranslateCategoryList(List<SaveTranslateCategoryCommand> commands) {

		// 收集所有 type
		Set<String> types = commands.stream().map(SaveTranslateCategoryCommand::getType).collect(Collectors.toSet());

		// 收集所有 code
		Set<String> codes = commands.stream().map(SaveTranslateCategoryCommand::getCode).collect(Collectors.toSet());

		// 一次性查詢所有既有 Aggregate
		List<TranslationCategory> existingCategories = translateRepository.findByTypeInAndCodeIn(types, codes);

		// 建立查找 Map（key = type-code）
		Map<String, TranslationCategory> categoryMap = existingCategories.stream()
				.collect(Collectors.toMap(c -> c.getType() + "-" + c.getCode(), Function.identity()));

		List<TranslationCategory> saveList = new ArrayList<>();

		// 套用每一筆指令
		for (SaveTranslateCategoryCommand command : commands) {
			String key = command.getType() + "-" + command.getCode();

			TranslationCategory category = categoryMap.getOrDefault(key, new TranslationCategory());

			// 所有狀態變更統一由 Aggregate 處理
			category.apply(command);
			saveList.add(category);
		}

		// 批次儲存
		translateRepository.saveAll(saveList);
	}

}
