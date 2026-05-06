package com.example.demo.config.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 快取設定類別
 *
 * <p>
 * 本配置類啟用 Spring Cache 功能，並使用 Caffeine 作為快取實作。 可針對不同快取類型設定 TTL（Time To Live）。
 * </p>
 *
 * <p>
 * 定義的快取：
 * <ul>
 * <li>{@code ExceptionMessage}：存活 60 分鐘，用於例外訊息快取</li>
 * <li>{@code SuccessMessage}：存活 10 分鐘，用於成功訊息快取</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

	/**
	 * 建立 Spring CacheManager，並註冊 Caffeine 快取
	 *
	 * <p>
	 * 使用 {@link CaffeineCacheManager} 管理快取，並可針對不同快取名稱設定不同 TTL。
	 * </p>
	 *
	 * @return CacheManager Spring Cache 管理器
	 */
	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager manager = new CaffeineCacheManager();

		// 註冊 ExceptionMessage 快取，寫入後 60 分鐘失效
		manager.registerCustomCache("ExceptionMessage",
				Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES).build());

		// 註冊 SuccessMessage 快取，寫入後 10 分鐘失效
		manager.registerCustomCache("SuccessMessage",
				Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build());

		return manager;
	}
}
