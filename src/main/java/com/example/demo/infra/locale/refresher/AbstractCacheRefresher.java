package com.example.demo.infra.locale.refresher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.example.demo.infra.locale.share.payload.TranslationChangedPayload;

/**
 * 抽象快取刷新器（Template Base Class）
 *
 * <p>
 * 此類作為所有「快取刷新策略」的共同父類， 封裝了以下通用行為：
 * </p>
 *
 * <ul>
 * <li>透過 {@link CacheManager} 取得對應的 Cache 實例</li>
 * <li>統一定義 cache key 的組合規則（messageKey + lang）</li>
 * <li>提供標準化的快取更新流程（Template Method）</li>
 * </ul>
 *
 * <p>
 * 子類僅需關心：
 * </p>
 *
 * <ul>
 * <li>此 Refresher 所屬的「類型」（{@link #getType()}）</li>
 * <li>實際對應的 Cache 名稱（{@link #getCacheName()}）</li>
 * </ul>
 *
 * <p>
 * 設計目的：
 * </p>
 *
 * <ul>
 * <li>避免各種 Cache 刷新邏輯重複撰寫</li>
 * <li>支援透過 Strategy / Registry 動態選擇 Refresher</li>
 * <li>集中治理 cache key 與 cache 存取規則</li>
 * </ul>
 *
 * <p>
 * 適用情境：
 * </p>
 *
 * <ul>
 * <li>多語系訊息（Success / Exception / Validation Message）</li>
 * <li>由事件或同步流程觸發的快取即時刷新</li>
 * </ul>
 */
public abstract class AbstractCacheRefresher {

	/**
	 * Spring Cache 核心入口
	 *
	 * <p>
	 * 由 Spring 容器注入實際的 {@link CacheManager} 實作， 可能為：
	 * </p>
	 *
	 * <ul>
	 * <li>{@code ConcurrentMapCacheManager}</li>
	 * <li>{@code RedisCacheManager}</li>
	 * <li>{@code CaffeineCacheManager}</li>
	 * </ul>
	 *
	 * <p>
	 * 此抽象層不關心實際快取實作，確保基礎設施可替換。
	 * </p>
	 */
	@Autowired
	protected CacheManager cacheManager;

	/**
	 * 回傳此 Refresher 所對應的類型識別
	 *
	 * <p>
	 * 通常用於 Registry / Factory 依類型動態取得對應的 Refresher， 例如：
	 * </p>
	 *
	 * <pre>
	 * SUCCESS_MESSAGE
	 * EXCEPTION_MESSAGE
	 * VALIDATION_MESSAGE
	 * </pre>
	 *
	 * <p>
	 * 注意：
	 * </p>
	 *
	 * <ul>
	 * <li>此值應具有唯一性</li>
	 * <li>建議使用 enum.name() 或常數定義，避免 magic string</li>
	 * </ul>
	 *
	 * @return Refresher 類型識別字串
	 */
	public abstract String getType();

	/**
	 * 回傳此 Refresher 實際操作的 Cache 名稱
	 *
	 * <p>
	 * 此名稱需與 Spring Cache 設定中的 cacheName 完全一致， 例如：
	 * </p>
	 *
	 * <pre>
	 * SuccessMessage
	 * ExceptionMessage
	 * </pre>
	 *
	 * <p>
	 * {@link #refreshCache(String, String, TranslationChangedPayload)} 會透過此名稱向
	 * {@link CacheManager} 取得 Cache 實例。
	 * </p>
	 *
	 * @return Spring Cache 的名稱
	 */
	public abstract String getCacheName();

	/**
	 * 刷新指定 key 與語系的快取資料
	 *
	 * <p>
	 * 此方法為 Template Method，定義了標準的快取刷新流程：
	 * </p>
	 *
	 * <ol>
	 * <li>檢查 payload 是否為 null（避免覆蓋有效資料）</li>
	 * <li>依照規範組合 cache key（messageKey + ":" + lang）</li>
	 * <li>從 {@link CacheManager} 取得對應 Cache</li>
	 * <li>將新資料寫入快取</li>
	 * </ol>
	 *
	 * <p>
	 * Cache Key 規則：
	 * </p>
	 *
	 * <pre>
	 * {messageKey}:{lang}
	 * 例如：USER_LOGIN_SUCCESS:zh_tw
	 * </pre>
	 *
	 * <p>
	 * 設計考量：
	 * </p>
	 *
	 * <ul>
	 * <li>lang 統一轉為小寫，避免語系 key 不一致</li>
	 * <li>Cache 不存在時安全忽略，不拋例外</li>
	 * </ul>
	 *
	 * @param messageKey 訊息代碼或業務 key（不可為空字串）
	 * @param lang       語系代碼（例如 zh_TW、en_US）
	 * @param payload    欲寫入快取的最新資料，若為 null 則不執行更新
	 */
	public void refreshCache(String messageKey, String lang, TranslationChangedPayload payload) {
		// 無有效資料時，不進行快取更新，避免覆蓋既有內容
		if (payload == null) {
			return;
		}

		// 統一快取 key 格式：{messageKey}:{lang}
		String cacheKey = messageKey + ":" + lang.toLowerCase();

		// 依 cacheName 取得對應的 Cache 實例
		Cache cache = cacheManager.getCache(getCacheName());

		// Cache 存在時才執行更新，避免 NPE
		if (cache != null) {
			cache.put(cacheKey, payload);
		}
	}
}
