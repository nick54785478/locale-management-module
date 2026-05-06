package com.example.demo.application.port;

import com.example.demo.infra.locale.refresher.AbstractCacheRefresher;

/**
 * 快取刷新器註冊中心（Registry）之 Port 定義
 *
 * <p>
 * 此介面定義了「如何依類型取得對應的快取刷新器」的抽象契約， 屬於 Clean Architecture / Hexagonal Architecture
 * 中的 Port。
 * </p>
 *
 * <p>
 * 核心職責：
 * </p>
 *
 * <ul>
 * <li>提供上層（Application / Domain）一個查找 Refresher 的統一入口</li>
 * <li>避免上層直接依賴 Spring、Bean 容器或具體實作</li>
 * <li>支援依「類型」動態選擇不同的快取刷新策略</li>
 * </ul>
 *
 * <p>
 * 在整體架構中的位置：
 * </p>
 *
 * <pre>
 * Application / Domain
 *        ↓
 * CacheRefresherRegistryPort   ←──（本介面）
 *        ↑
 * Infrastructure Adapter（Spring / DI / Map-based Registry）
 * </pre>
 *
 * <p>
 * 實作端（Adapter）可能的形式包括：
 * </p>
 *
 * <ul>
 * <li>Spring Bean Registry（目前使用）</li>
 * <li>測試用的 In-Memory Stub</li>
 * <li>未來可替換為遠端或配置驅動的 Registry</li>
 * </ul>
 *
 * <p>
 * 設計原則：
 * </p>
 *
 * <ul>
 * <li>只關心「查找能力」，不關心建立與管理生命週期</li>
 * <li>不暴露任何框架相關細節</li>
 * <li>允許呼叫端自行決定「查無結果」時的處理策略</li>
 * </ul>
 */
public interface CacheRefresherRegistryPort {

	/**
	 * 依 Refresher 類型取得對應的快取刷新器
	 *
	 * <p>
	 * 呼叫端可依業務情境指定欲操作的訊息類型， 由 Registry 回傳對應的 {@link AbstractCacheRefresher} 實例。
	 * </p>
	 *
	 * <p>
	 * 範例：
	 * </p>
	 *
	 * <pre>
	 * AbstractCacheRefresher refresher = registry.getRefresher("SUCCESS_MESSAGE");
	 *
	 * if (refresher != null) {
	 * 	refresher.refreshCache(key, lang, payload);
	 * }
	 * </pre>
	 *
	 * <p>
	 * 若指定的 type 不存在：
	 * </p>
	 *
	 * <ul>
	 * <li>回傳 {@code null}</li>
	 * <li>由呼叫端自行決定是否拋出例外或忽略</li>
	 * </ul>
	 *
	 * <p>
	 * 建議實務：
	 * </p>
	 *
	 * <ul>
	 * <li>type 應集中定義為 enum 或常數，避免 magic string</li>
	 * <li>關鍵流程可於上層包裝為「必須存在」的取得方法</li>
	 * </ul>
	 *
	 * @param type Refresher 類型識別（例如 SUCCESS_MESSAGE、EXCEPTION_MESSAGE）
	 * @return 對應的 {@link AbstractCacheRefresher}；若不存在則回傳 {@code null}
	 */
	AbstractCacheRefresher getRefresher(String type);

}
