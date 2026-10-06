package com.example.demo.application.port;

/**
 * 快取刷新器註冊中心（Registry）之 Port 定義
 *
 * <p>
 * 此介面定義了「如何依類型取得對應的快取刷新器」的抽象契約， 屬於 Clean Architecture / Hexagonal Architecture
 * 中的 Port。
 * </p>
 */
public interface CacheRefresherRegistryPort {

	/**
	 * 依 Refresher 類型取得對應的快取刷新器
	 *
	 * @param type Refresher 類型識別（例如 SUCCESS_MESSAGE、EXCEPTION_MESSAGE）
	 * @return 對應的 {@link CacheRefresherPort}；若不存在則回傳 {@code null}
	 */
	CacheRefresherPort getRefresher(String type);

}
