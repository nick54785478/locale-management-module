package com.example.demo.infra.locale.translator;

import java.util.Optional;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.example.demo.application.domain.localization.aggregate.entity.Translation;
import com.example.demo.infra.locale.share.enums.TranslationCacheName;
import com.example.demo.infra.persistence.TranslationCategoryRepository;

/**
 * 多語系成功訊息轉譯器（Local Success Message Translator）
 *
 * <p>
 * 本元件負責依據「成功訊息代號（messageKey）」與「語系（lang）」， 從資料庫中查詢對應的多語系成功訊息，並透過 Spring Cache
 * 將查詢結果快取於 JVM 記憶體中，以避免高頻 API Response 重複查詢資料庫。
 * </p>
 *
 * <h3>設計說明</h3>
 * <ul>
 * <li>快取層級：JVM In-Memory（Spring Cache）</li>
 * <li>快取 Key：{@code messageKey:lang}</li>
 * <li>適用場景：Success message（讀多寫極少）</li>
 * </ul>
 *
 * <p>
 * 本元件<strong>不負責</strong>：
 * <ul>
 * <li>HTTP 狀態碼判斷</li>
 * <li>Response DTO 組裝</li>
 * <li>流程控制或商業邏輯</li>
 * </ul>
 * 上述職責應由 Controller / Service / ResponseBodyAdvice 處理， 以維持單一職責原則（SRP）。
 * </p>
 *
 * <p>
 * 注意：本元件回傳 {@link Optional}，即使查無資料也會被快取， 可避免相同成功訊息在高併發情境下造成資料庫壓力。
 * </p>
 */
@Component
public class LocaleSuccessMessageTranslator extends AbstractMessageTranslator {

	/**
	 * Super DI Constructor
	 */
	protected LocaleSuccessMessageTranslator(TranslationCategoryRepository translationCategoryRepository) {
		super(translationCategoryRepository);
	}

	/**
	 * 由子類定義對應的 Translation Category Type 例如：SUCCESS_MESSAGE / EXCEPTION_MESSAGE
	 */
	@Override
	protected TranslationCacheName categoryType() {
		return TranslationCacheName.SUCCESS_MESSAGE;
	}

	/**
	 * 依據成功訊息代號與語系取得對應的多語系成功訊息。
	 *
	 * <pre>
	 * 快取 Key 格式為： messageKey:lang
	 *
	 * 若查無對應翻譯，則回傳 {@link Optional#empty()}，
	 * 並快取該結果以避免重複查詢。
	 * </pre>
	 *
	 * @param messageKey 成功訊息代號（SUCCESS_MESSAGE.xxx）
	 * @param lang       語系代碼（例如：zh_TW、en_US）
	 * @return 包含翻譯結果的 {@link Optional}，若查無資料則為 {@link Optional#empty()}
	 */
	@Cacheable(value = "SuccessMessage", key = "#messageKey + ':' + #lang.toLowerCase()", unless = "#result == null")
	public Optional<Translation> getTranslation(String messageKey, String lang) {
		return this.findTranslation(messageKey, lang);
	}

	/**
	 * 主動刷新指定成功訊息的快取（通常用於後台修改多語系後）
	 * 
	 * @param messageKey  成功訊息代號
	 * @param lang        語系代碼（例如：zh_tw、en_us）
	 * @param translation 對應的多語系成功訊息
	 */
	@CachePut(value = "SuccessMessage", key = "#messageKey + ':' + #lang.toLowerCase()", unless = "#result == null")
	public Optional<Translation> refreshCache(String messageKey, String lang, Translation translation) {
		return Optional.ofNullable(translation);
	}
}
