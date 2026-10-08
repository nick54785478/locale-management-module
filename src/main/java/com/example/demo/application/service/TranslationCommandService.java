package com.example.demo.application.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.demo.application.shared.exception.LocaleNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.application.shared.command.inbound.CreateTranslateCategoryCommand;
import com.example.demo.application.shared.command.inbound.UpdateTranslateCategoryCommand;
import com.example.demo.application.shared.payload.TranslationChangedPayload;
import com.example.demo.application.port.CacheRefresherPort;
import com.example.demo.application.port.CacheRefresherRegistryPort;
import com.example.demo.application.port.TranslationRepositoryPort;
import com.example.demo.application.port.LocaleConfigRepositoryPort;
import com.example.demo.application.shared.command.outbound.CreateTranslateCategoryPortCommand;
import com.example.demo.application.shared.command.outbound.UpdateTranslateCategoryPortCommand;
import com.example.demo.application.shared.dto.LocaleConfigGottenResult;

import lombok.AllArgsConstructor;

/**
 * 多語系設定指令服務（Application Command Service）
 *
 * <p>主要負責接收「寫入型」指令（Command），並交由 Repository 執行，隨後刷新快取。</p>
 */
@Slf4j
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class TranslationCommandService {

	private TranslationRepositoryPort translateRepository;
	private CacheRefresherRegistryPort refresherRegistry;
	private LocaleConfigRepositoryPort localeConfigRepository;

	/**
	 * 驗證語系代碼是否已註冊
	 * 
	 * <p>確保欲新增或更新的翻譯語系，皆已存在於 LocaleConfig (語系配置) 當中。
	 * 若遇到未註冊的語系代碼，將拋出 LocaleNotFoundException 並中斷寫入操作。</p>
	 * 
	 * @param targetLocales 欲驗證的目標語系代碼集合
	 * @throws LocaleNotFoundException 當任一目標語系代碼不存在時拋出
	 */
	private void validateLocales(Set<String> targetLocales) {
		Set<String> validLocales = localeConfigRepository.findAll().stream()
				.map(LocaleConfigGottenResult::getCode)
				.collect(Collectors.toSet());

		for (String locale : targetLocales) {
			if (!validLocales.contains(locale)) {
				log.error("未註冊的語系代碼：" + locale);
				throw new LocaleNotFoundException();
			}
		}
	}

	/**
	 * 新增一筆多語系翻譯分類
	 * 
	 * <p>接收來自外部的 Inbound Command，驗證其合法性後，將之映射 (Map) 為
	 * Outbound Port Command，並交由 Repository 持久化。持久化完成後，
	 * 動態依據分類類型 (Type) 尋找對應的快取刷新器 (Cache Refresher) 進行快取同步。</p>
	 * 
	 * @param command 包含欲建立之分類及各語系翻譯內容的 Inbound Command
	 */
	public void createTranslateCategory(CreateTranslateCategoryCommand command) {
		Set<String> targetLocales = command.getTranslations().stream()
				.map(t -> t.getLanguage())
				.collect(Collectors.toSet());
		validateLocales(targetLocales);

		// 轉換為 Outbound Port Command
		CreateTranslateCategoryPortCommand portCommand = new CreateTranslateCategoryPortCommand(
			command.getType(),
			command.getCode(),
			command.getDescription(),
			command.getTranslations().stream()
				.map(t -> new CreateTranslateCategoryPortCommand.TranslationPortCommand(t.getLanguage(), t.getTextValue(), t.getRemark()))
				.collect(Collectors.toList())
		);

		// 將狀態變更交由 Infra 處理，並取得更新後的翻譯 Payload
		List<TranslationChangedPayload> payloads = translateRepository.createCategory(portCommand);

		// 資料更新完成後，同步刷新快取
		payloads.forEach(payload -> {
			CacheRefresherPort refresher = refresherRegistry.getRefresher(command.getType());
			if (refresher != null) {
				refresher.refreshCache(command.getCode(), payload.getLanguage(), payload);
			}
		});
	}

	/**
	 * 更新已存在的多語系翻譯分類
	 * 
	 * <p>類似於建立操作，此方法負責驗證並將更新請求映射為 Outbound Port Command。
	 * 在 Repository 更新資料庫狀態後，取得異動的 Payload，並觸發對應的快取刷新機制，
	 * 確保系統各節點的快取資料與資料庫保持一致。</p>
	 * 
	 * @param command 包含欲更新之分類及各語系翻譯內容的 Inbound Command
	 */
	public void updateTranslateCategory(UpdateTranslateCategoryCommand command) {
		Set<String> targetLocales = command.getTranslations().stream()
				.map(t -> t.getLanguage())
				.collect(Collectors.toSet());
		validateLocales(targetLocales);

		// 轉換為 Outbound Port Command
		UpdateTranslateCategoryPortCommand portCommand = new UpdateTranslateCategoryPortCommand(
			command.getType(),
			command.getCode(),
			command.getDescription(),
			command.getTranslations().stream()
				.map(t -> new UpdateTranslateCategoryPortCommand.TranslationPortCommand(t.getLanguage(), t.getTextValue(), t.getRemark()))
				.collect(Collectors.toList())
		);

		// 將狀態變更交由 Infra 處理，並取得更新後的翻譯 Payload
		List<TranslationChangedPayload> payloads = translateRepository.updateCategory(portCommand);

		// 資料更新完成後，同步刷新快取
		payloads.forEach(payload -> {
			CacheRefresherPort refresher = refresherRegistry.getRefresher(command.getType());
			if (refresher != null) {
				refresher.refreshCache(command.getCode(), payload.getLanguage(), payload);
			}
		});
	}

	/**
	 * 批次新增多筆多語系翻譯分類
	 * 
	 * <p>主要用於系統初始化或大量匯入時的場景。對所有的分類及翻譯資料進行統一的
	 * 語系驗證，並轉換為 Port Command 後交由 Repository 進行批次持久化。
	 * 為考量效能，此方法通常不會逐筆觸發快取刷新。</p>
	 * 
	 * @param commands 欲新增的分類指令清單 (Inbound Commands)
	 */
	public void createTranslateCategoryList(List<CreateTranslateCategoryCommand> commands) {
		Set<String> targetLocales = commands.stream()
				.flatMap(cmd -> cmd.getTranslations().stream())
				.map(t -> t.getLanguage())
				.collect(Collectors.toSet());
		validateLocales(targetLocales);

		List<CreateTranslateCategoryPortCommand> portCommands = commands.stream().map(cmd -> 
			new CreateTranslateCategoryPortCommand(
				cmd.getType(),
				cmd.getCode(),
				cmd.getDescription(),
				cmd.getTranslations().stream()
					.map(t -> new CreateTranslateCategoryPortCommand.TranslationPortCommand(t.getLanguage(), t.getTextValue(), t.getRemark()))
					.collect(Collectors.toList())
			)
		).collect(Collectors.toList());

		translateRepository.createCategoryList(portCommands);
	}
}
