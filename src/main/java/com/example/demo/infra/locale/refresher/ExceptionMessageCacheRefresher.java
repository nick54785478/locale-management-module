package com.example.demo.infra.locale.refresher;

import org.springframework.stereotype.Component;

import com.example.demo.application.port.CacheMangerPort;
import com.example.demo.infra.locale.refresher.AbstractCacheRefresher;

/**
 * 例外訊息（Exception Message）專用的快取刷新器
 */
@Component
public class ExceptionMessageCacheRefresher extends AbstractCacheRefresher {

	public ExceptionMessageCacheRefresher(CacheMangerPort cacheManagerPort) {
		super(cacheManagerPort);
	}

	@Override
	public String getType() {
		return "EXCEPTION_MESSAGE";
	}

	@Override
	public String getCacheName() {
		return "ExceptionMessage";
	}
}
