package com.example.demo.infra.adapter;

import java.util.Map;
import java.util.Optional;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Component;

import com.example.demo.application.port.CacheMangerPort;
import com.example.demo.application.shared.CacheQueriedData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring Cache 適配器（支援 Caffeine TTL）
 *
 * <p>
 * 提供對 Spring Cache 的封裝操作，支援 put、get、evict、clear 等操作。 針對 CaffeineCache，可直接取得
 * native cache，支援 TTL 與查詢全部 key/value。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SpringCacheAdapter implements CacheMangerPort {

	private final CacheManager cacheManager;

	/**
	 * 放入快取
	 *
	 * @param cacheName 快取名稱
	 * @param key       key
	 * @param value     值
	 */
	@Override
	public void put(String cacheName, String key, Object value) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.put(key, value);
		}
	}

	/**
	 * 移除快取
	 *
	 * @param cacheName 快取名稱
	 * @param key       key
	 */
	@Override
	public void evict(String cacheName, String key) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			cache.evict(key);
		}
	}

	/**
	 * 依照 Cache 名稱與 key 取得快取值
	 *
	 * <p>
	 * 注意：
	 * <ul>
	 * <li>僅支援 CaffeineCache 的 native cache</li>
	 * <li>若快取中是 Optional 物件，會自動 unwrap</li>
	 * </ul>
	 * </p>
	 *
	 * @param cacheName 快取名稱
	 * @param key       key
	 * @return 封裝成 {@link Optional} 的快取值，若不存在或不是 CaffeineCache 則回傳
	 *         {@link Optional#empty()}
	 */
	@Override
	public Optional<Object> get(String cacheName, String key) {
		if (cacheName == null || key == null) {
			return Optional.empty();
		}

		Cache cache = cacheManager.getCache(cacheName);
		if (!(cache instanceof CaffeineCache caffeineCache)) {
			return Optional.empty();
		}
		// 直接從 Caffeine native cache 取值
		Object value = caffeineCache.getNativeCache().getIfPresent(key);
		// 如果快取裡是 Optional<Translation>，自動 unwrap
		if (value instanceof Optional<?> optionalValue) {
			return optionalValue.map(o -> o);
		}
		return Optional.ofNullable(value);
	}

	/**
	 * 取得快取內所有 key/value
	 *
	 * <p>
	 * 注意：
	 * <ul>
	 * <li>僅支援 CaffeineCache 實作</li>
	 * <li>回傳 {@link CacheQueriedData}，包含 cacheName 與每個快取紀錄</li>
	 * </ul>
	 * </p>
	 *
	 * @param cacheName 快取名稱
	 * @return 封裝快取資料的 {@link CacheQueriedData}，若不是 CaffeineCache 則 details 為空
	 */
	@Override
	public CacheQueriedData getAll(String cacheName) {

		CacheQueriedData result = new CacheQueriedData();
		result.setCacheName(cacheName);

		Cache cache = cacheManager.getCache(cacheName);
		if (!(cache instanceof CaffeineCache caffeineCache)) {
			return result;
		}

		Map<Object, Object> cacheMap = caffeineCache.getNativeCache().asMap();

		cacheMap.forEach((k, v) -> {
			log.debug("k = " + k + ", v = " + v);
			CacheQueriedData.CacheMetaData metaData = new CacheQueriedData.CacheMetaData(k.toString(), v);
			result.getDetails().add(metaData);
		});
		return result;
	}

	/**
	 * 清空整個快取
	 *
	 * <p>
	 * 僅對 CaffeineCache 有效，會使該 cache 的所有 key/value 失效
	 * </p>
	 *
	 * @param cacheName 快取名稱
	 */
	@Override
	public void clear(String cacheName) {
		Cache cache = cacheManager.getCache(cacheName);
		if (cache instanceof CaffeineCache caffeineCache) {
			caffeineCache.getNativeCache().invalidateAll();
		}
	}
}
