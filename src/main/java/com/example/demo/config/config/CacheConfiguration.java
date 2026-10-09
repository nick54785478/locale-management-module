package com.example.demo.config.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.example.demo.application.port.CacheMangerPort;
import com.example.demo.infra.adapter.SpringCacheAdapter;
import com.example.demo.infra.adapter.RedisCacheAdapter;

/**
 * 全域快取設定類別 (Cache Configuration)
 *
 * <p>
 * 本配置類啟用 Spring Cache 功能，並透過 {@code @ConditionalOnProperty} 實作雙層快取機制的動態切換。
 * 開發者可透過 `application.properties` 中的 `app.cache.type` 參數，無縫決定系統底層要使用哪一種快取實作，
 * 進而讓 Application 業務層達到「零感知」的境界。
 * </p>
 *
 * <p>
 * 支援的快取類型切換：
 * <ul>
 * <li><b>caffeine (預設)</b>：單機記憶體快取，效能極高。當 {@code app.cache.type=caffeine} 或未設定時生效。</li>
 * <li><b>redis</b>：分散式快取，適合多實例部署環境。當 {@code app.cache.type=redis} 時生效。</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

	/**
	 * 建立 Caffeine 版本的 Spring CacheManager (用於 @Cacheable)
	 *
	 * <p>
	 * 透過 {@code @ConditionalOnProperty(name = "app.cache.type", havingValue = "caffeine", matchIfMissing = true)}
	 * 確保只有在配置為 caffeine (或未填寫) 時，Spring 才會實體化此 Bean。
	 * </p>
	 * 
	 * @return CacheManager Caffeine 的 Cache 管理器
	 */
	@Bean
	@ConditionalOnProperty(name = "app.cache.type", havingValue = "caffeine", matchIfMissing = true)
	public CacheManager caffeineCacheManager() {
		CaffeineCacheManager manager = new CaffeineCacheManager();

		// 註冊 ExceptionMessage 快取，寫入後 60 分鐘失效
		manager.registerCustomCache("ExceptionMessage",
				Caffeine.newBuilder().expireAfterWrite(60, TimeUnit.MINUTES).build());

		// 註冊 SuccessMessage 快取，寫入後 10 分鐘失效
		manager.registerCustomCache("SuccessMessage",
				Caffeine.newBuilder().expireAfterWrite(10, TimeUnit.MINUTES).build());

		return manager;
	}

	/**
	 * 建立 Redis 版本的 Spring CacheManager (用於 @Cacheable)
	 * 
	 * <p>
	 * 透過 {@code @ConditionalOnProperty(name = "app.cache.type", havingValue = "redis")}
	 * 確保只有在明確將快取配置為 redis 時，系統才會註冊此 Bean。
	 * </p>
	 *
	 * @param connectionFactory Redis 連線工廠
	 * @return CacheManager Redis 的 Cache 管理器
	 */
	@Bean
	@ConditionalOnProperty(name = "app.cache.type", havingValue = "redis")
	public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
		RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(java.time.Duration.ofMinutes(60)); // 預設 60 分鐘
		return RedisCacheManager.builder(connectionFactory)
				.cacheDefaults(defaultConfig)
				.build();
	}

	/**
	 * 預設快取適配器：使用 Caffeine 實作 CacheMangerPort
	 * 
	 * <p>
	 * 條件觸發：當 {@code app.cache.type} 為 caffeine 或未設定時。<br>
	 * 將 Spring 的 CacheManager 封裝成我們自定義的 Port 供 Application 層使用。
	 * </p>
	 */
	@Bean
	@ConditionalOnProperty(name = "app.cache.type", havingValue = "caffeine", matchIfMissing = true)
	public CacheMangerPort caffeineCacheAdapter(CacheManager caffeineCacheManager) {
		return new SpringCacheAdapter(caffeineCacheManager);
	}

	/**
	 * 備用快取適配器：使用 Redis 實作 CacheMangerPort
	 * 
	 * <p>
	 * 條件觸發：當 {@code app.cache.type} 為 redis 時。<br>
	 * 將 RedisTemplate 封裝成我們自定義的 Port 供 Application 層使用。
	 * </p>
	 */
	@Bean
	@ConditionalOnProperty(name = "app.cache.type", havingValue = "redis")
	public CacheMangerPort redisCacheAdapter(RedisTemplate<String, Object> redisTemplate) {
		return new RedisCacheAdapter(redisTemplate);
	}
}
