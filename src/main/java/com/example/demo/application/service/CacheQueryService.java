package com.example.demo.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.application.port.CacheMangerPort;
import com.example.demo.application.shared.dto.CacheGottenResult;

import lombok.AllArgsConstructor;

/**
 * 快取查詢服務 (Application Query Service)
 *
 * <p>負責檢視與監控系統內的快取狀態。主要提供唯讀操作，協助維運與除錯，
 * 透過 Outbound Port 與底層的快取機制 (如 Redis、Caffeine) 進行溝通。</p>
 */
@Service
@AllArgsConstructor
public class CacheQueryService {

	private final CacheMangerPort cacheMangeAdapter;

	/**
	 * 取得指定快取名稱中，特定 key 的快取內容
	 *
	 * @param cacheName 快取名稱 (Cache Name)
	 * @param key       快取鍵值 (Cache Key)
	 * @return 包含快取資料的 Optional，若未命中則回傳 Optional.empty()
	 */
	public Optional<Object> getCache(String cacheName, String key) {
		return cacheMangeAdapter.get(cacheName, key);
	}

	/**
	 * 取得指定快取名稱下所有的快取內容 (全量查詢)
	 *
	 * @param cacheName 快取名稱 (Cache Name)
	 * @return 包含該快取名稱下所有 key-value 的查詢結果物件
	 */
	public CacheGottenResult getAllCache(String cacheName) {
		return cacheMangeAdapter.getAll(cacheName);
	}
}
