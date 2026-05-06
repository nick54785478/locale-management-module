package com.example.demo.infra.locale.refresher;

import org.springframework.stereotype.Component;

/**
 * 成功訊息（Success Message）專用的快取刷新器
 *
 * <p>
 * 此 Refresher 負責處理「成功提示訊息」相關的快取更新， 常見使用情境包括：
 * </p>
 *
 * <ul>
 * <li>操作成功提示（例如：新增成功、更新完成）</li>
 * <li>系統回傳給前端顯示的多語系成功訊息</li>
 * </ul>
 *
 * <p>
 * 在整體快取刷新架構中：
 * </p>
 *
 * <ul>
 * <li>繼承 {@link AbstractCacheRefresher}，共用標準快取刷新流程</li>
 * <li>由 Spring 自動掃描並註冊為 Bean</li>
 * <li>可由 Registry 依 {@code SUCCESS_MESSAGE} 類型動態選用</li>
 * </ul>
 *
 * <p>
 * 對應關係：
 * </p>
 *
 * <pre>
 * Type      : SUCCESS_MESSAGE
 * CacheName: SuccessMessage
 * </pre>
 *
 * <p>
 * 此類僅負責定義「類型」與「Cache 名稱」， 所有實際刷新邏輯皆集中於父類，確保一致性。
 * </p>
 */
@Component
public class SuccessMessageCacheRefresher extends AbstractCacheRefresher {

	/**
	 * 回傳此 Refresher 所對應的類型識別
	 *
	 * <p>
	 * 用於區分不同訊息類型的快取刷新策略， 例如成功訊息、例外訊息、驗證訊息等。
	 * </p>
	 *
	 * @return 固定回傳 {@code "SUCCESS_MESSAGE"}
	 */
	@Override
	public String getType() {
		return "SUCCESS_MESSAGE";
	}

	/**
	 * 回傳此 Refresher 所操作的 Cache 名稱
	 *
	 * <p>
	 * 此 Cache 通常用於儲存：
	 * </p>
	 *
	 * <ul>
	 * <li>成功訊息代碼 → 多語系顯示文字</li>
	 * </ul>
	 *
	 * <p>
	 * 必須與 Spring Cache 設定中的 cacheName 完全相同。
	 * </p>
	 *
	 * @return {@code "SuccessMessage"}
	 */
	@Override
	public String getCacheName() {
		return "SuccessMessage";
	}

}
