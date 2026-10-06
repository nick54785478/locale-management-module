package com.example.demo.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.application.shared.command.SaveTranslateCategoryCommand;
import com.example.demo.application.shared.payload.TranslationChangedPayload;
import com.example.demo.application.port.CacheRefresherPort;
import com.example.demo.application.port.CacheRefresherRegistryPort;
import com.example.demo.application.port.TranslationRepositoryPort;

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

	/**
	 * 新增或更新一筆多語系設定
	 */
	public void saveTranslateCategory(SaveTranslateCategoryCommand command) {
		// 將狀態變更交由 Infra 處理，並取得更新後的翻譯 Payload
		List<TranslationChangedPayload> payloads = translateRepository.saveCategory(command);

		// 資料更新完成後，同步刷新快取
		payloads.forEach(payload -> {
			CacheRefresherPort refresher = refresherRegistry.getRefresher(command.getType());
			if (refresher != null) {
				refresher.refreshCache(command.getCode(), payload.getLanguage(), payload);
			}
		});
	}

	/**
	 * 批次新增或更新多筆多語系設定
	 */
	public void saveTranslateCategoryList(List<SaveTranslateCategoryCommand> commands) {
		translateRepository.saveCategoryList(commands);
	}
}
