package com.example.demo.application.port;

import java.util.Optional;

import com.example.demo.application.shared.CacheQueriedData;

/**
 * Cache 管理 Port（Application Layer 契約）。
 *
 * <p>
 * 本介面定義 Application Layer 可使用的快取能力， 不暴露任何特定 Cache Provider（如
 * Caffeine、Redis）的實作細節。
 * </p>
 *
 * <p>
 * TTL、eviction policy、key 列舉等進階行為 應由 Infrastructure Adapter 自行決定是否支援。
 * </p>
 */
public interface CacheMangerPort {

	/**
	 * 放入快取。
	 */
	void put(String cacheName, String key, Object value);

	/**
	 * 移除指定 key 的快取資料。
	 */
	void evict(String cacheName, String key);

	/**
	 * 清空指定快取。
	 */
	void clear(String cacheName);

	/**
	 * 依照 Cache 名稱與 key 取得快取值。
	 *
	 * <p>
	 * 回傳 {@link Optional#empty()} 表示：
	 * <ul>
	 * <li>key 不存在</li>
	 * <li>cache 不存在</li>
	 * <li>Adapter 不支援該 cache 實作</li>
	 * </ul>
	 * </p>
	 */
	Optional<Object> get(String cacheName, String key);

	/**
	 * 取得快取內所有 key/value。
	 *
	 * <p>
	 * 是否支援由 Adapter 決定； 若不支援，應回傳空集合。
	 * </p>
	 */
	CacheQueriedData getAll(String cacheName);
}
