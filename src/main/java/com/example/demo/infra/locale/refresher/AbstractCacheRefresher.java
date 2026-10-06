package com.example.demo.infra.locale.refresher;

import com.example.demo.application.port.CacheMangerPort;
import com.example.demo.application.port.CacheRefresherPort;
import com.example.demo.application.shared.payload.TranslationChangedPayload;

/**
 * 抽象快取刷新器（Template Base Class）
 */
public abstract class AbstractCacheRefresher implements CacheRefresherPort {

	protected CacheMangerPort cacheManagerPort;

	public AbstractCacheRefresher(CacheMangerPort cacheManagerPort) {
		this.cacheManagerPort = cacheManagerPort;
	}

	public abstract String getType();

	public abstract String getCacheName();

	@Override
	public void refreshCache(String messageKey, String lang, TranslationChangedPayload payload) {
		if (payload == null) {
			return;
		}

		String cacheKey = messageKey + ":" + lang.toLowerCase();
		cacheManagerPort.put(getCacheName(), cacheKey, payload);
	}
}
