package com.example.demo.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.application.port.CacheMangerPort;
import com.example.demo.application.shared.CacheQueriedData;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CacheQueryService {

	private final CacheMangerPort cacheMangeAdapter;

	/**
	 * 取得指定快取
	 */
	public Optional<Object> getCache(String cacheName, String key) {
		return cacheMangeAdapter.get(cacheName, key);
	}

	/**
	 * 取得整個快取內容
	 */
	public CacheQueriedData getAllCache(String cacheName) {
		return cacheMangeAdapter.getAll(cacheName);
	}
}
