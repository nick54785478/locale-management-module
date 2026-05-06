package com.example.demo.infra.locale.translator;

import java.util.Optional;

import com.example.demo.application.domain.localization.aggregate.entity.Translation;
import com.example.demo.infra.locale.share.enums.TranslationCacheName;
import com.example.demo.infra.persistence.TranslationCategoryRepository;

/**
 * 抽象多語系訊息轉譯模板（Template Method）
 *
 * <p>
 * 負責處理：
 * <ul>
 * <li>語系正規化</li>
 * <li>Translation Repository 查詢流程</li>
 * <li>Optional 回傳語義</li>
 * </ul>
 *
 * <p>
 * 子類只需：
 * <ul>
 * <li>宣告對應的 {@link TranslationCacheName}</li>
 * <li>決定是否暴露 @Cacheable / @CachePut API</li>
 * </ul>
 */
public abstract class AbstractMessageTranslator {

	protected final TranslationCategoryRepository repository;

	protected AbstractMessageTranslator(TranslationCategoryRepository repository) {
		this.repository = repository;
	}

	/**
	 * 子類宣告此 Translator 所屬的快取 / 類別定義
	 */
	protected abstract TranslationCacheName categoryType();

	/**
	 * Template Method：實際查詢多語系翻譯
	 *
	 * <p>
	 * 注意：
	 * <ul>
	 * <li>此方法<strong>不負責快取</strong></li>
	 * <li>快取應由子類透過 Spring Cache Annotation 處理</li>
	 * </ul>
	 */
	protected Optional<Translation> findTranslation(String messageKey, String lang) {
		if (messageKey == null) {
			return Optional.empty();
		}

		// 若語系為 null 則 rollback 為 en_us
		String normalizedLang = (lang == null) ? "en_us" : normalizeLang(lang);
		return repository.findByTypeAndCode(categoryType().getType(), messageKey).flatMap(category -> category
				.getTranslations().stream().filter(t -> t.getLanguage().equalsIgnoreCase(normalizedLang)).findFirst());
	}

	/**
	 * 語系正規化（避免 cache key / DB query 爆炸）
	 *
	 * @param lang 語系
	 */
	protected String normalizeLang(String lang) {
		return lang.toLowerCase();
	}
}
