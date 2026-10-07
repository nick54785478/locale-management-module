package com.example.demo.application.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.example.demo.infra.persistence.entity.LocaleConfig;

import lombok.AllArgsConstructor;

/**
 * 多語系設定指令服務（Application Command Service）
 *
 * <p>主要負責接收「寫入型」指令（Command），並交由 Repository 執行，隨後刷新快取。</p>
 */
@Service
@AllArgsConstructor
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class TranslationCommandService {

	private TranslationRepositoryPort translateRepository;
	private CacheRefresherRegistryPort refresherRegistry;
	private LocaleConfigRepositoryPort localeConfigRepository;

	/**
	 * 驗證語系代碼是否存在
	 */
	private void validateLocales(Set<String> targetLocales) {
		Set<String> validLocales = localeConfigRepository.findAll().stream()
				.map(LocaleConfig::getCode)
				.collect(Collectors.toSet());

		for (String locale : targetLocales) {
			if (!validLocales.contains(locale)) {
				throw new IllegalArgumentException("未註冊的語系代碼：" + locale);
			}
		}
	}

	/**
	 * 新增一筆多語系設定
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
	 * 更新一筆多語系設定
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
	 * 批次新增多語系設定
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
