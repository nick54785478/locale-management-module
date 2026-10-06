package com.example.demo.infra.locale.refresher;

import org.springframework.stereotype.Component;

import com.example.demo.application.port.CacheMangerPort;
import com.example.demo.infra.locale.refresher.AbstractCacheRefresher;

/**
 * 成功訊息（Success Message）專用的快取刷新器
 */
@Component
public class SuccessMessageCacheRefresher extends AbstractCacheRefresher {

	public SuccessMessageCacheRefresher(CacheMangerPort cacheManagerPort) {
		super(cacheManagerPort);
	}

	@Override
	public String getType() {
		return "SUCCESS_MESSAGE";
	}

	@Override
	public String getCacheName() {
		return "SuccessMessage";
	}
}
