package com.example.demo.infra.locale.refresher;

import org.springframework.stereotype.Component;

/**
 * 例外訊息（Exception Message）專用的快取刷新器
 *
 * <p>
 * 此 Refresher 負責處理「例外訊息」相關的快取更新， 通常用於：
 * </p>
 *
 * <ul>
 * <li>系統錯誤訊息（Error Code → 多語系訊息）</li>
 * <li>業務例外（Business Exception）的顯示文字</li>
 * </ul>
 *
 * <p>
 * 在整體架構中：
 * </p>
 *
 * <ul>
 * <li>作為 {@link AbstractCacheRefresher} 的具體實作</li>
 * <li>由 Spring 透過 {@link Component} 自動註冊為 Bean</li>
 * <li>通常會被 Registry / Factory 依 {@code EXCEPTION_MESSAGE} 類型動態取得</li>
 * </ul>
 *
 * <p>
 * 對應關係：
 * </p>
 *
 * <pre>
 * Type      : EXCEPTION_MESSAGE
 * CacheName: ExceptionMessage
 * </pre>
 *
 * <p>
 * 實際的快取刷新流程與 key 規則， 皆由父類 {@link AbstractCacheRefresher} 統一處理。
 * </p>
 */
@Component
public class ExceptionMessageCacheRefresher extends AbstractCacheRefresher {

	/**
	 * 回傳此 Refresher 所對應的類型識別
	 *
	 * <p>
	 * 此值通常用於：
	 * </p>
	 *
	 * <ul>
	 * <li>Refresher Registry 註冊與查找</li>
	 * <li>依訊息類型動態選擇快取刷新策略</li>
	 * </ul>
	 *
	 * <p>
	 * 建議與系統內的 enum 或常數保持一致， 以避免 magic string 與誤用。
	 * </p>
	 *
	 * @return 固定回傳 {@code "EXCEPTION_MESSAGE"}
	 */
	@Override
	public String getType() {
		return "EXCEPTION_MESSAGE";
	}

	/**
	 * 回傳此 Refresher 實際操作的 Cache 名稱
	 *
	 * <p>
	 * 此名稱必須與 Spring Cache 設定中的 cacheName 完全一致， 否則將無法正確取得 Cache 實例。
	 * </p>
	 *
	 * <p>
	 * 此 Cache 通常用於儲存：
	 * </p>
	 *
	 * <ul>
	 * <li>例外代碼 → 多語系錯誤訊息</li>
	 * </ul>
	 *
	 * @return {@code "ExceptionMessage"}
	 */
	@Override
	public String getCacheName() {
		return "ExceptionMessage";
	}

}
