package com.example.demo.infra.locale.translator;

import java.util.Optional;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.example.demo.application.domain.localization.aggregate.entity.Translation;
import com.example.demo.infra.locale.share.enums.TranslationCacheName;
import com.example.demo.infra.persistence.TranslationCategoryRepository;

/**
 * 多語系例外訊息轉譯器（Local Exception Translator）
 *
 * <p>
 * 本元件負責依據「例外訊息代號（messageKey）」與「語系（lang）」， 從資料庫中查詢對應的多語系錯誤訊息，並透過 Spring Cache
 * 將查詢結果快取於 JVM 記憶體中，以避免每次例外發生時重複存取資料庫。
 * </p>
 *
 * <h3>設計說明</h3>
 * <ul>
 * <li>快取層級：JVM In-Memory（Spring Cache）</li>
 * <li>快取 Key：{@code messageKey:lang}</li>
 * <li>適用場景：Exception message（讀多寫極少）</li>
 * </ul>
 *
 * <p>
 * 本元件<strong>不負責</strong>：
 * <ul>
 * <li>訊息組裝（append fields、格式化等）</li>
 * <li>Response DTO 組裝</li>
 * <li>例外攔截與處理流程控制</li>
 * </ul>
 * 上述職責應由 {@code GlobalExceptionHandler} 或 {@code ExceptionMapper} 處理，
 * 以維持單一職責原則（SRP）。
 * </p>
 *
 * <p>
 * 注意：本元件回傳 {@link Optional}，即使查無資料也會被快取， 可避免重複發生相同例外時造成資料庫壓力。
 * </p>
 */
@Component
public class LocaleExceptionMessageTranslator extends AbstractMessageTranslator {

	/**
	 * Super DI Constructor
	 */
	protected LocaleExceptionMessageTranslator(TranslationCategoryRepository translationCategoryRepository) {
		super(translationCategoryRepository);
	}

	/**
	 * 由子類定義對應的 Translation Category Type 例如：SUCCESS_MESSAGE / EXCEPTION_MESSAGE
	 */
	@Override
	protected TranslationCacheName categoryType() {
		return TranslationCacheName.EXCEPTION_MESSAGE;
	}

	/**
	 * 依據例外訊息代號與語系取得對應的多語系錯誤訊息。
	 *
	 * <pre>
	 * 此方法會透過 {@link Cacheable} 將查詢結果快取起來，
	 * 快取 Key 格式為： messageKey:lang
	 *
	 * 若指定的 {@code
	 * messageKey
	 * } 或 {@code
	 * lang
	 * } 查無對應翻譯，
	 * 則回傳 {@link Optional#empty()}，且該結果同樣會被快取，
	 * 以避免重複查詢資料庫。
	 * </pre>
	 *
	 * @param messageKey 多語系例外訊息代號（EXCEPTION_MESSAGE.xxx）
	 * @param lang       語系代碼（例如：zh_TW、en_US）
	 * @return 包含翻譯結果的 {@link Optional}，若查無資料則為 {@link Optional#empty()}
	 */
	@Cacheable(value = "ExceptionMessage", key = "#messageKey + ':' + #lang.toLowerCase()", unless = "#result == null")
	public Optional<Translation> getTranslation(String messageKey, String lang) {
		return this.findTranslation(messageKey, lang);
	}

	@CachePut(value = "ExceptionMessage", key = "#messageKey + ':' + #lang.toLowerCase()", unless = "#result == null")
	public Optional<Translation> refreshCache(String messageKey, String lang, Translation translation) {
		return Optional.ofNullable(translation);
	}

}
